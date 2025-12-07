package com.oleksandr.common.constants;

/**
 * Common constants used across all microservices
 */
public final class CommonConstants {
    
    private CommonConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    // API versioning
    public static final String API_V1 = "/api/v1";
    
    // Headers
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    
    // Date formats
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DATE_FORMAT = "yyyy-MM-dd";
}
