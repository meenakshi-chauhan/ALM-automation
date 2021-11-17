package com.nagarro.driven.core.rest.api;

import java.util.Map;

/**
 * Interface for api response.
 *
 * @author nagarro
 */
public interface IApiResponse {

  /**
   * Gets the response code.
   *
   * @return the response code
   */
  int code();

  /**
   * Gets the response message.
   *
   * @return the response message
   */
  String message();

  /**
   * Gets the response headers.
   *
   * @return the response headers
   */
  Map<String, String> getHeader();

  /**
   * Gets the response body.
   *
   * @return the response body
   */
  String getPayload();

  /**
   * Gets the redirect.
   *
   * @return the redirect
   */
  boolean isRedirect();
}
