package com.kruosant.bookwalker.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> handleException(MethodArgumentNotValidException ex) {
    StringBuilder errorStringBuilder = new StringBuilder();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errorStringBuilder.append(fieldName);
      errorStringBuilder.append(" ");
      errorStringBuilder.append(errorMessage);
      errorStringBuilder.append(", ");
    });
    return ResponseEntity.status(ex.getStatusCode()).body(error(ex.getStatusCode(), errorStringBuilder.toString()));
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<?> handleException(ResourceNotFoundException ex) {
    return ResponseEntity.status(ex.getStatusCode()).body(error(ex.getStatusCode(), "Not found"));
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<?> handleException(HttpRequestMethodNotSupportedException ex) {
    return ResponseEntity.status(ex.getStatusCode()).body(error(ex.getStatusCode(), "Method not allowed"));
  }


  private Map<String, Object> error(HttpStatusCode status, String message) {
    return Map.of(
        "timestamp", LocalDateTime.now(),
        "status", status.value(),
        "error", message
    );
  }
}



