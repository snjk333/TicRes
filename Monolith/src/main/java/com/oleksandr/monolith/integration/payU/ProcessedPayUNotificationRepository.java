package com.oleksandr.monolith.integration.payU;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProcessedPayUNotificationRepository extends JpaRepository<ProcessedPayUNotification, UUID> {
    

    boolean existsByPayuOrderId(String payuOrderId);
    
    ProcessedPayUNotification findByPayuOrderId(String payuOrderId);
}
