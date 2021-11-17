package com.nagarro.driven.core.rest.api;

import java.util.Map;

/**
 * Interface for api request.
 *
 * @author nagarro
 */
public interface IApiRequest {

  /**
   * Gets the header of the request.
   *
   * @return map containing header key and value.
   */
  Map<String, String> getHeader();

  /**
   * Gets the request body.
   *
   * @return the request body
   */
  String getPayload();

  /**
   * Gets the url of the request.
   *
   * @return the url of the request
   */
  String getUrl();

  /**
   * Gets the method of the request i.e. GET, POST, PUT or DELETE
   *
   * @return the method of the request i.e. GET, POST, PUT or DELETE
   */
  String getMethod();

  /**
   * Gets the redirect.
   *
   * @return the redirect
   */
  boolean isRedirect();

  /**
   * Sets the redirect as true or false.
   *
   * @param isRedirect true or false
   */
  void setRedirect(boolean isRedirect);

  /**
   * Gets the request set by the user.
   *
   * @return the request set by the user
   */
  <T> T getRequest();
}
