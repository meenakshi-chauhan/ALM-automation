package com.nagarro.driven.client.ui.api;

public class UnexpectedClientException extends RuntimeException {

  public UnexpectedClientException(String message) {
    super(message);
  }

  public UnexpectedClientException(String message, Throwable cause) {
    super(message, cause);
  }
}
