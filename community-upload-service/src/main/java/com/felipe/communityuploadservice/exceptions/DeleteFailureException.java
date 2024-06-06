package com.felipe.communityuploadservice.exceptions;

public class DeleteFailureException extends RuntimeException {
  public DeleteFailureException(String message, Throwable cause) {
    super(message, cause);
  }
}
