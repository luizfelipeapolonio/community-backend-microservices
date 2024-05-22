package com.felipe.communityuploadservice.controllers;

import com.felipe.communityuploadservice.exceptions.InvalidFileTypeException;
import com.felipe.communityuploadservice.utils.response.CustomResponseBody;
import com.felipe.communityuploadservice.utils.response.ResponseConditionStatus;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionControllerAdvice {

  @ExceptionHandler(InvalidFileTypeException.class)
  @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
  public CustomResponseBody<Void> handleInvalidFileTypeException(InvalidFileTypeException e) {
    CustomResponseBody<Void> response = new CustomResponseBody<>();
    response.setStatus(ResponseConditionStatus.ERROR);
    response.setCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    response.setMessage(e.getMessage());
    response.setData(null);
    return response;
  }
}
