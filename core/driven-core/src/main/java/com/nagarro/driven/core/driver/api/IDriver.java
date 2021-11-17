package com.nagarro.driven.core.driver.api;

import com.nagarro.driven.core.util.Cleanable;

/**
 * Interface for the driver.
 *
 * @author nagarro
 */
public interface IDriver extends Cleanable {

  /**
   * It contains the name of the driver
   *
   * @return driver name
   */
  String driverName();
}
