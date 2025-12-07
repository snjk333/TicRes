package com.oleksandr.monolith.common.exceptions;

public class TicketNotAvailableException extends RuntimeException {
    public TicketNotAvailableException(String message) {
        super(message);
    }
}
