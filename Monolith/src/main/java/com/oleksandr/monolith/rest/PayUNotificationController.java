package com.oleksandr.monolith.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oleksandr.monolith.Coordinator.BookingCoordinator;
import com.oleksandr.monolith.integration.payU.PayUSignatureVerifier;
import com.oleksandr.monolith.integration.payU.ProcessedPayUNotification;
import com.oleksandr.monolith.integration.payU.ProcessedPayUNotificationRepository;
import com.oleksandr.monolith.integration.payU.dto.PayUNotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/payu")
public class PayUNotificationController {

    private final BookingCoordinator bookingCoordinator;
    private final PayUSignatureVerifier signatureVerifier;
    private final ProcessedPayUNotificationRepository processedNotificationRepository;

    public PayUNotificationController(
            BookingCoordinator bookingCoordinator,
            PayUSignatureVerifier signatureVerifier,
            ProcessedPayUNotificationRepository processedNotificationRepository) {
        this.bookingCoordinator = bookingCoordinator;
        this.signatureVerifier = signatureVerifier;
        this.processedNotificationRepository = processedNotificationRepository;
    }

    @Transactional
    @PostMapping("/notifications")
    public ResponseEntity<Void> handlePayUNotification(
            @RequestHeader(value = "OpenPayu-Signature", required = false) String signatureHeader,
            @RequestBody String rawRequestBody) {
        
        log.info("======================================================");
        log.info("           RECEIVED NOTIFICATION FROM PAYU!           ");
        log.info("======================================================");
        
        try {
            log.info("🔐 Step 1: Verifying PayU signature...");
            
            String signature = signatureVerifier.extractSignature(signatureHeader);
            boolean isSignatureValid = signatureVerifier.verifySignature(signature, rawRequestBody);
            
            if (!isSignatureValid) {
                log.error("❌ SECURITY ALERT: Invalid PayU signature detected!");
                log.error("❌ Signature header: {}", signatureHeader);
                log.error("❌ This could be a fraudulent webhook attempt!");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            
            log.info("✅ Signature verification PASSED - webhook is authentic");
            
            log.info("📦 Step 2: Parsing notification...");
            
            PayUNotificationDTO notification;
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                notification = objectMapper.readValue(rawRequestBody, PayUNotificationDTO.class);
            } catch (Exception e) {
                log.error("❌ Failed to parse notification JSON", e);
                return ResponseEntity.badRequest().build();
            }
            
            log.info("🔍 Notification object: {}", notification);
            log.info("🔍 Notification is null? {}", notification == null);
            if (notification != null) {
                log.info("🔍 Order is null? {}", notification.getOrder() == null);
                log.info("🔍 Order object: {}", notification.getOrder());
            }

            if (notification == null || notification.getOrder() == null) {
                log.error("❌ Notification does not contain order information!");
                log.error("❌ notification == null: {}", notification == null);
                if (notification != null) {
                    log.error("❌ notification.getOrder() == null: {}", notification.getOrder() == null);
                }
                return ResponseEntity.badRequest().build();
            }

            PayUNotificationDTO.Order order = notification.getOrder();
            String extOrderId = order.getExtOrderId();
            String status = order.getStatus();
            String payuOrderId = order.getOrderId();

            log.info("📦 Order ID (PayU): {}", payuOrderId);
            log.info("📦 External Order ID (Booking): {}", extOrderId);
            log.info("📊 Payment Status: {}", status);
            log.info("💰 Total Amount: {} {}", order.getTotalAmount(), order.getCurrencyCode());

            log.info("🔍 Step 3: Checking for duplicate notifications...");
            
            if (processedNotificationRepository.existsByPayuOrderId(payuOrderId)) {
                log.warn("⚠️ DUPLICATE NOTIFICATION DETECTED!");
                log.warn("⚠️ PayU Order {} was already processed", payuOrderId);
                log.warn("⚠️ Skipping duplicate webhook processing");
                return ResponseEntity.ok().build();
            }
            
            log.info("✅ No duplicate found - proceeding with processing");

            if (extOrderId == null || extOrderId.isEmpty()) {
                log.error("❌ extOrderId is null or empty");
                return ResponseEntity.badRequest().build();
            }

            UUID bookingId;
            try {
                bookingId = UUID.fromString(extOrderId);
            } catch (IllegalArgumentException e) {
                log.error("❌ Invalid UUID format for extOrderId: {}", extOrderId);
                return ResponseEntity.badRequest().build();
            }

            switch (status) {
                case "COMPLETED":
                    log.info("✅ Payment COMPLETED for booking {}", bookingId);
                    handleCompletedPayment(bookingId, order, payuOrderId);
                    break;

                case "PENDING":
                    log.info("⏳ Payment PENDING for booking {}", bookingId);
                    break;

                case "WAITING_FOR_CONFIRMATION":
                    log.info("⏳ Payment WAITING_FOR_CONFIRMATION for booking {}", bookingId);
                    break;

                case "CANCELED":
                    log.warn("❌ Payment CANCELED for booking {}", bookingId);
                    handleCanceledPayment(bookingId, order);
                    break;

                default:
                    log.warn("⚠️ Unknown payment status: {} for booking {}", status, bookingId);
            }

            log.info("======================================================");

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            log.error("❌ Error processing PayU notification", e);
            return ResponseEntity.ok().build();
        }
    }

