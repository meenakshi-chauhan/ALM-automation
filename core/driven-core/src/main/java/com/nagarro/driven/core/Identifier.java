package com.nagarro.driven.core;

/**
 * Identifier class contains the details of the driver to be created.
 *
 * @author nagarro
 */
public class Identifier {

  private String driverName;
  private String applicationName;

  /**
   * Constructor assigns the value of driver name and application name.
   *
   * @param driverName the driver name
   * @param applicationName the application name
   */
  public Identifier(String driverName, String applicationName) {
    this.driverName = driverName;
    this.applicationName = applicationName;
  }

  /**
   * Gets the driver name.
   *
   * @return the driver name
   */
  public String getDriverName() {
    return driverName;
  }

  /**
   * Gets the application name.
   *
   * @return the application name
   */
  public String getApplicationName() {
    return applicationName;
  }
}
