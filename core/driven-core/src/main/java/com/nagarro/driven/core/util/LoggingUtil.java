package com.nagarro.driven.core.util;

import org.slf4j.Logger;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Arrays.asList;

public class LoggingUtil {
  private LoggingUtil() {
    // Utility class
  }

  public static void executeWithDebugLogging(
      Runnable action, Logger logger, String actionDescription) {
    try {
      logger.debug("Executing {}", actionDescription);
      action.run();
      logger.debug("Successfully executed {}", actionDescription);
    } catch (Exception e) {
      throw new AutomationFrameworkException(
          format("An exception has been thrown while executing %s", actionDescription), e);
    }
  }

  public static <T> T executeWithResultAndDebugLogging(
      Callable<T> actionWithResult, Logger logger, String actionDescription) {
    try {
      logger.debug("Executing {}", actionDescription);
      T result = actionWithResult.call();
      logger.debug("Successfully executed {}", actionDescription);
      return result;
    } catch (Exception e) {
      throw new AutomationFrameworkException(
          format("An exception has been thrown while executing %s", actionDescription), e);
    }
  }

  public static String prettyPrintCollection(Collection<?> collection) {
    return collection.stream()
        .map(Object::toString)
        .collect(Collectors.joining("\n\t", "\n\t", "\n"));
  }

  public static String prettyPrintArray(Object[] array) {
    return  prettyPrintCollection(asList(array));
  }
}
