package com.felipe.communityapigateway.exceptions;

public class AuthValidationException extends RuntimeException {
  public AuthValidationException(String body) {
    super(body);
  }
}
