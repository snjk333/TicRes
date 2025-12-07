package com.oleksandr.registerms.jwt;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Configuration;

import javax.crypto.spec.SecretKeySpec;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

@Configuration
public class JwtConfig {

    private final JwtProperties props;

    public JwtConfig(JwtProperties props) {
        this.props = props;
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        SecretKeySpec secretKey =
                new SecretKeySpec(
                        props.getSecretKey().getBytes(),
                        MacAlgorithm.HS256.getName());
        JWK jwk = new OctetSequenceKey.Builder(secretKey)
                .algorithm(com.nimbusds.jose.JWSAlgorithm.HS256)
                .build();
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(
                new JWKSet(jwk));

        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        SecretKeySpec secretKey =
                new SecretKeySpec(
                        props.getSecretKey().getBytes(),
                        MacAlgorithm.HS256.getName());

        return NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}
