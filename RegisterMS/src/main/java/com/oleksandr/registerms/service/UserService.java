package com.oleksandr.registerms.service;

import com.oleksandr.registerms.dto.LoginRegister.LoginRequestDTO;
import com.oleksandr.registerms.dto.LoginRegister.LoginResponseDTO;
import com.oleksandr.registerms.dto.LoginRegister.RegisterRequestDTO;
import com.oleksandr.registerms.dto.LoginRegister.RegisterResponseDTO;
import com.oleksandr.registerms.entity.RefreshToken;
import com.oleksandr.registerms.entity.TokenPair;
import com.oleksandr.registerms.entity.users.User;
import com.oleksandr.common.dto.AuthUserDTO;
import com.oleksandr.registerms.exception.InvalidPasswordException;
import com.oleksandr.registerms.exception.InvalidTokenException;
import com.oleksandr.registerms.exception.UserNotFoundException;
import com.oleksandr.registerms.jwt.JwtTokenProvider;
import com.oleksandr.registerms.repository.RefreshTokenRepository;
import com.oleksandr.registerms.repository.UserRepository;
import com.oleksandr.registerms.util.UserMapper;
import com.oleksandr.registerms.util.uservalidation.UserLoginValidator;
import com.oleksandr.registerms.util.uservalidation.UserRegisterValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserRegisterValidator userRegisterValidator;
    private final UserLoginValidator userLoginValidator;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;
    private final RefreshTokenRepository refreshTokenRepository;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    private static final int SECONDS_IN_7_DAYS = 604800;
    private static final int SECONDS_1_HOUR = 3600;

    public Mono<RegisterResponseDTO> registerUser(RegisterRequestDTO dto) {
        return userRegisterValidator.validateRequest(dto)
                .then(Mono.defer(() -> {
                    String hashedPassword = passwordEncoder.encode(dto.getPassword());
                    User user = userMapper.mapFromRegisterDTO(dto, hashedPassword);
                    return userRepository.save(user);
                }))
                .doOnSuccess(savedUser -> log.info("User registered: {}", savedUser.getUsername()))
                .flatMap(savedUser -> {
                    String accessToken = jwtTokenProvider.generateToken(savedUser.getId().toString());
                    String refreshToken = UUID.randomUUID().toString();

                    RefreshToken refreshTokenEntity = new RefreshToken(
                            UUID.randomUUID(),               // id
                            refreshToken,
                            savedUser.getId(),
                            Instant.now().plusSeconds(SECONDS_IN_7_DAYS)
                    );

                    return refreshTokenRepository.deleteByUserId(savedUser.getId())
                            .then(r2dbcEntityTemplate.insert(refreshTokenEntity))
                            .thenReturn(new RegisterResponseDTO(accessToken, refreshToken, SECONDS_1_HOUR));
                });
    }

    public Mono<LoginResponseDTO> loginUser(LoginRequestDTO dto) {
        return userLoginValidator.validateRequest(dto)
                .then(userRepository.findByUsername(dto.getUsername())
                        .switchIfEmpty(Mono.error(new UserNotFoundException("User not found")))
                        .flatMap(user -> {
                            if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
                                log.warn("Invalid password for user: {}", dto.getUsername());
                                return Mono.error(new InvalidPasswordException("Invalid password"));
                            }

                            log.info("User successful login: {}", dto.getUsername());
                            String accessToken = jwtTokenProvider.generateToken(user.getId().toString());
                            String refreshToken = UUID.randomUUID().toString();

                            RefreshToken refreshTokenEntity = new RefreshToken(
                                    UUID.randomUUID(),
                                    refreshToken,
                                    user.getId(),
                                    Instant.now().plusSeconds(SECONDS_IN_7_DAYS)
                            );

                            return refreshTokenRepository.deleteByUserId(user.getId())
                                    .then(r2dbcEntityTemplate.insert(refreshTokenEntity))
                                    .thenReturn(new LoginResponseDTO(accessToken, refreshToken, SECONDS_1_HOUR));
                        }));
    }

    public Mono<TokenPair> refreshToken(String refreshTokenValue) {
        if (refreshTokenValue == null || refreshTokenValue.isBlank()) {
            return Mono.error(new InvalidTokenException("Refresh token is empty"));
        }

        return refreshTokenRepository.findByToken(refreshTokenValue)
                .switchIfEmpty(Mono.error(new InvalidTokenException("Refresh token not found")))
                .flatMap(storedToken -> {
                    if (storedToken.getExpiryDate().isBefore(Instant.now())) {
                        return refreshTokenRepository.delete(storedToken)
                                .then(Mono.error(new InvalidTokenException("Refresh token expired")));
                    }

                    UUID userId = storedToken.getUserId();
                    return userRepository.findById(userId)
                            .switchIfEmpty(Mono.error(new UserNotFoundException("User not found")))
                            .flatMap(user -> {
                                String newAccess = jwtTokenProvider.generateToken(user.getId().toString());
                                String newRefresh = UUID.randomUUID().toString();

                                RefreshToken newRefreshToken = new RefreshToken(
                                        UUID.randomUUID(),
                                        newRefresh,
                                        user.getId(),
                                        Instant.now().plusSeconds(SECONDS_IN_7_DAYS)
                                );

                                return refreshTokenRepository.deleteByUserId(user.getId())
                                        .then(r2dbcEntityTemplate.insert(newRefreshToken))
                                        .thenReturn(new TokenPair(newAccess, newRefresh));
                            });
                });
    }

    public Mono<Void> logout(String refreshTokenValue) {
        return refreshTokenRepository.findByToken(refreshTokenValue)
                .flatMap(refreshTokenRepository::delete)
                .then();
    }

    public Mono<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    public Mono<User> updateUser(UUID id, AuthUserDTO userDto) {
        return Mono.empty();
    }
}
