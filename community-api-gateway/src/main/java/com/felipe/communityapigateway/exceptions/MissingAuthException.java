package com.felipe.communityapigateway.exceptions;

public class MissingAuthException extends RuntimeException {
  public MissingAuthException() {
    super("Autenticação é necessária para acessar este recurso");
  }
}
