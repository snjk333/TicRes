package com.oleksandr.registerms.controller;

import com.oleksandr.registerms.dto.LoginRegister.LoginRequestDTO;
import com.oleksandr.registerms.dto.LoginRegister.LoginResponseDTO;
import com.oleksandr.registerms.dto.LoginRegister.RegisterRequestDTO;
import com.oleksandr.registerms.dto.LoginRegister.RegisterResponseDTO;
import com.oleksandr.registerms.entity.TokenPair;
import com.oleksandr.registerms.entity.users.UserDTO;
import com.oleksandr.registerms.service.BlacklistService;
import com.oleksandr.registerms.service.MonolithNotificationService;
import com.oleksandr.registerms.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {

    private final UserService userService;
    private final BlacklistService blacklistService;
    private final MonolithNotificationService monolithNotificationService;
    private final boolean SecureValue = false;

    @PostMapping("/register")
    public Mono<ResponseEntity<RegisterResponseDTO>> register(@Valid @RequestBody RegisterRequestDTO dto) {
        return userService.registerUser(dto)
                .map(response -> {
                    ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", response.getRefreshToken())
                            .httpOnly(true)
                            .secure(SecureValue)
                            .path("/api/users/refresh")
                            .maxAge(7 * 24 * 60 * 60) // 7 days
                            .sameSite("Strict")
                            .build();

                    return ResponseEntity.status(HttpStatus.CREATED)
                            .header("Set-Cookie", refreshCookie.toString())
                            .body(response);
                });
    }


    @PostMapping("/login")
    public Mono<ResponseEntity<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO dto) {
        return userService.loginUser(dto)
                .map(response -> {
                    ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", response.getRefreshToken())
                            .httpOnly(true)
                            .secure(SecureValue)
                            .path("/api/users/refresh")
                            .maxAge(7 * 24 * 60 * 60)
                            .sameSite("Strict")
                            .build();

                    return ResponseEntity.ok()
                            .header("Set-Cookie", refreshCookie.toString())
                            .body(response);
                });
    }


    @PostMapping("/refresh")
    public Mono<ResponseEntity<TokenPair>> refreshToken(@CookieValue(value = "refreshToken") String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        }

        return userService.refreshToken(refreshToken)
                .map(newTokens -> {
                    ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newTokens.getRefreshToken())
                            .httpOnly(true)
                            .secure(SecureValue)
                            .path("/api/users/refresh")
                            .sameSite("Strict")
                            .maxAge(7 * 24 * 60 * 60)
                            .build();

                    return ResponseEntity.ok()
                            .header("Set-Cookie", refreshCookie.toString())
                            .body(newTokens);
                })
                .onErrorResume(ex -> {
                    log.warn("Refresh token error: {}", ex.getMessage());
                    return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
                });
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Object>> logout(
            @CookieValue(value = "refreshToken") String refreshToken,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (refreshToken == null || refreshToken.isBlank()) {
            return Mono.just(ResponseEntity.noContent().build());
        }

        String accessToken = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
        }

        String finalAccessToken = accessToken;

        return userService.logout(refreshToken)
                .then(Mono.defer(() -> {
                    if (finalAccessToken != null && !finalAccessToken.isBlank()) {
                        UUID userId = extractUserIdFromToken(finalAccessToken);

                        return blacklistService.addToBlacklist(finalAccessToken, userId, 3600)
                                .then(monolithNotificationService.notifyBlacklist(finalAccessToken));
                    }
                    return Mono.empty();
                }))
                .thenReturn(ResponseEntity.noContent()
                        .header("Set-Cookie", ResponseCookie.from("refreshToken", "")
                                .httpOnly(true)
                                .secure(SecureValue)
                                .path("/api/users/refresh")
                                .sameSite("Strict")
                                .maxAge(0)
                                .build().toString())
                        .build())
                .onErrorResume(ex -> {
                    log.error("Error during logout: {}", ex.getMessage());
                    return Mono.just(ResponseEntity.noContent()
                            .header("Set-Cookie", ResponseCookie.from("refreshToken", "")
                                    .httpOnly(true)
                                    .secure(SecureValue)
                                    .path("/api/users/refresh")
                                    .sameSite("Strict")
                                    .maxAge(0)
                                    .build().toString())
                            .build());
                });
    }


    private UUID extractUserIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                return UUID.randomUUID();
            }

            String payload = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            int subIndex = payload.indexOf("\"sub\":\"");
            if (subIndex >= 0) {
                int start = subIndex + 7;
                int end = payload.indexOf("\"", start);
                String uuidStr = payload.substring(start, end);
                return UUID.fromString(uuidStr);
            }
        } catch (Exception e) {
            log.warn("Failed to extract userId from token: {}", e.getMessage());
        }
        return UUID.randomUUID();
    }


    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserDTO>> getUserById(@PathVariable("id") UUID id) {
        return userService.findById(id)
                .map(user -> UserDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .build())
                .map(userDto -> ResponseEntity.ok().body(userDto))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(ex -> {
                    log.error("Error while fetching user id={}: {}", id, ex.getMessage(), ex);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

    @PatchMapping("/{id}")
    public Mono<ResponseEntity<UserDTO>> updateUser(@PathVariable("id") UUID id, @RequestBody UserDTO userDto) {
        return userService.updateUser(id, userDto)
                .map(user -> UserDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .build())
                .map(userReturnDto -> ResponseEntity.ok().body(userDto))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .onErrorResume(ex -> {
                    log.error("Error while fetching user id={}: {}", id, ex.getMessage(), ex);
                    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
                });
    }

}
