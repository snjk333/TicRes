package com.oleksandr.registerms.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table("refresh_tokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken {
    @Id
    private UUID id;
    private String token;
    private UUID userId;
    private Instant expiryDate;

    public RefreshToken(String token, UUID userId, Instant expiryDate) {
        this.token = token;
        this.userId = userId;
        this.expiryDate = expiryDate;
    }
}

