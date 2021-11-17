package com.nagarro.driven.client.ng;

import com.nagarro.driven.client.selenium.SeleniumAbstractDriver;
import com.nagarro.driven.client.selenium.WebLocator;
import com.nagarro.driven.client.ui.api.Element;
import com.nagarro.driven.client.ui.api.Elements;
import com.nagarro.driven.core.reporting.api.TestReportLogger;
import com.paulhammant.ngwebdriver.NgWebDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

/**
 * NgDriver extend SeleniumAbstractDriver, which contains all the functions related to selenium
 * driver and implement some methods specific to NgDriver.
 *
 * @author nagarro
 */
public class NgDriver extends SeleniumAbstractDriver {
  private final NgWebDriver ngWebDriver;

  /**
   * Calls the constructor of SeleniumAbstractDriver, where driver is assigned and create
   * NgWebDriver using web driver.
   */
  public NgDriver(WebDriver webDriver, TestReportLogger testReportLogger) {
    super(webDriver, testReportLogger);
    ngWebDriver = new NgWebDriver((JavascriptExecutor) webDriver);
    this.waitForPageToLoad();
  }

  @Override
  public void waitForPageToLoad() {
    ngWebDriver.waitForAngularRequestsToFinish();
  }

  @Override
  public Element<WebLocator> element(String pageName, String elementName) {
    WebLocator locator =
        NgWebLocatorLoader.findWebLocatorForElement(pageName, elementName, this.getWebDriver(), reportLog);
    return new SeleniumElement(locator, elementName);
  }

  @Override
  public Element<WebLocator> element(String pageName, String elementName, Object ... dynamicLocatorValue) {
    WebLocator locator =
            NgWebLocatorLoader.findWebLocatorForElement(pageName, elementName, this.getWebDriver(), reportLog, dynamicLocatorValue);
    return new SeleniumElement(locator, elementName);
  }

  @Override
  public Elements elements(String pageName, String elementName) {
    WebLocator locator =
        NgWebLocatorLoader.findWebLocatorForElement(pageName, elementName, this.getWebDriver(), reportLog);
    return new SeleniumElements(locator, elementName);
  }
}
