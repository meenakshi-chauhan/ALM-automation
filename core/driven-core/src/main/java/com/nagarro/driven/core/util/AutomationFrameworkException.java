package com.nagarro.driven.core.util;

/**
 * This exception can be thrown for internal framework exceptions
 *
 * @author nagarro
 */
public class AutomationFrameworkException extends RuntimeException {

  private static final long serialVersionUID = 1168711898947724457L;

  /**
   * The constructor for automation framework exception with message.
   *
   * @param message, the exception message.
   */
  public AutomationFrameworkException(String message) {
    super(message);
  }

  /**
   * The constructor for automation framework exception with message and cause.
   *
   * @param message, the exception message
   * @param cause, the exception cause
   */
  public AutomationFrameworkException(String message, Throwable cause) {
    super(message, cause);
  }
}
