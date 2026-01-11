package com.oleksandr.monolith.coordinator;

import com.oleksandr.common.dto.TicketDTO;
import com.oleksandr.common.enums.BOOKING_STATUS;
import com.oleksandr.common.enums.TICKET_STATUS;
import com.oleksandr.common.notification.NotificationRequest;
import com.oleksandr.monolith.booking.input.dto.BookingDetailsDTO;
import com.oleksandr.monolith.booking.input.dto.BookingSummaryDTO;
import com.oleksandr.monolith.booking.mapper.BookingMapper;
import com.oleksandr.monolith.booking.model.Booking;
import com.oleksandr.monolith.booking.service.api.BookingService;
import com.oleksandr.monolith.common.exceptions.BookingAccessDeniedException;
import com.oleksandr.monolith.integration.wrapper.reservationIntegration.ReserveService;
import com.oleksandr.monolith.kafka.EmailMapper;
import com.oleksandr.monolith.kafka.KafkaProducer;
import com.oleksandr.monolith.payU.client.PayUClient;
import com.oleksandr.monolith.payU.input.dto.PayUAuthResponseDTO;
import com.oleksandr.monolith.payU.input.dto.PayUOrderResponseDTO;
import com.oleksandr.monolith.payU.output.PayUOrderRequestDTO;
import com.oleksandr.monolith.ticket.Service.api.TicketService;
import com.oleksandr.monolith.ticket.mapper.TicketMapper;
import com.oleksandr.monolith.user.Service.api.UserService;
import com.oleksandr.monolith.user.mapper.UserMapper;
import com.oleksandr.monolith.user.model.User;
import com.oleksandr.monolith.user.output.dto.UserSummaryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingCoordinator {

    private final UserService userService;
    private final TicketService ticketService;
    private final BookingService bookingService;

    private final ReserveService reserveService;


    private final BookingMapper bookingMapper;
    private final UserMapper userMapper;
    private final TicketMapper ticketMapper;

    private final PayUClient payUClient;

    //kafka
    private final EmailMapper emailMapper;
    private final KafkaProducer kafkaProducer;

    @Value("${payu.notify.base.url}")
    private String notifyBaseUrl;

    @Value("${app.frontend.url}")
    private String frontendUrlFromProperties;

    private static final String PAYU_NOTIFICATION_PATH = "/monolith/api/payu/notifications";


    @Transactional
    public BookingSummaryDTO createBooking(UUID userId, UUID ticketId) {
        var user = userService.getOrCreateUser(userId);
        var ticket = ticketService.reserveTicket(ticketId);
        var booking = bookingService.createBooking(user, ticket);

        reserveService.sendBookingCreation(ticketId, booking.getId());
        return bookingMapper.mapToSummaryDto(booking);
    }

    @Transactional
    public BookingSummaryDTO cancelBooking(UUID bookingId, UUID userId) {
        var booking = bookingService.findById(bookingId);

        if(booking.getTicket().getStatus().equals(TICKET_STATUS.SOLD)){
            throw new BookingAccessDeniedException("You can't cancel booking with sold ticket");
        }

        if(booking.getStatus().equals(BOOKING_STATUS.WAITING_FOR_PAYMENT)){
            throw new BookingAccessDeniedException("You can't booking ticket while payment");
        }

        if (!booking.getUser().getId().equals(userId))
            throw new BookingAccessDeniedException("User id its not equals to booking's user id");

        ticketService.markAvailable(booking.getTicket());
        var cancelled = bookingService.cancelBooking(booking);

        reserveService.sendBookingCancel(booking.getTicket().getId(), booking.getId());
        return bookingMapper.mapToSummaryDto(cancelled);
    }


    @Transactional
    public BookingSummaryDTO completeBooking(UUID bookingId, UUID userId) {
        var booking = bookingService.findById(bookingId);

        if (!booking.getUser().getId().equals(userId))
            throw new BookingAccessDeniedException("User id its not equals to booking's user id");

        ticketService.markSold(booking.getTicket());
        var completed = bookingService.completeBooking(booking);

        reserveService.sendBookingComplete(booking.getTicket().getId(), booking.getId());

        NotificationRequest request = emailMapper.buildPurchaseConfirmMail(completed);
        kafkaProducer.sendMessage(request);

        return bookingMapper.mapToSummaryDto(completed);
    }

    public BookingDetailsDTO getBookingDetails(UUID id) {
        var booking = bookingService.findById(id);
        var user = userService.getOrCreateUser(booking.getUser().getId());
        var ticket = booking.getTicket();

        UserSummaryDTO userSummaryDTO = userMapper.mapToSummaryDto(user);
        TicketDTO ticketDTO = ticketMapper.mapToDto(ticket);

        return BookingDetailsDTO
                .builder()
                .id(booking.getId())
                .user(userSummaryDTO)
                .ticket(ticketDTO)
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .version(booking.getVersion())
                .build();
    }


    public List<BookingSummaryDTO> getUserBookings(UUID userID) {
        List<Booking> bookingsList = bookingService.getBookingsByUser(userID);
        return bookingMapper.mapListToSummaryListDto(bookingsList);
    }

    @Transactional
    public String initiatePayment(UUID bookingId, UUID userId, String customerIp) {
        log.info("Initiating payment for booking: {} by user: {}", bookingId, userId);

        Booking booking = bookingService.findById(bookingId);
        User user = booking.getUser();
        var ticket = booking.getTicket();

        if (!user.getId().equals(userId)) {
            log.warn("Access denied for user {} trying to pay for booking {}", userId, bookingId);
            throw new BookingAccessDeniedException("User is not authorized to pay for this booking");
        }
        booking.setStatus(BOOKING_STATUS.WAITING_FOR_PAYMENT);
        PayUAuthResponseDTO authToken = payUClient.getAccessToken();

        log.info("Successfully got PayU token");

        String totalAmount = String.valueOf(((int) (ticket.getPrice()*100)));

        PayUOrderRequestDTO.Buyer buyerDto = PayUOrderRequestDTO.Buyer.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName() != null ? user.getFirstName() : user.getUsername())
                .lastName(user.getLastName() != null ? user.getLastName() : "User")
                .phone(user.getPhoneNumber() != null ? user.getPhoneNumber() : "123456789")
                .language("pl")
                .build();
        PayUOrderRequestDTO.Product productDto = PayUOrderRequestDTO.Product.builder()
                .name("Ticket to: " + ticket.getEvent().getName())
                .unitPrice(totalAmount)
                .quantity("1")
                .build();
        String fullNotifyUrl;

        if (notifyBaseUrl.contains("webhook.site")) {
            fullNotifyUrl = notifyBaseUrl;
            log.info("Using webhook.site for testing notifications: {}", fullNotifyUrl);
        } else {
            fullNotifyUrl = notifyBaseUrl + PAYU_NOTIFICATION_PATH;
            log.info("Using custom webhook URL: {}", fullNotifyUrl);
        }
        
        PayUOrderRequestDTO orderRequest = PayUOrderRequestDTO.builder()
                .customerIp(customerIp)
                .extOrderId(booking.getId().toString())
                .description("Ticket reservation: " + ticket.getEvent().getName())
                .currencyCode("PLN")
                .totalAmount(totalAmount)
                .buyer(buyerDto)
                .products(List.of(productDto))
                .notifyUrl(fullNotifyUrl)
                .continueUrl(frontendUrlFromProperties + "/payment/success")
                .build();

        PayUOrderResponseDTO orderResponse = payUClient.createOrder(orderRequest, authToken.getAccessToken());
        log.info("PayU order created with ID: {}", orderResponse.getOrderId());

        return orderResponse.getRedirectUri();
    }
}