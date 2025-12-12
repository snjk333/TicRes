package com.oleksandr.eventprovider.common.exceptions;

import java.util.UUID;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(UUID eventId) {
        super("Event not found with id: " + eventId);
    }
    
    public EventNotFoundException(String message) {
        super(message);
    }
}
