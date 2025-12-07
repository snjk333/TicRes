package com.oleksandr.monolith.common.exceptions;

/**
 * Exception thrown when a resource exists in DB but could not be properly mapped to DTO.
 * RuntimeException, so no need to declare in method signature.
 */
public class ResourceCorruptedException extends RuntimeException {

    public ResourceCorruptedException(String message) {
        super(message);
    }

    public ResourceCorruptedException(String message, Throwable cause) {
        super(message, cause);
    }
}
