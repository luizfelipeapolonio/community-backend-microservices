package com.felipe.communityuploadservice.exceptions;

public class RecordNotFoundException extends RuntimeException {
  public RecordNotFoundException(String message) {
    super(message);
  }
}
