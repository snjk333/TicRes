package com.oleksandr.monolith.common.exceptions;

public class ConcurrentUpdateException extends RuntimeException {
  public ConcurrentUpdateException(String msg, Throwable cause) { super(msg, cause); }
}