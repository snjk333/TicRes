package com.oleksandr.registerms.entity;

import lombok.*;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class TokenPair {
    String accessToken;
    String refreshToken;
}
