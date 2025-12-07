package com.oleksandr.monolith.integration.payU.dto;

import lombok.Data;

@Data
public class PayUOrderResponseDTO {
    private Status status;
    private String redirectUri;
    private String orderId;
    private String extOrderId;

    @Data
    public static class Status {
        private String statusCode;
    }
}