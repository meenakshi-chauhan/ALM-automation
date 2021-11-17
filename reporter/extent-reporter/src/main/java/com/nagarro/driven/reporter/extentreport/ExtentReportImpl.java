package com.nagarro.driven.reporter.extentreport;

import com.aventstack.extentreports.AnalysisStrategy;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.nagarro.driven.core.reporting.api.IReporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Implements the methods of reporter interface and includes methods related to extent report.
 *
 * <p><i>NOTE: This is a wrapper around ExtentReports</i>
 *
 * @author nagarro
 */
@Singleton
public class ExtentReportImpl implements IReporter {

  /** The logger. */
  private static final Logger log = LoggerFactory.getLogger(ExtentReportImpl.class);
  /* Instance of Extent reports. */
  ExtentReports extentReport = new ExtentReports();

  @Inject
  // Direct Singleton injection
  private ExtentReportImpl() {}

  @Override
  public synchronized void flush() {
    log.debug("Flush report.");
    extentReport.flush();
  }

  /** Attach the html reporter to the extent report. */
  public synchronized void attachReporter(final ExtentHtmlReporter htmlreporter) {
    log.debug("Attaching html reporter to extent report.");
    extentReport.setAnalysisStrategy(AnalysisStrategy.SUITE);
    extentReport.attachReporter(htmlreporter);
  }

  /**
   * Creates the extent test in extent report.
   *
   * @param testName name of the test
   * @return the extent test
   */
  public ExtentTest createExtentTest(final String testName) {
    log.debug("Creating test '{}' in extent report", testName);
    return extentReport.createTest(testName);
  }
}
