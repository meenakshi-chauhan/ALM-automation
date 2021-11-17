package au.com.alm.bdd.testbase;

import au.com.alm.bdd.config.ALMBDDConfigHolder;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Stage;
import com.nagarro.driven.client.selenium.SeleniumAbstractDriver;
import com.nagarro.driven.client.selenium.SeleniumAbstractDriverFactory;
import com.nagarro.driven.client.selenium.SeleniumDriverFactory;
import com.nagarro.driven.client.selenium.SeleniumDriverProvider;
import com.nagarro.driven.client.selenium.util.VideoRecordingUtil;
import com.nagarro.driven.core.driver.api.DriverOptions;
import com.nagarro.driven.core.reporting.api.IReportManager;
import com.nagarro.driven.core.webdriver.Browser;;
import com.nagarro.driven.reporter.extentreport.ExtentReportManagerImpl;
import com.nagarro.driven.runner.cucumbertestng.base.CucumberTestRunner;
import io.cucumber.guice.ScenarioScoped;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import javax.inject.Inject;

public class ALMBDDTestBase extends CucumberTestRunner {
    private static final String APPLICATION_URL = ALMBDDConfigHolder.getInstance().applicationURL();
    private static final Stage INJECTION_STAGE =
            ALMBDDConfigHolder.getInstance().dependencyInjectionStage();

    @Inject protected Provider<SeleniumAbstractDriver> seleniumAbstractDriverProvider;

    @BeforeSuite
    public void startRecorder() {
        VideoRecordingUtil.startRecorder(false);
    }

    @AfterSuite
    public void stopRecorder() {
        VideoRecordingUtil.stopRecorder(false);
    }

    @Override
    @Before
    public void before(Scenario scenario) {
        super.beforeScenario(scenario);
        seleniumAbstractDriverProvider.get().go(APPLICATION_URL);
    }

    @Override
    @After
    public void after(Scenario scenario) {
        super.afterScenario(scenario);
        seleniumAbstractDriverProvider.get().cleanUp();
    }

    @Override
    protected Stage getInjectionStage() {
        return INJECTION_STAGE;
    }

    @Override
    protected CucumberBaseModule getInjectionModule() {
        return new ALMBddTestModule();
    }

    public static class ALMBddTestModule extends CucumberBaseModule {
        private static final String APPLICATION_NAME =
                ALMBDDConfigHolder.getInstance().applicationName();
        private static final Browser BROWSER_NAME = ALMBDDConfigHolder.getInstance().initiateBrowser();
        private static final String DRIVER_NAME =
                ALMBDDConfigHolder.getInstance().driverName().toString();

        @Override
        protected void configure() {
            super.configure();
            bind(SeleniumAbstractDriverFactory.class)
                    .to(SeleniumDriverFactory.class)
                    .in(ScenarioScoped.class);
            bind(SeleniumAbstractDriver.class)
                    .toProvider(SeleniumDriverProvider.class)
                    .in(ScenarioScoped.class);
        }

        @Provides
        DriverOptions driverOptionsProvider() {
            return initializeDriverOptions();
        }

        @Override
        public Class<? extends IReportManager> getReportManagerImplementationClass() {
            return ExtentReportManagerImpl.class;
        }

        private DriverOptions initializeDriverOptions() {
            DriverOptions options = new DriverOptions();
            options.setApplicationName(APPLICATION_NAME);
            options.setBrowser(BROWSER_NAME);
            options.setDriverName(DRIVER_NAME);
            return options;
        }
    }

}
