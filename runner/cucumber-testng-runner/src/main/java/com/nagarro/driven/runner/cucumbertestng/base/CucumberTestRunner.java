package com.nagarro.driven.runner.cucumbertestng.base;

import static com.google.inject.Guice.createInjector;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.Stage;
import com.nagarro.driven.core.config.CoreConfig;
import com.nagarro.driven.core.constant.FrameworkCoreConstant;
import com.nagarro.driven.core.guice.ThreadLocalScopeModule;
import com.nagarro.driven.core.reporting.api.IReportManager;
import com.nagarro.driven.core.reporting.api.TestReport;
import com.nagarro.driven.core.reporting.api.TestReportLogger;
import com.nagarro.driven.core.reporting.api.TestStatus;
import com.nagarro.driven.core.util.ThreadExecutor;
import com.nagarro.driven.runner.testng.base.TestRecordMaintain;
import io.cucumber.core.backend.TestCaseState;
import io.cucumber.guice.CucumberModules;
import io.cucumber.guice.InjectorSource;
import io.cucumber.java.Scenario;
import io.cucumber.plugin.event.Result;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.FeatureWrapper;
import io.cucumber.testng.PickleWrapper;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.inject.Inject;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;

/**
 * CucumberTestRunner is the parent test case for all BDD Cucumber-based test cases. It uses
 * dependency injection but it's active only in the scope of Cucumber's execution
 *
 * @author nagarro
 */
public abstract class CucumberTestRunner extends AbstractTestNGCucumberTests
    implements InjectorSource {
  private String currentFeatureName;
  private Object[][] features;
  private static Set<String> nodes = new HashSet<>();
  private static final Logger log = LoggerFactory.getLogger(CucumberTestRunner.class);

  @Inject private TestReport testReport;
  @Inject private IReportManager reportManager;
  @Inject private TestReportLogger testReportLogger;

  /**
   * Before scenario hook which uses DI and should be overridden and should at least call {@link
   * #beforeScenario(Scenario)}
   */
  public abstract void before(Scenario scenario);

  /**
   * After scenario hook which uses DI and should be overridden and should at least call {@link
   * #afterScenario(Scenario)}
   */
  public abstract void after(Scenario scenario);

  public void feature(PickleWrapper pickle, FeatureWrapper feature) {
    super.runScenario(pickle, feature);
    currentFeatureName = pickle.getPickle().getName();
  }

  @DataProvider(name = "features")
  public Object[][] features() {
    return new RiskBasedExecutionCucumber().arrangeFeatures(features);
  }

  /**
   * Maintaining test results in csv.
   *
   * @param result - test result
   */
  @AfterMethod(alwaysRun = true)
  public void maintainTestDetails(ITestResult result) {
    new TestRecordMaintain().maintain(currentFeatureName, result.getStatus());
  }

  /**
   * DI injector provider. Works only in the scope of Cucumber's execution
   *
   * @return - the initialized and configured injector
   */
  @Override
  public Injector getInjector() {
    return initializeInjector();
  }

  public void beforeScenario(Scenario scenario) {
    String featureName = null;
    try {
      Matcher m = Pattern.compile("([a-zA-Z]+[.]{1}[a-z]+)").matcher(scenario.getId());
      m.find();
      featureName = m.group(0);
    } catch (PatternSyntaxException e) {
      log.error(e.getMessage());
    }

    if (nodes.add(featureName)) {
      log.info("Creating the parent node {} in report.", featureName);
      reportManager.createNode(featureName, true, false);
    }
    String scenarioName = scenario.getName();
    reportManager.createNode(scenarioName, false, false);
  }

  public void afterScenario(@NotNull Scenario scenario) {
    String scenarioName = scenario.getName().replace(' ', '_');
    Optional<String> optionalFailureMessage;

    if (scenario.isFailed()) {
      optionalFailureMessage = getFailureMessage(scenario);
      if (optionalFailureMessage.isPresent()) {
        String failureMessage = optionalFailureMessage.get();
        testReport.error(failureMessage.split("\n")[0]);
        String screenshotName = getScreenshotName(scenarioName);
        if (CoreConfig.getInstance().screenshotOnFail()) {
          testReport.takeScreenshot(TestStatus.FAIL, "Failure screenshot", screenshotName);
        }
        }
      }
    ThreadExecutor.getInstance().awaitTermination();
    reportManager.getReporter().flush();
  }

  @Override
  @AfterClass(alwaysRun = true)
  public void tearDownClass() {
    super.tearDownClass();
  }

  protected abstract Stage getInjectionStage();

  protected abstract CucumberBaseModule getInjectionModule();

  private Injector initializeInjector() {
    Injector newInjector =
        createInjector(
            getInjectionStage(), CucumberModules.createScenarioModule(), getInjectionModule());
    newInjector.injectMembers(this);
    return newInjector;
  }

  private String getIdOfIssueLinkedWithThisTestOnJira(Scenario scenario) {
    String idOfIssueLinkedWithThisTestOnJira = "";
    Collection<String> annotations = scenario.getSourceTagNames();
    for (String a : annotations) {
      if (a.contains("Xray") || a.contains("JiraIssueNumber")) {
        idOfIssueLinkedWithThisTestOnJira = a.split("=")[1];
        break;
      }
    }
    return idOfIssueLinkedWithThisTestOnJira;
  }

  private String getScreenshotName(String testName) {
    if (testName.length() > 100) testName = testName.substring(0, 99);
    StringBuilder screenshotName =
        new StringBuilder()
            .append(testName)
            .append(
                new SimpleDateFormat("_" + FrameworkCoreConstant.TIMESTAMP_FORMAT)
                    .format(new Date()))
            .append("_FAILED");
    return screenshotName.toString();
  }

  @SuppressWarnings("unchecked")
  private Optional<String> getFailureMessage(Scenario scenario) {
    try {
      Field fields = FieldUtils.getField((scenario).getClass(), "delegate", true);
      final TestCaseState testCase = (TestCaseState) fields.get(scenario);
      Field field = FieldUtils.getField((testCase).getClass(), "stepResults", true);
      ArrayList<Result> results = (ArrayList<Result>) field.get(testCase);

      return results.stream()
          .filter(Objects::nonNull)
          .map(Result::getError)
          .filter(Objects::nonNull)
          .findFirst()
          .map(Throwable::getMessage);
    } catch (IllegalAccessException e) {
      log.error("Failure reason could not be retrieved {} ", e.getMessage());
      return Optional.empty();
    }
  }

  public abstract static class CucumberBaseModule extends AbstractModule {
    @Override
    protected void configure() {
      install(new ThreadLocalScopeModule());
      bind(IReportManager.class).to(getReportManagerImplementationClass()).in(Singleton.class);
    }

    public abstract Class<? extends IReportManager> getReportManagerImplementationClass();
  }
}
