package com.oleksandr.monolith.common.exceptions;

public class BookingConflictException extends RuntimeException {
  public BookingConflictException(String message, Throwable cause) {
    super(message, cause);
  }
  public BookingConflictException(String message) {
    super(message);
  }
}
