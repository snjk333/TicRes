package com.oleksandr.monolith.configuration.Security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import java.util.UUID;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final UUID userId;
    private final String token;

    public JwtAuthenticationToken(UUID userId, String token) {
        super(null);
        this.userId = userId;
        this.token = token;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }
}
