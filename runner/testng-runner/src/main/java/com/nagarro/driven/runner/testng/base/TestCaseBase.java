package com.nagarro.driven.runner.testng.base;

import static com.nagarro.driven.core.reporting.api.TestStatus.SKIP;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.nagarro.driven.core.Scope;
import com.nagarro.driven.core.guice.ThreadLocalScopeModule;
import com.nagarro.driven.core.reporting.api.IReportManager;
import com.nagarro.driven.core.reporting.api.KeywordReportingImpl;
import com.nagarro.driven.core.util.Cleanable;
import com.nagarro.driven.core.util.Sleeper;
import com.nagarro.driven.core.util.ThreadExecutor;
import com.nagarro.driven.runner.testng.base.TestCaseBase.TestCaseBaseModule;
import com.nagarro.driven.runner.testng.base.config.ExecutionConfig;
import com.nagarro.driven.runner.testng.base.listener.TestNGBugListener;
import com.nagarro.driven.runner.testng.base.listener.TestNGTestListener;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.aspectj.lang.Aspects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Guice;
import org.testng.annotations.Listeners;

/**
 * TestCaseBase is the parent test case base for all the test cases.
 *
 * @author nagarro
 */
@Listeners({TestNGTestListener.class, TestNGBugListener.class})
@Guice(modules = TestCaseBaseModule.class)
public abstract class TestCaseBase {
  private static final Logger LOG = LoggerFactory.getLogger(TestCaseBase.class);
  private static boolean flagForTimeLimitReached = false;
  private static final double TIME_BOX_TIME_LIMIT =
      ExecutionConfig.getInstance().timeboxTimeLimit();
  private static final TimeUnit TIME_BOX_TIME_LIMIT_UNIT =
      ExecutionConfig.getInstance().timeboxTimeLimitUnit();
  private final boolean timeBoxExecutionFlag = ExecutionConfig.getInstance().timeboxExecutionFlag();
  protected final boolean dashboardFlag = ExecutionConfig.getInstance().dashboardFlag();
  private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private String executionStartTime;
  private String testCaseStartTime;
  private static int totalTestCount = 0;
  private static final int[] count = {0, 0, 0};
  private static final String TEST_SKIP_EXCEPTION_MESSAGE =
      "Skipping test because the time limit has been exceeded";

  @Inject protected IReportManager reportManager;

  /** Method implements the timeBox execution */
  @BeforeSuite
  protected void beforeForTimeBox() {
    if (timeBoxExecutionFlag) {
      new Thread(TestCaseBase::timeBox).start();
    }
  }

  /** Keep monitoring the time limit given */
  private static void timeBox() {
    Sleeper.silentSleep((long) getTimeForTimeBox());
    flagForTimeLimitReached = true;
  }

  private static double getTimeForTimeBox() {
    switch (TIME_BOX_TIME_LIMIT_UNIT) {
      case HOURS:
        return TIME_BOX_TIME_LIMIT * 3600000;
      case SECONDS:
        return TIME_BOX_TIME_LIMIT * 1000;
      case MINUTES:
      default:
        return TIME_BOX_TIME_LIMIT * 60000;
    }
  }

  /** Flush and close the report. */
  @AfterSuite(alwaysRun = true)
  public void flushAndCloseReport() {
    if (timeBoxExecutionFlag) {
      new TestRecordMaintain().saveDetails();
    }
    reportManager.getReporter().flush();
  }

  /** Creates the parent node in report. */
  @BeforeClass(alwaysRun = true)
  protected synchronized void beforeTestClass() {
    String parentName = getClass().getSimpleName();
    LOG.info("Creating the parent node {} in report.", parentName);
    reportManager.createNode(parentName, true, false);
  }

  /**
   * Creates the child node inside the parent node created in beforeTestClass method.
   *
   * @param method, the method name
   */
  @BeforeMethod(alwaysRun = true)
  public void setUpBase(Method method) {
    testCaseStartTime = format.format(new Date());
    LOG.info("Creating the child node {} in report.", method.getName());
    String className = method.getDeclaringClass().getSimpleName();
    reportManager.createNode(
        method.getName() + Scope.getIssueID(className, method.getName()), false, false);
    if (flagForTimeLimitReached) {
      reportManager.getTest().log(SKIP, TEST_SKIP_EXCEPTION_MESSAGE);
      throw new SkipException(TEST_SKIP_EXCEPTION_MESSAGE);
    }
  }

  /**
   * Maintaining test results in csv.
   *
   * @param result - result
   */
  @SuppressWarnings({"All the control statements are needed."})
  @AfterMethod(alwaysRun = true)
  public void maintainTestDetails(ITestResult result) {
    String testCaseEndTime = format.format(new Date());
    if (!result.getMethod().getMethodName().equals("feature")) {
      String testName = result.getMethod().getMethodName();
      String testClass = result.getMethod().getRealClass().getName();
      String[] groupName = new String[] {"Default"};
      if (result.getMethod().getGroups().length != 0) {
        groupName = result.getMethod().getGroups();
      }

      if (timeBoxExecutionFlag) {
        new TestRecordMaintain().maintain(testClass + "_" + testName, result.getStatus());
      }
    }
  }

  private static void incrementCount(ITestResult result) {
    count[result.getStatus() - 1] += 1;
  }

  @BeforeSuite(alwaysRun = true)
  public synchronized void beforeSuit(ITestContext context) {
    executionStartTime = format.format(new Date());
    totalTestCount = context.getAllTestMethods().length;
  }

  /** Flush and close the report. */
  @AfterSuite(alwaysRun = true)
  public void saveExecutionDetails() {
    ThreadExecutor.getInstance().awaitTermination();
    if (timeBoxExecutionFlag) {
      new TestRecordMaintain().saveDetails();
    }
  }

  /**
   * Trigger the clean up action.
   *
   * @param cleanables - Cleanable instances which require cleanup
   */
  protected void triggerAppropriateCleanUps(Cleanable... cleanables) {
    for (Cleanable cleanable : cleanables) {
      try {
        cleanable.cleanUp();
      } catch (Exception cleanUpException) {
        cleanable.emergencyCleanUp();
      }
    }
  }

  public abstract static class TestCaseBaseModule extends AbstractModule {

    @Override
    protected void configure() {
      install(new ThreadLocalScopeModule());
      bind(IReportManager.class).to(getReportManagerImplementationClass()).in(Singleton.class);
      requestInjection(Aspects.aspectOf(KeywordReportingImpl.class));
    }

    public abstract Class<? extends IReportManager> getReportManagerImplementationClass();
  }
}
