package com.felipe.communityuploadservice.exceptions;

public class ImageAlreadyExistsException extends RuntimeException {
  public ImageAlreadyExistsException(String message) {
    super(message);
  }
}
