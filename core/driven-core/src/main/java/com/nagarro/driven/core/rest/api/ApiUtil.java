package com.nagarro.driven.core.rest.api;

import com.google.gson.Gson;

/**
 * Api util contains generic methods for api response conversion.
 *
 * @author nagarro
 */
public class ApiUtil {

  private ApiUtil(){}

  /**
   * Converts java object to json.
   *
   * @param object the java object
   * @return string of json
   */
  public static String toJson(final Object object) {
    final Gson gson = new Gson();
    return gson.toJson(object);
  }

  /**
   * Converts json to specified java object.
   *
   * @param json the json to be converted
   * @param object the java class in whict it need to be converted
   * @return the java object
   */
  public static <T> T fromJson(final String json, final Class<T> object) {
    final Gson gson = new Gson();
    return gson.fromJson(json, object);
  }
}
