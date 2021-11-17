package com.nagarro.driven.core.webdriver;

import com.nagarro.driven.core.driver.api.IDriver;
import com.nagarro.driven.core.reporting.api.TestReportLogger;

/**
 * AbstractWebDriver is a abstract class which implement IDriver.
 *
 * @author nagarro
 */
public abstract class AbstractWebDriver implements IDriver {
  protected final TestReportLogger reportLog;

  protected AbstractWebDriver(TestReportLogger reportLog) {
    this.reportLog = reportLog;
  }
}