package com.nagarro.driven.runner.testng.base.listener;

import com.nagarro.driven.core.config.CoreConfig;
import com.nagarro.driven.core.constant.FrameworkCoreConstant;
import com.nagarro.driven.core.reporting.api.IReportManager;
import com.nagarro.driven.core.reporting.api.TestReport;
import com.nagarro.driven.core.reporting.api.TestReportLogger;
import com.nagarro.driven.core.reporting.api.TestStatus;
import com.nagarro.driven.runner.testng.base.config.ExecutionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestContext;
import org.testng.ITestResult;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.nagarro.driven.core.util.ReportUtil.archiveReport;
import static java.lang.System.getProperty;

/**
 * This class is called after the test case has been executed.
 *
 * @author nagarro
 */
public class TestNGTestListener extends AbstractTestNgListener implements IRetryAnalyzer
{
  private static final Logger LOG = LoggerFactory.getLogger(TestNGTestListener.class);
  private static final int MAX_RETRY_COUNT = ExecutionConfig.getInstance().maxRetryCount();
  private int retryCount = 0;

  @Inject private IReportManager reportManager;
  @Inject private TestReport testReport;
  @Inject private TestReportLogger testReportLogger;

  /** Performs action on test start. */
  @Override
  public void onTestStart(ITestResult result)
  {
    TestReportLogger.setDescriptionForJiraBugEmpty();
  }

  /** Report the pass status in report on test case success. */
  @Override
  public void onTestSuccess(ITestResult result)
  {
    testReport.pass("Test succeeded");
  }

  /** Report the fail status in report on test case failure. */
  @Override
  public void onTestFailure(ITestResult result)
  {
    if (!result.getName().equals("feature")) {
      String failureMessage =
          result.getThrowable() != null ? result.getThrowable().getMessage() : null;
      testReport.error("Test failed: " + failureMessage);
      String testname = result.getName();

      String screenshotname = getScreenshotName(testname);

      if (CoreConfig.getInstance().screenshotOnFail()) {
        testReport.takeScreenshot(TestStatus.FAIL, "Failure screenshot", screenshotname);
      }
    }
  }

  private String getIdOfissueLinkedWithThisTestOnJira(ITestResult result) {
    String idOfissueLinkedWithThisTestOnJira = "";
    Annotation[] annotations =
        result.getMethod().getConstructorOrMethod().getMethod().getAnnotations();
    for (Annotation a : annotations) {
      if (a.toString().contains("Xray") || a.toString().contains("JiraIssueNumber")) {
        idOfissueLinkedWithThisTestOnJira = a.toString().split("=")[1].replace(")", "");
        break;
      }
    }
    return idOfissueLinkedWithThisTestOnJira;
  }

  private String getScreenshotName(String testname) {
    if (testname.length() > 100) testname = testname.substring(0, 99);
    return testname
        + new SimpleDateFormat("_" + FrameworkCoreConstant.TIMESTAMP_FORMAT).format(new Date())
        + "_FAILED";
  }

  /** Report the skip status in report on test case skipped. */
  @Override
  public void onTestSkipped(ITestResult result) {
    testReport.skip("Test skipped");
  }

  /** Report the pass status in report on test case failed but within success percentage. */
  @Override
  public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    testReport.pass("Failed but within success percentage");
  }

  /** Flush the report on finish. */
  @Override
  public void onFinish(ITestContext context) {
    reportManager.getReporter().flush();
    zipReportInTestDirectory();
  }

  @Override
  public boolean retry(ITestResult result) {
    if (retryCount < MAX_RETRY_COUNT) {
      LOG.info("Retrying {} again and the count is {}", result.getName(), retryCount + 1);
      retryCount++;
      return true;
    }
    return false;
  }

  private void zipReportInTestDirectory() {
    String archiveFile = "report" + FrameworkCoreConstant.REPORT_ARCHIVE_EXTENSION;
    Path archiveDir =
        Paths.get(getProperty("user.dir")).resolve("ZippedReport").resolve(archiveFile);
    archiveReport(archiveDir);
  }
}
