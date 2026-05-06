package com.kruosant.bookwalker.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends ResponseStatusException {
  public ConflictException(String message) {
    super(HttpStatus.CONFLICT, message);
  }
}
