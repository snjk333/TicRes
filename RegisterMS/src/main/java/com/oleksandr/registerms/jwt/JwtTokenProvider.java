package com.oleksandr.registerms.jwt;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class JwtTokenProvider {

    private final JwtEncoder encoder;
    private final JwtDecoder decoder;
    private final JwtProperties props;

    public JwtTokenProvider(JwtEncoder encoder, JwtDecoder decoder, JwtProperties props) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.props = props;
    }

    public String generateToken(String subject) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(props.getIssuer())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(props.getExpirationSeconds()))
                .subject(subject)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
        Jwt jwt = encoder.encode(JwtEncoderParameters.from(jwsHeader, claims));
        return jwt.getTokenValue();
    }

    public void validateToken(String token) {
        decoder.decode(token);
    }

    public String getSubject(String token) {
        Jwt jwt = decoder.decode(token);
        return jwt.getSubject();
    }
}
