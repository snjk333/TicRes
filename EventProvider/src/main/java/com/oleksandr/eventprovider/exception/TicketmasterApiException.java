package com.oleksandr.eventprovider.exception;

import lombok.Getter;

@Getter
public class TicketmasterApiException extends RuntimeException {

    private int statusCode;
    private String responseBody;

    public TicketmasterApiException(String message, int statusCode, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public TicketmasterApiException(String ticketmasterApiReturnedEmptyResponse) {
        super(ticketmasterApiReturnedEmptyResponse);
    }

    public TicketmasterApiException(String s, Exception e) {
        super(s, e);
    }
}
