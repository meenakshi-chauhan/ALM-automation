package com.nagarro.driven.runner.testng.base.listener;

import java.util.HashSet;
import java.util.Set;

import com.nagarro.driven.core.config.CoreConfig;
import com.nagarro.driven.core.reporting.api.IReportManager;
import com.nagarro.driven.runner.testng.base.extentreport.Bug;
import com.nagarro.driven.runner.testng.base.extentreport.TestCaseCount;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;

import com.nagarro.driven.core.ITestCaseCount;
import com.nagarro.driven.core.Scope;

import javax.inject.Inject;

public class TestNGBugListener extends AbstractTestNgListener {
  @Inject private IReportManager reportManager;

  @Override
  public void onTestStart(ITestResult result) {
    // Override for Test Start
  }

  @Override
  public void onTestSuccess(ITestResult result) {
    ITestCaseCount currentCount = null;
    String className = result.getMethod().getRealClass().getSimpleName();
    try {
      currentCount = Scope.getResultCount(className);
      currentCount.setPassCount(currentCount.getPassCount() + 1);

    } catch (Exception e) {
      currentCount = new TestCaseCount();
      currentCount.setPassCount(currentCount.getPassCount() + 1);
    }
    Scope.setResultCount(className, currentCount);
  }

  @Override
  public void onTestFailure(ITestResult result) {
    ITestCaseCount currentCount = null;
    String className = result.getMethod().getRealClass().getSimpleName();
    String testName = result.getMethod().getMethodName();
    try {
      currentCount = Scope.getResultCount(className);

      if (Scope.getIssueID(className, testName).equals("")) {
        currentCount.setNewIssueCount(currentCount.getNewIssueCount() + 1);
      } else {
        currentCount.setKnownIssueCount(currentCount.getKnownIssueCount() + 1);
      }
    } catch (Exception e) {
      currentCount = new TestCaseCount();
      if (Scope.getIssueID(className, testName).equals("")) {
        currentCount.setNewIssueCount(currentCount.getNewIssueCount() + 1);
      } else {
        currentCount.setKnownIssueCount(currentCount.getKnownIssueCount() + 1);
      }
    }
    Scope.setResultCount(className, currentCount);
  }

  @Override
  public void onTestSkipped(ITestResult result) {
    ITestCaseCount currentCount = null;
    String className = result.getMethod().getRealClass().getSimpleName();
    try {
      currentCount = Scope.getResultCount(className);
      currentCount.setSkipCount(currentCount.getSkipCount() + 1);

    } catch (Exception e) {
      currentCount = new TestCaseCount();
      currentCount.setSkipCount(currentCount.getSkipCount() + 1);
    }
    Scope.setResultCount(className, currentCount);
  }

  @Override
  public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    // Override on test failure within success percentage
  }

  @Override
  public void onStart(ITestContext context) {
    super.onStart(context);
    if (CoreConfig.getInstance().extentReportIssueLogging()) {
      for (ITestNGMethod method : context.getAllTestMethods()) {
        try {
          String[] issue = new String[3];
          issue[0] = method.getTestClass().getRealClass().getSimpleName();
          issue[1] = method.getMethodName();
          issue[2] =
              method.getConstructorOrMethod().getMethod().getAnnotation(Bug.class).jiraIssueId();
          Scope.setIssueID(issue);
        } catch (Exception e) {
          // There was no bug annotation for the particular test.
        }
      }
    }
  }

  @Override
  public void onFinish(ITestContext context) {
    if (CoreConfig.getInstance().extentReportIssueLogging()) {
      StringBuilder script = new StringBuilder();
      Set<String> classList = new HashSet<>();
      for (ITestNGMethod method : context.getAllTestMethods()) {
        classList.add(method.getRealClass().getSimpleName());
      }

      for (String className : classList) {
        if (Scope.getResultCount(className).getKnownIssueCount()
                + Scope.getResultCount(className).getNewIssueCount()
            != 0) {
          script.append("$(\"li.test:contains('");
          script.append(className);
          script.append("')>div.test-heading>span.test-time\").before(\"");
          script.append(Scope.getResultCount(className).toString());
          script.append("<br>\");");
        }
      }
      reportManager.setScript(script.toString());
    }
  }
}
