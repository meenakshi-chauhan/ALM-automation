package com.nagarro.driven.core.reporting.api;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * TestReportLogger logs the test case status in the report based on the type of the report.
 *
 * @author nagarro
 */
@Singleton
public class TestReportLogger {
  private static final StringBuilder desc = new StringBuilder();
  private final IReportManager reportManager;

  // Direct Singleton injection
  @Inject
  private TestReportLogger(IReportManager reportManager) {
    this.reportManager = reportManager;
  }

  /**
   * Logs the test case status with message in the report.
   *
   * @param status, the status of test case
   * @param message, the message for the test case
   */
  public void reportLogger(final TestStatus status, final String message) {
    desc.append(message);
    reportManager.getTest().log(status, message);
  }

  /**
   * Logs the exception in the report.
   *
   * @param status, the status of test case
   * @param exception, the exception to be logged
   */
  public void reportErrorLogger(final TestStatus status, final Throwable exception) {
    desc.append(exception.getMessage());
    reportManager.getTest().log(status, exception);
  }

  public static void setDescriptionForJiraBugEmpty() {
    desc.delete(0, desc.length());
  }

  public static String getDescriptionForJiraBug() {
    return desc.toString();
  }
}
