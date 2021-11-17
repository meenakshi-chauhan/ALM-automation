package com.nagarro.driven.core.driver;

import com.nagarro.driven.core.driver.api.DriverOptions;
import com.nagarro.driven.core.driver.api.IDriver;
import com.nagarro.driven.core.driver.api.IDriverFactory;
import com.nagarro.driven.core.guice.AbstractClientProvider;
import com.nagarro.driven.core.reporting.api.TestReportLogger;

/**
 * An abstract Guice provider for all the instances implementing {@link IDriver} interface. A concrete
 * implementation must be used in order to provide a binding to the requested type
 *
 * @param <T> - the actual type which needs to be provided
 * @author nagarro
 */
public abstract class AbstractDriverProvider<T extends IDriver> extends AbstractClientProvider<T> {
  private final DriverOptions driverOptions;
  private final IDriverFactory<T> driverFactory;

  protected AbstractDriverProvider(
      TestReportLogger testReportLogger,
      DriverOptions driverOptions,
      IDriverFactory<T> driverFactory) {
    super(testReportLogger);
    this.driverOptions = driverOptions;
    this.driverFactory = driverFactory;
  }

  @Override
  public T get() {
    return driverFactory.createConcrete(driverOptions, testReportLogger);
  }
}
