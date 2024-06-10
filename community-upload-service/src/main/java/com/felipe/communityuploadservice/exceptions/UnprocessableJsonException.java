package com.felipe.communityuploadservice.exceptions;

public class UnprocessableJsonException extends RuntimeException {
  public UnprocessableJsonException(String message, Throwable cause) {
    super(message, cause);
  }
}
