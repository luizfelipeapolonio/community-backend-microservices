package com.felipe.community_post_service.exceptions;

public class RecordNotFoundException extends RuntimeException {
  public RecordNotFoundException(String message) {
    super(message);
  }
}
