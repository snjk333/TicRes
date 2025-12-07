package com.oleksandr.eventprovider.exception;

import java.util.UUID;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(UUID eventId) {
        super("Event not found with id: " + eventId);
    }
    
    public EventNotFoundException(String message) {
        super(message);
    }
}
