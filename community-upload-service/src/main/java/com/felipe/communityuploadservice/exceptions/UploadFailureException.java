package com.felipe.communityuploadservice.exceptions;

public class UploadFailureException extends RuntimeException {
  public UploadFailureException(String message, Throwable cause) {
    super(message, cause);
  }
}
