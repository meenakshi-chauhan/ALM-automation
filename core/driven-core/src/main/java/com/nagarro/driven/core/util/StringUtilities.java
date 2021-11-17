package com.nagarro.driven.core.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/** utility methods for string operations, checks, etc. */
public class StringUtilities {
  private StringUtilities() {}
  private static final String COMMA = ",";
  private static final String EQUAL_TO = "=";
  private static final String CURLY_OPEN = "{";
  private static final String CURLY_CLOSE = "}";


  /** checks whether all values are non null and nonempty string. Throws an exception otherwise. */
  public static void checkIfEmpty(String[] values) {
    if (values == null) {
      throw new NullPointerException("The input array cannot be null");
    }

    for (String value : values) {
      if (StringUtils.isEmpty(value)) {
        throw new IllegalArgumentException("No parameter might be null or empty.");
      }
    }
  }

  public static String prettyPrintExceptionTrace(Throwable throwable){
    return Arrays.stream(throwable.getStackTrace())
            .map(StackTraceElement::toString)
            .collect(Collectors.joining("\n"));
  }
  public static String mapEntriesToString(Map<String, ?> map) {
    return map.entrySet().stream()
            .map(entry -> entry.getKey() + EQUAL_TO + entry.getValue())
            .collect(Collectors.joining(COMMA, CURLY_OPEN, CURLY_CLOSE));
  }
  public static Map<String, String> entriesStringToMap(String mapAsString) {
    return Arrays.stream(mapAsString.split(COMMA))
            .map(entry -> entry.split(EQUAL_TO))
            .collect(Collectors.toMap(entry -> entry[0], entry -> entry[1]));
  }
}
