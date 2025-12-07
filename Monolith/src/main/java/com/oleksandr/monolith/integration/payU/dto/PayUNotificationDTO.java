package com.oleksandr.monolith.integration.payU.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor; // <--- Импортируем
import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayUNotificationDTO {

    private Order order;
    private String localReceiptDateTime;
    private List<Property> properties;

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Order {
        private String orderId;
        private String extOrderId;
        private String orderCreateDate;
        private String notifyUrl;
        private String customerIp;
        private String merchantPosId;
        private String description;
        private String currencyCode;
        private String totalAmount;
        private String status; // <-- Важное поле
        private Buyer buyer;
        private PayMethod payMethod; // Добавлено
        private List<Product> products;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Buyer {
        private String customerId;
        private String email;
        private String phone;
        private String firstName;
        private String lastName;
        private String language;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PayMethod {
        private String amount;
        private String type;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Product {
        private String name;
        private String unitPrice;
        private String quantity;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Property {
        private String name;
        private String value;
    }
}