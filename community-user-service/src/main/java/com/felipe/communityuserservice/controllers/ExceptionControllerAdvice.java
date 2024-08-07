package com.felipe.communityuserservice.controllers;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.felipe.communityuserservice.exceptions.RecordNotFoundException;
import com.felipe.communityuserservice.exceptions.UnprocessableJsonException;
import com.felipe.communityuserservice.exceptions.UserAlreadyExistsException;
import com.felipe.communityuserservice.utils.response.CustomResponseBody;
import com.felipe.communityuserservice.utils.response.CustomValidationErrors;
import com.felipe.communityuserservice.utils.response.ResponseConditionStatus;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

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

  @ExceptionHandler(RecordNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public CustomResponseBody<Void> handleRecordNotFoundException(RecordNotFoundException e) {
    CustomResponseBody<Void> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.ERROR);
    response.setCode(HttpStatus.NOT_FOUND);
    response.setMessage(e.getMessage());
    response.setData(null);
    return response;
  }

  @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public CustomResponseBody<Void> handleAuthenticationException(AuthenticationException e) {
    CustomResponseBody<Void> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.ERROR);
    response.setCode(HttpStatus.UNAUTHORIZED);
    response.setMessage(e.getMessage());
    response.setData(null);
    return response;
  }

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public CustomResponseBody<Void> handleAccessDeniedException(AccessDeniedException e) {
    CustomResponseBody<Void> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.ERROR);
    response.setCode(HttpStatus.FORBIDDEN);
    response.setMessage(e.getMessage());
    response.setData(null);
    return response;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public CustomResponseBody<List<CustomValidationErrors>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
    List<CustomValidationErrors> errors = e.getBindingResult()
      .getFieldErrors()
      .stream()
      .map(fieldError -> new CustomValidationErrors(
        fieldError.getField(),
        fieldError.getField().equalsIgnoreCase("password") ? "" : fieldError.getRejectedValue(),
        fieldError.getDefaultMessage()
      ))
      .toList();

    CustomResponseBody<List<CustomValidationErrors>> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.ERROR);
    response.setCode(HttpStatus.UNPROCESSABLE_ENTITY);
    response.setMessage("Erros de validação");
    response.setData(errors);
    return response;
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public CustomResponseBody<List<CustomValidationErrors>> handleConstraintViolationException(ConstraintViolationException e) {
    List<CustomValidationErrors> errors = e.getConstraintViolations()
      .stream()
      .map(constraintViolation -> {
        String field = constraintViolation.getPropertyPath().toString().split("\\.")[2];
        return new CustomValidationErrors(
          field,
          field.equals("password") ? "" : constraintViolation.getInvalidValue(),
          constraintViolation.getMessage()
        );
      })
      .toList();

    CustomResponseBody<List<CustomValidationErrors>> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.ERROR);
    response.setCode(HttpStatus.BAD_REQUEST);
    response.setMessage("Erro ao validar parâmetros");
    response.setData(errors);
    return response;
  }

  @ExceptionHandler(JWTCreationException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public CustomResponseBody<Void> handleJWTCreationException(JWTCreationException e) {
    CustomResponseBody<Void> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.ERROR);
    response.setCode(HttpStatus.INTERNAL_SERVER_ERROR);
    response.setMessage(e.getMessage());
    response.setData(null);
    return response;
  }

  @ExceptionHandler(JWTVerificationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public CustomResponseBody<Void> handleJWTVerificationException(JWTVerificationException e) {
    CustomResponseBody<Void> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.ERROR);
    response.setCode(HttpStatus.UNAUTHORIZED);
    response.setMessage(e.getMessage());
    response.setData(null);
    return response;
  }

  @ExceptionHandler(UnprocessableJsonException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public CustomResponseBody<Void> handleUnprocessableJsonException(UnprocessableJsonException e) {
    CustomResponseBody<Void> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.ERROR);
    response.setCode(HttpStatus.UNPROCESSABLE_ENTITY);
    response.setMessage(e.getMessage());
    response.setData(null);
    return response;
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
