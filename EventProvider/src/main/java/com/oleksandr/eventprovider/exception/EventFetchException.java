package com.oleksandr.eventprovider.exception;

public class EventFetchException extends RuntimeException {
    
    public EventFetchException(String message) {
        super(message);
    }
    
    public EventFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}
