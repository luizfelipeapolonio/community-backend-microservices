package com.felipe.communityuserservice.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
  public UserAlreadyExistsException(String email) {
    super("Usuário de email '" + email + "' já cadastrado");
  }
}
