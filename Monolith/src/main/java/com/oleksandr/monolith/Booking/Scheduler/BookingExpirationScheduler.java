package com.oleksandr.monolith.Booking.Scheduler;

import com.oleksandr.monolith.Booking.EntityRepo.BOOKING_STATUS;
import com.oleksandr.monolith.Booking.EntityRepo.Booking;
import com.oleksandr.monolith.Booking.EntityRepo.BookingRepository;
import com.oleksandr.monolith.Coordinator.BookingCoordinator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class BookingExpirationScheduler {

    private final BookingRepository bookingRepository;
    private final BookingCoordinator bookingCoordinator;

    private static final int PAYMENT_TIMEOUT_MINUTES = 15;

    public BookingExpirationScheduler(
            BookingRepository bookingRepository,
            BookingCoordinator bookingCoordinator) {
        this.bookingRepository = bookingRepository;
        this.bookingCoordinator = bookingCoordinator;
    }

    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void cancelExpiredBookings() {
        log.debug("⏰ Starting scheduled task: checking for expired bookings...");

        try {
            LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(PAYMENT_TIMEOUT_MINUTES);

            log.debug("🔍 Looking for bookings with status WAITING_FOR_PAYMENT created before: {}", 
                expirationTime);

            List<Booking> expiredBookings = bookingRepository
                .findByStatusAndCreatedAtBefore(BOOKING_STATUS.WAITING_FOR_PAYMENT, expirationTime);

            if (expiredBookings.isEmpty()) {
                log.debug("✅ No expired bookings found");
                return;
            }

            log.warn("⚠️ Found {} expired booking(s) that need to be canceled", expiredBookings.size());

            int successCount = 0;
            int failCount = 0;

            for (Booking booking : expiredBookings) {
                try {
                    log.info("🚫 Auto-canceling expired booking: {} (created at: {}, age: {} minutes)",
                        booking.getId(),
                        booking.getCreatedAt(),
                        java.time.Duration.between(booking.getCreatedAt(), LocalDateTime.now()).toMinutes()
                    );
                    bookingCoordinator.cancelBooking(booking.getId(), booking.getUser().getId());

                    successCount++;
                    log.info("✅ Successfully auto-canceled booking: {}", booking.getId());

                } catch (Exception e) {
                    failCount++;
                    log.error("❌ Failed to auto-cancel booking {}: {}", 
                        booking.getId(), e.getMessage(), e);
                }
            }

            log.info("📊 Expiration task completed: {} canceled, {} failed, {} total",
                successCount, failCount, expiredBookings.size());

        } catch (Exception e) {
            log.error("❌ Error in booking expiration scheduler", e);
        }
    }

    @Scheduled(fixedDelay = 3600000)
    public void logExpirationStatistics() {
        try {
            LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
            
            long waitingCount = bookingRepository.countByStatus(BOOKING_STATUS.WAITING_FOR_PAYMENT);
            long oldWaitingCount = bookingRepository
                .findByStatusAndCreatedAtBefore(BOOKING_STATUS.WAITING_FOR_PAYMENT, 
                    LocalDateTime.now().minusMinutes(PAYMENT_TIMEOUT_MINUTES))
                .size();

            log.info("📊 Booking Expiration Statistics:");
            log.info("   - Currently WAITING_FOR_PAYMENT: {}", waitingCount);
            log.info("   - Overdue (>15 min): {}", oldWaitingCount);

        } catch (Exception e) {
            log.error("❌ Error generating expiration statistics", e);
        }
    }
}
