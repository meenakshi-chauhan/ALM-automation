package com.nagarro.driven.core.driver.api;

import com.nagarro.driven.core.reporting.api.TestReportLogger;

/**
 * Interface for creation/instantiation of driver and setting priority
 *
 * @author nagarro
 */
public interface IDriverFactory<T extends IDriver> {

  /**
   * Contains the concrete implementation of the driver and responsible for creating the driver
   * according to the browser.
   *
   * @param options the driver options
   * @return the driver
   */
  T createConcrete(DriverOptions options);

  /**
   * Contains the concrete implementation of the driver and responsible for creating the driver
   * according to the browser. By default will call the driver's creation without the report logger
   *
   * @param options the driver options
   * @param testReportLogger - report logger which will be used for this driver
   * @return the driver
   */
  default T createConcrete(DriverOptions options, TestReportLogger testReportLogger) {
    return createConcrete(options);
  }

  /**
   * Sets the priority to the driver.
   */
  void myPriority();

  /**
   * Provides the condition on which driver will be instantiated otherwise not.
   *
   * @param options the driver options
   * @return true or false
   */
  boolean canInstantiate(DriverOptions options);
}
