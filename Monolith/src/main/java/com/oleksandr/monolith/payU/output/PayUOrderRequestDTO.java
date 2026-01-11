package com.oleksandr.monolith.payU.output;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PayUOrderRequestDTO {

    private String continueUrl;

    private String notifyUrl;

    private String extOrderId;

    private Buyer buyer;

    private String customerIp;

    private String merchantPosId;

    private String description;

    private String currencyCode;

    private String totalAmount;

    private List<Product> products;


    @Data
    @Builder
    public static class Buyer {
        private String email;
        private String phone;
        private String firstName;
        private String lastName;
        private String language;
    }

    @Data
    @Builder
    public static class Product {
        private String name;

        private String unitPrice;

        private String quantity;
    }
}