package com.oleksandr.monolith.blacklist;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "blacklisted_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "token", nullable = false, unique = true, length = 1000)
    private String token;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "blacklisted_at", nullable = false)
    private Instant blacklistedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
}
