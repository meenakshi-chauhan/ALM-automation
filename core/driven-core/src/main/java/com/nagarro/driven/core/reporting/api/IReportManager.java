package com.nagarro.driven.core.reporting.api;

import java.io.IOException;

/**
 * Creates report and test node in the report.
 *
 * @author nagarro
 */
public interface IReportManager {

  /**
   * Gets the instance of the report.
   *
   * @return the instance of report
   */
  IReporter getReporter();

  /**
   * Gets the instance of the test.
   *
   * @return the instance of test.
   */
  ITest getTest();

  /**
   * Creates the child or parent node in the report.
   *
   * @param nodeName, the name of the node
   * @param isParent, true if it parent node else false
   * @param isthirdNode, true if it is the third child node else false
   */
  void createNode(String nodeName, boolean isParent, boolean isthirdNode);

  /** Clears the parent test. */
  void clearParentTest();

  /** Clears the child test. */
  void clearChildTest();

  /**
   * Takes Screenshot for test case.
   *
   * @param status, status of the screenshot
   * @param message, message to be displayed with screenshot
   * @param sanitizedPath, path of the screenshot
   */
  void addScreenshot(TestStatus status, String message, String sanitizedPath) throws IOException;
  /**
   * Attach the custom JavaScript to Extent Report
   *
   * @param script, JavaScript as a String
   */
  void setScript(String script);
}
