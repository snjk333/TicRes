package com.oleksandr.monolith.payU.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "processed_payu_notifications", 
       indexes = @Index(name = "idx_payu_order_id", columnList = "payu_order_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessedPayUNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "payu_order_id", unique = true, nullable = false, length = 100)
    private String payuOrderId;

    @Column(name = "booking_id", nullable = false)
    private UUID bookingId;

    @Column(name = "payment_status", length = 50)
    private String paymentStatus;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    @PrePersist
    protected void onCreate() {
        if (processedAt == null) {
            processedAt = LocalDateTime.now();
        }
    }
}
