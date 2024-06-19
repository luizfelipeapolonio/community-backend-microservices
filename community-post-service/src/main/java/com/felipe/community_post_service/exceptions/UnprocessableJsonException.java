package com.felipe.community_post_service.exceptions;

public class UnprocessableJsonException extends RuntimeException {
  public UnprocessableJsonException(String message, Throwable cause) {
    super(message, cause);
  }
}
