package com.oleksandr.registerms.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("blacklisted_tokens")
public class BlacklistedToken {

    @Id
    private UUID id;

    @Column("token")
    private String token;

    @Column("user_id")
    private UUID userId;

    @Column("blacklisted_at")
    private Instant blacklistedAt;

    @Column("expires_at")
    private Instant expiresAt;
}
