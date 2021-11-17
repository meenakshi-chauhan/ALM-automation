package com.nagarro.driven.core.webdriver;

import com.nagarro.driven.core.driver.AbstractDriverProvider;
import com.nagarro.driven.core.driver.api.DriverOptions;
import com.nagarro.driven.core.reporting.api.TestReportLogger;

/**
 * An abstract Guice provider for all the instances implementing {@link AbstractWebDriver}. A concrete
 * implementation must be used in order to provide a binding to the requested type
 *
 * @param <T> - the actual type which needs to be provided
 * @author nagarro
 */
public abstract class AbstractWebDriverProvider<T extends AbstractWebDriver>
        extends AbstractDriverProvider<T> {
    protected AbstractWebDriverProvider(
            TestReportLogger testReportLogger,
            DriverOptions driverOptions,
            IWebDriverFactory<T> abstractDriverFactory) {
        super(testReportLogger, driverOptions, abstractDriverFactory);
    }
}
