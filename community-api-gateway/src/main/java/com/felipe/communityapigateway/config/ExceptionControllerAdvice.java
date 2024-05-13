package com.felipe.communityapigateway.config;

import com.felipe.communityapigateway.exceptions.AuthValidationException;
import com.felipe.communityapigateway.exceptions.MissingAuthException;
import com.felipe.communityapigateway.utils.CustomResponseBody;
import com.felipe.communityapigateway.utils.ResponseConditionStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionControllerAdvice {

  @ExceptionHandler(MissingAuthException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public CustomResponseBody<Void> handleMissingAuthException(MissingAuthException e) {
    CustomResponseBody<Void> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.ERROR);
    response.setCode(HttpStatus.UNAUTHORIZED);
    response.setMessage(e.getMessage());
    response.setData(null);
    return response;
  }

  @ExceptionHandler(AuthValidationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public String handleAuthValidationException(AuthValidationException e) {
    return e.getMessage();
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public CustomResponseBody<Void> handleUncaughtException() {
    CustomResponseBody<Void> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.ERROR);
    response.setCode(HttpStatus.INTERNAL_SERVER_ERROR);
    response.setMessage("Ocorreu um erro interno do servidor");
    response.setData(null);
    return response;
  }
}