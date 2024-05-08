package com.felipe.communityuserservice.controllers;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.felipe.communityuserservice.exceptions.UserAlreadyExistsException;
import com.felipe.communityuserservice.utils.response.CustomResponseBody;
import com.felipe.communityuserservice.utils.response.ResponseConditionStatus;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionControllerAdvice {

  @ExceptionHandler(UserAlreadyExistsException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public CustomResponseBody<Void> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
    CustomResponseBody<Void> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.ERROR);
    response.setCode(HttpStatus.CONFLICT);
    response.setMessage(e.getMessage());
    response.setData(null);
    return response;
  }

  @ExceptionHandler(BadCredentialsException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public CustomResponseBody<Void> handleBadCredentialsException() {
    CustomResponseBody<Void> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.ERROR);
    response.setCode(HttpStatus.UNAUTHORIZED);
    response.setMessage("Usuário ou senha inválidos");
    response.setData(null);
    return response;
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public CustomResponseBody<Void> handleUsernameNotFoundException(UsernameNotFoundException e) {
    CustomResponseBody<Void> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.ERROR);
    response.setCode(HttpStatus.UNAUTHORIZED);
    response.setMessage(e.getMessage());
    response.setData(null);
    return response;
  }

  @ExceptionHandler(JWTCreationException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public CustomResponseBody<Void> handleJWTCreationException() {
    CustomResponseBody<Void> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.ERROR);
    response.setCode(HttpStatus.INTERNAL_SERVER_ERROR);
    response.setMessage("Ocorreu um erro interno do servidor");
    response.setData(null);
    return response;
  }
}
