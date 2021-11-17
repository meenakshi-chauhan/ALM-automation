package com.nagarro.driven.client.ng;

import com.nagarro.driven.client.ng.screenshot.Screenshot;
import com.nagarro.driven.client.selenium.SeleniumAbstractDriverFactory;
import com.nagarro.driven.client.selenium.SeleniumGrid;
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
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Creates the Ng driver and sets it priority.
 *
 * @author nagarro
 */
public class NgDriverFactory extends SeleniumAbstractDriverFactory {

  private static final Logger log = LoggerFactory.getLogger(NgDriverFactory.class);
  private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

  private DesiredCapabilities desiredCapabilities;

  /** Gets the webdriver created. */
  public static WebDriver getDriver() {
    return DRIVER.get();
  }

  @Override
  public NgDriver createConcrete(DriverOptions options, TestReportLogger testReportLogger) {
    log.trace(
        "Creating local WebDriver for browser {} with capabilities {}",
        options.getBrowser(),
        options.getCapabilities());
    final WebDriver webDriver;
    DRIVER.remove();
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
    webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
    DRIVER.set(webDriver);
    addSnapshotDriver();
    return new NgDriver(webDriver, testReportLogger);
  }

  @Override
  public boolean canInstantiate(DriverOptions options) {
    return Driver.NG_WEBDRIVER.toString().equals(options.getDriverName());
  }

  @Override
  public void myPriority() {
    // TODO Enum
    // TODO priority setting
  }

  private void addSnapshotDriver() {
    if (noScreenShotterFound()) {
      SnapshotterManager.addSnapshotter(new Screenshot());
    }
  }
}
