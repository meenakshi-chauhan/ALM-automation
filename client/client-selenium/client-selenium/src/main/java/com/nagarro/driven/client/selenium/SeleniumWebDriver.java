package com.nagarro.driven.client.selenium;

import com.nagarro.driven.core.reporting.api.TestReportLogger;
import org.openqa.selenium.WebDriver;

/**
 * SeleniumWebDriver extend SeleniumAbstractDriver, which contains all the functions related to
 * selenium driver.
 *
 * @author nagarro
 */
public class SeleniumWebDriver extends SeleniumAbstractDriver {

    /**
     * Calls the constructor of SeleniumAbstractDriver, where driver is assigned.
     *
     * @param webDriver to be used
     */
    public SeleniumWebDriver(WebDriver webDriver, TestReportLogger reportLog) {
        super(webDriver, reportLog);
    }

    @Override
    public void waitForPageToLoad() {
        // This method does not have any use in selenium. It is implemented in
        // ng driver.
    }
}
