package com.oleksandr.monolith.payU.client;

import com.oleksandr.monolith.payU.input.dto.PayUAuthResponseDTO;
import com.oleksandr.monolith.payU.output.PayUOrderRequestDTO;
import com.oleksandr.monolith.payU.input.dto.PayUOrderResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Slf4j
@Service
public class PayUClient {

    private final WebClient webClient;

    @Value("${payu.client.id}")
    private String payuClientId;

    @Value("${payu.merchant.pos.id}")
    private String merchantPosId;

    @Value("${payu.client.secret}")
    private String payuClientSecret;

    public PayUClient(WebClient.Builder webClientBuilder, @Value("${payu.base.url}") String payuBaseUrl) {
        HttpClient httpClient = HttpClient.create().followRedirect(false);
        
        this.webClient = webClientBuilder
                .baseUrl(payuBaseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    public PayUAuthResponseDTO getAccessToken() {
        log.info("Requesting Access Token from PayU...");
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        formData.add("client_id", payuClientId);
        formData.add("client_secret", payuClientSecret);

        try {
            return this.webClient
                    .post()
                    .uri("/pl/standard/user/oauth/authorize")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .onStatus(
                            status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> {
                                log.error("Error response from PayU: {}", response.statusCode());
                                return response.bodyToMono(String.class)
                                        .flatMap(body -> {
                                            log.error("PayU Error Body: {}", body);
                                            return Mono.error(new RuntimeException("PayU auth failed with status: " + response.statusCode()));
                                        });
                            }
                    )
                    .bodyToMono(PayUAuthResponseDTO.class)
                    .block();

        } catch (Exception e) {
            log.error("Failed to get Access Token from PayU: {}", e.getMessage());
            throw new RuntimeException("Could not authenticate with PayU", e);
        }
    }

    public PayUOrderResponseDTO createOrder(PayUOrderRequestDTO orderRequest, String accessToken) {
        log.info("Creating a new order in PayU for description: {}", orderRequest.getDescription());
        orderRequest.setMerchantPosId(this.merchantPosId);

        log.info("📦 PayU Order Request:");
        log.info("  merchantPosId: {}", orderRequest.getMerchantPosId());
        log.info("  customerIp: {}", orderRequest.getCustomerIp());
        log.info("  description: {}", orderRequest.getDescription());
        log.info("  currencyCode: {}", orderRequest.getCurrencyCode());
        log.info("  totalAmount: {}", orderRequest.getTotalAmount());
        log.info("  continueUrl: {}", orderRequest.getContinueUrl());
        log.info("  notifyUrl: {}", orderRequest.getNotifyUrl());
        log.info("  extOrderId: {}", orderRequest.getExtOrderId());
        if (orderRequest.getBuyer() != null) {
            log.info("  buyer.email: {}", orderRequest.getBuyer().getEmail());
            log.info("  buyer.phone: {}", orderRequest.getBuyer().getPhone());
            log.info("  buyer.firstName: {}", orderRequest.getBuyer().getFirstName());
            log.info("  buyer.lastName: {}", orderRequest.getBuyer().getLastName());
            log.info("  buyer.language: {}", orderRequest.getBuyer().getLanguage());
        }
        if (orderRequest.getProducts() != null && !orderRequest.getProducts().isEmpty()) {
            orderRequest.getProducts().forEach(p -> {
                log.info("  product.name: {}", p.getName());
                log.info("  product.unitPrice: {}", p.getUnitPrice());
                log.info("  product.quantity: {}", p.getQuantity());
            });
        }

        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            String requestJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(orderRequest);
            log.info("📤 Full JSON Request Body:\n{}", requestJson);
        } catch (Exception e) {
            log.warn("Could not serialize request to JSON: {}", e.getMessage());
        }

        try {
            return this.webClient
                    .post()
                    .uri("/api/v2_1/orders")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .bodyValue(orderRequest)
                    .exchangeToMono(response -> {
                        if (response.statusCode().is3xxRedirection()) {
                            log.info("✅ PayU returned redirect (status {}), extracting response", response.statusCode());
                            return response.bodyToMono(PayUOrderResponseDTO.class);
                        }
                        else if (response.statusCode().isError()) {
                            return response.bodyToMono(String.class).flatMap(errorBody -> {
                                log.error("❌ Failed to create PayU order. Status: {}, Body: {}", response.statusCode(), errorBody);
                                return Mono.error(new RuntimeException("Failed to create PayU order: " + errorBody));
                            });
                        }
                        else {
                            return response.bodyToMono(PayUOrderResponseDTO.class);
                        }
                    })
                    .block();

        } catch (Exception e) {
            log.error("An unexpected error occurred while creating PayU order: {}", e.getMessage(), e);
            throw new RuntimeException("Could not create order in PayU", e);
        }
    }

}