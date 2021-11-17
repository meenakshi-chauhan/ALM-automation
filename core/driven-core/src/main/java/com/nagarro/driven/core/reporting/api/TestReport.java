package com.nagarro.driven.core.reporting.api;

import com.nagarro.driven.core.config.CoreConfig;
import com.nagarro.driven.core.constant.FrameworkCoreConstant;
import com.nagarro.driven.core.reporting.screenshot.Screenshotter;
import com.nagarro.driven.core.reporting.screenshot.SnapshotterManager;
import com.nagarro.driven.core.util.AutomationFrameworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.nagarro.driven.core.constant.FrameworkCoreConstant.REPORT_FOLDER_NAME;
import static com.nagarro.driven.core.constant.FrameworkCoreConstant.SCREENSHOT_FOLDER_NAME;

/**
 * TestReport for reporting the status (pass, fail, skip) of test cases in report and take its
 * screenshot.
 *
 * @author nagarro
 */
@Singleton
public class TestReport {
  private static final String REPORT_LEVEL = CoreConfig.getInstance().reportLevel();
  private static final String LEVEL_THREE = "3";
  /* The Logger. */
  private final Logger log = LoggerFactory.getLogger(TestReport.class);

  /* The core config. */
  private final CoreConfig.CoreConfigSpec config = CoreConfig.getInstance();

  /* The report base directory path. */
  private final Path reportBaseDir = config.reportPath().resolve(REPORT_FOLDER_NAME);

  /* The screenshot directory path. */
  private final Path screenshotDir = reportBaseDir.resolve(SCREENSHOT_FOLDER_NAME);

  /* Instance of test report logger. */
  private final TestReportLogger testReportLogger;
  private final IReportManager reportManager;

  // Direct Singleton injection
  @Inject
  private TestReport(TestReportLogger testReportLogger, IReportManager reportManager) {
    this.testReportLogger = testReportLogger;
    this.reportManager = reportManager;
  }

  /* The static block to initialize the screenshot. */
  static {
    if (CoreConfig.getInstance().screenshotOnPass()
        || CoreConfig.getInstance().screenshotOnFail()) {
      Screenshotter.init();
    }
  }

  /* Logs the test case status in the report. */
  private void report(TestStatus status, String message) {
    testReportLogger.reportLogger(status, message);
  }

  /**
   * Report the pass status of the test case.
   *
   * @param message, the pass message
   */
  public void pass(String message) {
    log.info(message);
    report(TestStatus.PASS, message);
  }

  /**
   * Report the info status of the test case.
   *
   * @param message, the info message
   */
  public void info(String message) {
    log.info(message);
    report(TestStatus.INFO, message);
  }

  /**
   * Report the skip status of the test case.
   *
   * @param message, the skip message
   */
  public void skip(String message) {
    log.info(message);
    report(TestStatus.SKIP, message);
  }

  /**
   * Report the fail status of the test case.
   *
   * @param message, the fail message
   */
  public void error(String message) {
    log.error(message);
    report(TestStatus.FAIL, message);
  }

  /**
   * Takes screenshot and store images in the screenshot folder.
   *
   * @param status, log status of screenshot.
   * @param description, the screenshot description
   */
  public void takeScreenshot(TestStatus status, String description, String screenShotName) {
    if (REPORT_LEVEL.equalsIgnoreCase(LEVEL_THREE)) {
      log.debug("Taking screenshots");
      StringBuilder screenshotText = new StringBuilder(description);
      SnapshotterManager.getSnapshotter()
          .forEach(
              img -> {
                try {
                  addImage(
                      status,
                      img.takeSnapshot().toPath(),
                      screenshotText.toString(),
                      screenShotName);
                } catch (IOException ioException) {
                  log.error("Exception occured while adding image", ioException);
                }
              });
    }
  }

  /**
   * Adds the screenshot to a screenshot folder and add it in report.
   *
   * @param status, log status of screenshot.
   * @param screenshotPath, the screenshot path
   * @param message, the screenshot description
   * @throws IOException - IOException
   */
  public void addImage(
      TestStatus status, Path screenshotPath, String message, String screenShotName)
      throws IOException {
    log.info("Adding screenshot from {} to Report: {}", screenshotPath, message);
    String sanitizedPath =
        sanitizePath(
            reportBaseDir
                .relativize(
                    storeFile(
                        screenshotPath,
                        screenshotDir,
                        FrameworkCoreConstant.SCREENSHOT_EXTENSION,
                        screenShotName))
                .toString());
    reportManager.addScreenshot(status, message, sanitizedPath);
  }

  /*
   * Replaces backslashes with forward slashes so href will be compatible with
   * all browsers
   */
  private String sanitizePath(String path) {
    return path.replace(FrameworkCoreConstant.BACKSLASHES, FrameworkCoreConstant.FORWARD_SLASH);
  }

  /*
   * Stores screenshot or video file to the specified folder and path with the
   * extension.
   */
  private Path storeFile(Path file, Path targetDirectory, String extension, String screenShotName) {
    try {
      Path targetPath = targetDirectory.resolve(screenShotName.concat(extension));
      log.debug("Copying file from {} to {}", file, targetPath);

      targetPath.getParent().toFile().mkdirs();
      Files.copy(file, targetPath);

      return targetPath;
    } catch (FileAlreadyExistsException feException) {
      return targetDirectory.resolve(screenShotName.concat(extension));
    } catch (IOException ioException) {
      throw new AutomationFrameworkException("Couldn't copy file for report", ioException);
    }
  }
}
