package com.oleksandr.monolith.integration.auth;

import com.oleksandr.common.dto.AuthUserDTO;
import com.oleksandr.monolith.common.exceptions.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
public class AuthClientServiceImpl implements AuthClientService {

    private final WebClient.Builder webClientBuilder;
    private final String authServiceUrl;

    public AuthClientServiceImpl(WebClient.Builder webClientBuilder,
                                  @Value("${auth.service.url}") String authServiceUrl) {
        this.webClientBuilder = webClientBuilder;
        this.authServiceUrl = authServiceUrl;
    }

    @Override
    public AuthUserDTO getUserById(UUID userId) {
        try {
            WebClient webClient = webClientBuilder
                    .baseUrl(authServiceUrl + "/api/users")
                    .build();

            return webClient.get()
                    .uri("/{id}", userId)
                    .retrieve()
                    .onStatus(status -> status.value() == 404,
                            resp -> Mono.error(new ResourceNotFoundException("User not found in Auth service: " + userId)))
                    .bodyToMono(AuthUserDTO.class)
                    .block();

        } catch (WebClientResponseException.NotFound e) {
            throw new ResourceNotFoundException("User not found in Auth service: " + userId);
        } catch (Exception e) {
            log.error("Failed to fetch user {} from Auth MS: {}", userId, e.toString());
            throw new RuntimeException("Auth service unavailable or failed for user: " + userId, e);
        }
    }


    @Override
    public AuthUserDTO updateUser(AuthUserDTO userDto) {
        try {
            WebClient webClient = webClientBuilder
                    .baseUrl(authServiceUrl + "/api/users")
                    .build();

            return webClient
                    .patch()
                    .uri("/{id}", userDto.getId()) // PATCH /api/users/{id}
                    .bodyValue(userDto)            // Send JSON body
                    .retrieve()
                    .bodyToMono(AuthUserDTO.class)     // Expect updated UserDTO
                    .block();                      // Sync call for monolith

        } catch (WebClientResponseException.NotFound e) {
            log.warn("User {} not found in Auth MS while updating", userDto.getId());
            return null;
        } catch (Exception e) {
            log.error("Failed to update user {} in Auth MS: {}", userDto.getId(), e.getMessage());
            return null;
        }
    }
}

//   @Override
//    public UserDTO getUserById(UUID userId) {
//        try {
//            log.debug("Отправляем GET /api/users/{} в Auth MS", userId);
//
//            return authClient()
//                    .get()
//                    .uri("/{id}", userId)
//                    .retrieve()
//                    .bodyToMono(UserDTO.class)
//                    .block(); // синхронный вызов для монолита
//
//        } catch (WebClientResponseException.NotFound e) {
//            log.warn("Пользователь {} не найден в Auth MS", userId);
//            return null;
//        } catch (Exception e) {
//            log.error("Ошибка при запросе к Auth MS: {}", e.getMessage(), e);
//            return null;
//        }
//    }