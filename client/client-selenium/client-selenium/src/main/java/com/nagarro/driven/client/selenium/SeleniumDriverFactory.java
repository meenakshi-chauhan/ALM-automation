package com.nagarro.driven.client.selenium;

import com.nagarro.driven.client.selenium.config.SeleniumConfig;
import com.nagarro.driven.client.selenium.report.Screenshot;
import com.nagarro.driven.client.ui.api.UnexpectedClientException;
import com.nagarro.driven.core.driver.api.DriverOptions;
import com.nagarro.driven.core.reporting.api.TestReportLogger;
import com.nagarro.driven.core.reporting.screenshot.SnapshotterManager;
import com.nagarro.driven.core.webdriver.Driver;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.selendroid.client.SelendroidDriver;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Creates the selenium driver and sets it priority.
 *
 * @author nagarro
 */
public class SeleniumDriverFactory extends SeleniumAbstractDriverFactory {
    private static final Logger LOG = LoggerFactory.getLogger(SeleniumDriverFactory.class);
    private static final ThreadLocal<WebDriver> DRIVER_THREAD_LOCAL = new ThreadLocal<>();
    private static final int IMPLICIT_WAIT_TIMEOUT_SECONDS =
            SeleniumConfig.getInstance().seleniumImplicitWaitTimeoutSeconds();

    private DesiredCapabilities desiredCapabilities;

    /**
     * Gets the webdriver created.
     */
    public static WebDriver getDriver() {
        return DRIVER_THREAD_LOCAL.get();
    }

    /**
     * Creates the capabilities of firefox by setting various options in it.
     *
     * @return the capabilities of firefox.
     */
    @Override
    protected FirefoxOptions createFirefoxOptions() {
        FirefoxOptions firefoxOptions = super.createFirefoxOptions();
        firefoxOptions.setCapability(FirefoxDriver.MARIONETTE, false);
        return firefoxOptions;
    }

    @Override
    public SeleniumWebDriver createConcrete(
            DriverOptions options, TestReportLogger testReportLogger) {
        LOG.trace(
                "Creating local WebDriver for browser {} with capabilities {}",
                options.getBrowser(),
                options.getCapabilities());
        final WebDriver webDriver;
        DRIVER_THREAD_LOCAL.remove();
        if (options.getGridUrl() != null) {
            webDriver = new SeleniumGrid(options).create();
        } else {
            switch (options.getBrowser()) {
                case INTERNET_EXPLORER:
                    WebDriverManager.iedriver().setup();
                    webDriver = new InternetExplorerDriver(createIEOptions());
                    break;
                case CHROME:
                    WebDriverManager.chromedriver().setup();
                    webDriver = new ChromeDriver(createChromeOptions());
                    break;
                case FIREFOX:
                    WebDriverManager.firefoxdriver().setup();
                    webDriver = new FirefoxDriver(createFirefoxOptions());
                    break;
                case ANDROID:
                    try {
                        webDriver = new SelendroidDriver(desiredCapabilities);
                    } catch (Exception e) {
                        throw new UnexpectedClientException("Couldn't create Selendroid driver", e);
                    }
                    break;
                default:
                    throw new UnexpectedClientException("Unknown browser " + options.getBrowser());
            }
        }
        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().implicitlyWait(IMPLICIT_WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        DRIVER_THREAD_LOCAL.set(webDriver);
        addSnapshotDriver();
        return new SeleniumWebDriver(webDriver, testReportLogger);
    }

    @Override
    public void myPriority() {
        // TODO Enum
        // TODO priority setting

    }

    @Override
    public boolean canInstantiate(DriverOptions options) {
        return Driver.valueOf(options.getDriverName()).equals(Driver.SELENIUM_WEBDRIVER);
    }

    /**
     * Adds the screenshot instance for selenium driver in the screenshot list.
     */
    private void addSnapshotDriver() {
        if (noScreenShotterFound()) {
            SnapshotterManager.addSnapshotter(new Screenshot());
        }
    }
}