    private void handleCompletedPayment(UUID bookingId, PayUNotificationDTO.Order order, String payuOrderId) {
        try {
            log.info("💳 Processing completed payment for booking: {}", bookingId);
            log.info("💳 PayU Order ID: {}", payuOrderId);

            var booking = bookingCoordinator.getBookingDetails(bookingId);
            UUID userId = booking.user().id();
            
            log.info("💰 Step 2: Verifying payment amount...");
            
            String receivedAmountStr = order.getTotalAmount();
            long receivedAmount = Long.parseLong(receivedAmountStr);
            
            double ticketPricePLN = booking.ticket().price();
            long expectedAmount = (long) (ticketPricePLN * 100);
            
            log.info("💰 Expected amount: {} groszy ({} PLN)", expectedAmount, ticketPricePLN);
            log.info("💰 Received amount: {} groszy ({} PLN)", receivedAmount, receivedAmount / 100.0);
            
            if (receivedAmount != expectedAmount) {
                log.error("❌ PAYMENT AMOUNT MISMATCH!");
                log.error("❌ Expected: {} groszy, Received: {} groszy", expectedAmount, receivedAmount);
                log.error("❌ Difference: {} groszy", Math.abs(expectedAmount - receivedAmount));
                throw new IllegalStateException(
                    String.format("Payment amount mismatch: expected %d, received %d", 
                        expectedAmount, receivedAmount)
                );
            }
            
            log.info("✅ Payment amount verification PASSED");
            
            log.info("👤 User ID from booking: {}", userId);
            log.info("🎫 Completing booking for user {} and booking {}", userId, bookingId);

            var completedBooking = bookingCoordinator.completeBooking(bookingId, userId);
            
            log.info("✅ Booking {} successfully completed!", bookingId);
            log.info("📊 New booking status: {}", completedBooking.status());
            log.info("💳 PayU Order {} processed successfully", payuOrderId);
            
            log.info("💾 Step 4: Saving processed notification record...");
            ProcessedPayUNotification processedNotification = ProcessedPayUNotification.builder()
                .payuOrderId(payuOrderId)
                .bookingId(bookingId)
                .paymentStatus("COMPLETED")
                .amount(Long.parseLong(order.getTotalAmount()))
                .build();
            
            processedNotificationRepository.save(processedNotification);
            log.info("✅ Notification record saved - duplicate protection active");
            
        } catch (Exception e) {
            log.error("❌ Failed to complete booking {}: {}", bookingId, e.getMessage(), e);
            throw new RuntimeException("Failed to complete booking after payment", e);
        }
    }

    private void handleCanceledPayment(UUID bookingId, PayUNotificationDTO.Order order) {
        try {
            log.info("🚫 Processing canceled payment for booking: {}", bookingId);

            var booking = bookingCoordinator.getBookingDetails(bookingId);

            UUID userId = booking.user().id();
            
            log.info("👤 User ID from booking: {}", userId);
            log.info("❌ Canceling booking for user {} and booking {}", userId, bookingId);

            var canceledBooking = bookingCoordinator.cancelBooking(bookingId, userId);
            
            log.info("✅ Booking {} successfully canceled", bookingId);
            log.info("📊 New booking status: {}", canceledBooking.status());
            
        } catch (Exception e) {
            log.error("❌ Failed to cancel booking {}: {}", bookingId, e.getMessage(), e);
        }
    }

}