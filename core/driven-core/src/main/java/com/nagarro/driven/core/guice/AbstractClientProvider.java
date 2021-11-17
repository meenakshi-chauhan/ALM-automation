package com.nagarro.driven.core.guice;

import com.google.inject.Provider;
import com.nagarro.driven.core.reporting.api.TestReportLogger;

/**
 * An abstract Guice provider for all the instances which need a {@link TestReportLogger} instance
 * for internal purposes. A concrete implementation must be used in order to provide a binding to
 * the requested type
 *
 * @param <T> - the actual type which needs to be provided
 * @author nagarro
 */
public abstract class AbstractClientProvider<T> implements Provider<T> {
  protected final TestReportLogger testReportLogger;

  protected AbstractClientProvider(TestReportLogger testReportLogger) {
    this.testReportLogger = testReportLogger;
  }
}
