package com.oleksandr.eventprovider.common.exceptions;

public class EventFetchException extends RuntimeException {
    
    public EventFetchException(String message) {
        super(message);
    }
    
    public EventFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
