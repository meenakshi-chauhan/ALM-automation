package com.nagarro.driven.client.selenium;

import com.nagarro.driven.core.driver.api.DriverOptions;
import com.nagarro.driven.core.reporting.api.TestReportLogger;

import javax.inject.Inject;

/**
 * A concrete implementation of {@link SeleniumAbstractDriverProvider} Guice provider which is responsible
 * for the instances of {@link SeleniumAbstractDriver} using {@link SeleniumDriverFactory}
 *
 * @author nagarro
 */
public class SeleniumDriverProvider extends SeleniumAbstractDriverProvider {
    @Inject
    public SeleniumDriverProvider(
            TestReportLogger testReportLogger,
            DriverOptions driverOptions,
            SeleniumDriverFactory seleniumDriverFactory) {
        super(testReportLogger, driverOptions, seleniumDriverFactory);
    }
}
