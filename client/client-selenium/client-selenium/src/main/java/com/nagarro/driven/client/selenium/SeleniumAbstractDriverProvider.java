package com.nagarro.driven.client.selenium;

import com.nagarro.driven.core.driver.api.DriverOptions;
import com.nagarro.driven.core.reporting.api.TestReportLogger;
import com.nagarro.driven.core.webdriver.AbstractWebDriverProvider;

/**
 * An abstract extension of {@link AbstractWebDriverProvider} Guice provider which is responsible for the
 * instances of {@link SeleniumAbstractDriver}
 *
 * @author nagarro
 */
public abstract class SeleniumAbstractDriverProvider
        extends AbstractWebDriverProvider<SeleniumAbstractDriver> {
    protected SeleniumAbstractDriverProvider(
            TestReportLogger testReportLogger,
            DriverOptions driverOptions,
            SeleniumAbstractDriverFactory seleniumAbstractDriverFactory) {
        super(testReportLogger, driverOptions, seleniumAbstractDriverFactory);
    }
}
