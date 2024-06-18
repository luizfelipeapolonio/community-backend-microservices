package com.felipe.community_post_service.controllers;

import com.felipe.community_post_service.exceptions.RecordNotFoundException;
import com.felipe.community_post_service.util.response.CustomResponseBody;
import com.felipe.community_post_service.util.response.CustomValidationErrors;
import com.felipe.community_post_service.util.response.ResponseConditionStatus;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ExceptionControllerAdvice {

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

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public CustomResponseBody<List<CustomValidationErrors>> handleConstraintViolationException(ConstraintViolationException e) {
    List<CustomValidationErrors> errors = e.getConstraintViolations()
      .stream()
      .map(constraintViolation -> {
        String field = constraintViolation.getPropertyPath().toString().split("\\.")[2];
        return new CustomValidationErrors(
          field,
          constraintViolation.getInvalidValue(),
          constraintViolation.getMessage()
        );
      })
      .toList();

    CustomResponseBody<List<CustomValidationErrors>> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.ERROR);
    response.setCode(HttpStatus.BAD_REQUEST);
    response.setMessage("Erros de validação");
    response.setData(errors);
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
