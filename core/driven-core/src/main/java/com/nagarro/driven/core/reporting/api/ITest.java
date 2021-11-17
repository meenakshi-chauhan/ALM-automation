package com.nagarro.driven.core.reporting.api;

import java.io.IOException;

/**
 * Interface for reporting test and its pass and fail screenshot.
 * 
 * @author nagarro
 */
public interface ITest {

	/**
	 * Logs the test status with a message in the report.
	 * 
	 * @param testStatus,
	 *            status of the test
	 * @param message,
	 *            text to be logged
	 */
	void log(TestStatus testStatus, String message);

	/**
	 * Logs the exception in the report.
	 * 
	 * @param testStatus,
	 *            status of the test
	 * //@param exception,
	 *            exception to be logged
	 */
	void log(TestStatus testStatus, Throwable e);

	/**
	 * Logs the pass screenshot in the report.
	 * 
	 * @param message,
	 *            test message with screenshot
	 * @param screenshotPath,
	 *            path of the screenshot image
	 * @throws IOException,
	 *             io exception
	 */
	void passScreenshot(String message, String screenshotPath) throws IOException;

	/**
	 * Logs the fail screenshot in the report.
	 * 
	 * @param message,
	 *            test message with screenshot
	 * @param screenshotPath,
	 *            path of the screenshot image
	 * @throws IOException,
	 *             io exception
	 */
	void failScreenshot(String message, String screenshotPath) throws IOException;
	
	void infoScreenshot(String message, String entity) throws IOException;
}
