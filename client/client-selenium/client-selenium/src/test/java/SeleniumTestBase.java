import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.nagarro.driven.client.selenium.SeleniumAbstractDriver;
import com.nagarro.driven.client.selenium.SeleniumAbstractDriverFactory;
import com.nagarro.driven.client.selenium.SeleniumDriverFactory;
import com.nagarro.driven.client.selenium.SeleniumDriverProvider;
import com.nagarro.driven.core.driver.api.DriverOptions;
import com.nagarro.driven.core.guice.ThreadLocalScope;
import com.nagarro.driven.core.guice.ThreadLocalScoped;
import com.nagarro.driven.core.reporting.api.IReportManager;
import com.nagarro.driven.core.reporting.api.TestReportLogger;
import com.nagarro.driven.core.webdriver.Browser;
import com.nagarro.driven.reporter.extentreport.ExtentReportManagerImpl;
import com.nagarro.driven.runner.testng.base.TestCaseBase;
import javax.inject.Inject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Guice;

/**
 * The test base class for the Unit test project.
 *
 * @author nagarro
 */
@Guice(modules = SeleniumTestBase.SeleniumTestBaseModule.class)
public class SeleniumTestBase extends TestCaseBase {

  static Browser BROWSER = Browser.CHROME;

  @Inject protected ThreadLocalScope threadLocalScope;
  @Inject protected Provider<SeleniumAbstractDriver> seleniumAbstractDriverProvider;
  @Inject protected TestReportLogger reportLogger;

  /** Enters an injection scope and opens the application url */
  @BeforeMethod
  public void beforeMethod() {
    threadLocalScope.enter();
    seleniumAbstractDriverProvider.get();
  }

  /** Quits the client and exits the injection scope */
  @AfterMethod(alwaysRun = true)
  public void afterMethod() {
    try {
      seleniumAbstractDriverProvider.get().cleanUp();
    } finally {
      threadLocalScope.exit();
    }
  }

  public static class SeleniumTestBaseModule extends TestCaseBaseModule {
    @Override
    protected void configure() {
      super.configure();
      bind(SeleniumAbstractDriverFactory.class).to(SeleniumDriverFactory.class).in(Singleton.class);
      bind(SeleniumAbstractDriver.class)
          .toProvider(SeleniumDriverProvider.class)
          .in(ThreadLocalScoped.class);
    }

    @Override
    public Class<? extends IReportManager> getReportManagerImplementationClass() {
      return ExtentReportManagerImpl.class;
    }

    @Provides
    DriverOptions driverOptionsProvider() {
      return initializeDriverOptions();
    }

    public DriverOptions initializeDriverOptions() {
      DriverOptions options = new DriverOptions();
      options.setBrowser(BROWSER);
      return options;
    }
  }
}
