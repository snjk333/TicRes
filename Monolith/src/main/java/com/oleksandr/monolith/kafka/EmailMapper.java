package com.oleksandr.monolith.kafka;

import com.oleksandr.common.enums.MAIL_TYPE;
import com.oleksandr.common.notification.NotificationRequest;
import com.oleksandr.common.notification.UserForMailDTO;
import com.oleksandr.monolith.booking.model.Booking;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EmailMapper {
    public NotificationRequest buildPurchaseConfirmMail(Booking completedBooking){
        UserForMailDTO userForMail = new UserForMailDTO(
                completedBooking.getUser().getFirstName(),
                completedBooking.getUser().getEmail()
        );
        MAIL_TYPE type = MAIL_TYPE.TICKET_PURCHASE;

        Map<String, String> properties = new HashMap<>();
        properties.put("eventName", completedBooking.getTicket().getEvent().getName());
        properties.put("ticketType", completedBooking.getTicket().getType());
        properties.put("location", completedBooking.getTicket().getEvent().getLocation());
        properties.put("place", completedBooking.getTicket().getPlace());
        properties.put("ticketPrice", String.valueOf(completedBooking.getTicket().getPrice()));
        properties.put("date", String.valueOf(completedBooking.getTicket().getEvent().getEventDate()));

        return new NotificationRequest(
                userForMail,
                type,
                properties
        );
    }
}
