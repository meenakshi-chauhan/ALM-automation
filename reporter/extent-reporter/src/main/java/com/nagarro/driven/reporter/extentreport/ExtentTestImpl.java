package com.nagarro.driven.reporter.extentreport;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.MediaEntityModelProvider;
import com.nagarro.driven.core.reporting.api.ITest;
import com.nagarro.driven.core.reporting.api.TestStatus;
import com.nagarro.driven.core.util.AutomationFrameworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

/**
 * ExtentTestImpl implements methods for extent test.
 *
 * @author nagarro
 */
@Singleton
public class ExtentTestImpl implements ITest {

  private static final Logger log = LoggerFactory.getLogger(ExtentTestImpl.class);

  /* The parent test thread local. */
  private final ThreadLocal<ExtentTest> parentTest = new ThreadLocal<>();

  /* The child test thread local. */
  private final ThreadLocal<ExtentTest> childTest = new ThreadLocal<>();

  /* The test thread local. */
  private final ThreadLocal<ExtentTest> test = new ThreadLocal<>();

  // Direct Singleton injection
  @Inject
  private ExtentTestImpl() {}

  @Override
  public void log(TestStatus testStatus, String message) {
    if (null != test.get()) {
      switch (testStatus) {
        case PASS:
          test.get().pass(message);
          break;
        case FAIL:
          test.get().fail(message);
          break;
        case ERROR:
          test.get().error(message);
          break;
        case INFO:
          test.get().info(message);
          break;
        case SKIP:
        default:
          test.get().skip(message);
      }
    }
  }

  @Override
  public void log(TestStatus testStatus, Throwable exception) {
    AutomationFrameworkException automationFrameworkException =
        new AutomationFrameworkException(exception.getLocalizedMessage(), exception.getCause());
    automationFrameworkException.setStackTrace(exception.getStackTrace());
    if (null != test.get()) {
      switch (testStatus) {
        case FAIL:
          test.get().fail(exception);
          break;
        case ERROR:
          test.get().error(exception);
          break;
        default:
          test.get().info(exception);
      }
    }
    throw automationFrameworkException;
  }

  @Override
  public void passScreenshot(String message, String entity) throws IOException {
    log.debug("Logs the pass screenshot with message : {}", message);
    MediaEntityModelProvider mediaModel =
        MediaEntityBuilder.createScreenCaptureFromPath(entity).build();
    test.get().pass(message, mediaModel);
  }

  @Override
  public void failScreenshot(String message, String entity) throws IOException {
    log.debug("Logs the fail screenshot with message : {}", message);
    MediaEntityModelProvider mediaModel =
        MediaEntityBuilder.createScreenCaptureFromPath(entity).build();
    test.get().fail(message, mediaModel);
  }

  @Override
  public void infoScreenshot(String message, String entity) throws IOException {
    log.debug("Logs the info screenshot with message : {}", message);
    MediaEntityModelProvider mediaModel =
        MediaEntityBuilder.createScreenCaptureFromPath(entity).build();
    test.get().info(message, mediaModel);
  }

  /**
   * Creates the child or parent node in the report.
   *
   * @param nodeName the name of the node
   * @param isParent true if it parent node else false
   * @param isthirdNode true if it is the third child node else false
   * @param extentReport the instance of extent report
   */
  public void createNode(
      String nodeName, boolean isParent, boolean isthirdNode, ExtentReportImpl extentReport) {
    ExtentTest node;
    if (isParent) {
      node = startNewTest(nodeName, extentReport);
      parentTest.set(node);
    } else if (!isthirdNode) {
      if (null != parentTest.get()) {
        node = parentTest.get().createNode(nodeName);
      } else {
        node = startNewTest(nodeName, extentReport);
      }
      childTest.set(node);
    } else {
      if (null != childTest.get()) {
        node = childTest.get().createNode(nodeName);
      } else if (null != parentTest.get()) {
        node = parentTest.get().createNode(nodeName);
      } else {
        node = startNewTest(nodeName, extentReport);
      }
    }
    test.set(node);
  }

  /** Clears the parent test. */
  public void clearParentTest() {
    parentTest.remove();
  }

  /** Clears the child test. */
  public void clearChildTest() {
    childTest.remove();
  }

  /** Clears the current test node */
  public void clearCurrentTest() {
    test.remove();
  }

  /*
   * Start the new test in extent report.
   */
  private ExtentTest startNewTest(String nodeName, ExtentReportImpl extentReport) {
    return extentReport.createExtentTest(nodeName);
  }
}
