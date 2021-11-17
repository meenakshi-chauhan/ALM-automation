package com.nagarro.driven.reporter.extentreport;

import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.nagarro.driven.core.config.CoreConfig;
import com.nagarro.driven.core.reporting.api.IReportManager;
import com.nagarro.driven.core.reporting.api.IReporter;
import com.nagarro.driven.core.reporting.api.ITest;
import com.nagarro.driven.core.reporting.api.TestStatus;
import com.nagarro.driven.core.util.ReportUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Path;

import static com.aventstack.extentreports.reporter.configuration.Theme.STANDARD;
import static com.nagarro.driven.core.reporting.api.TestStatus.*;
import static com.nagarro.driven.core.util.ReportUtil.makeDirectory;
import static com.nagarro.driven.reporter.extentreport.ExtentReportConstant.REPORT_FOLDER_NAME;
import static com.nagarro.driven.reporter.extentreport.ExtentReportConstant.SCREENSHOT_FOLDER_NAME;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

/**
 * ExtentReportManagerImpl implements the method for extent report.
 *
 * @author nagarro
 */
@Singleton
public class ExtentReportManagerImpl implements IReportManager {

  private static final Logger log = LoggerFactory.getLogger(ExtentReportManagerImpl.class);

  /* The instance of extent report impl. */
  private final ExtentReportImpl extentReportImpl;

  /* The instance of extent report html reporter. */
  private final ExtentHtmlReporter htmlReporter;

  /* The instance of extent test report impl. */
  private final ExtentTestImpl extentTestImpl;

  // Direct Singleton injection
  @Inject
  private ExtentReportManagerImpl(
      ExtentTestImpl extentTestImpl, ExtentReportImpl extentReportImpl) {
    this.extentTestImpl = extentTestImpl;
    this.extentReportImpl = extentReportImpl;
    Path reportFullPath = prepareFilesystemAndGetReportFullPath();
    this.htmlReporter = initializeHtmlReporter(reportFullPath, extentReportImpl);
  }

  private String readScriptFile(String fileName) {
    StringWriter script = new StringWriter();
    try {
      InputStream inputStream =
          requireNonNull(
              ExtentReportManagerImpl.class.getClassLoader().getResourceAsStream(fileName));
      IOUtils.copy(inputStream, script, UTF_8);
    } catch (Exception e) {
      log.error(format("Unable to read script from file %s", fileName));
    }
    return script.toString();
  }

  /* Creates the extent report and make some custom settings. */
  private ExtentHtmlReporter initializeHtmlReporter(
      Path fullPath, ExtentReportImpl extentReportImpl) {
    ExtentHtmlReporter newHtmlReporter = new ExtentHtmlReporter(fullPath.toString());
    newHtmlReporter.config().setTheme(STANDARD);
    newHtmlReporter.config().setJS("$(\"#charts-row .panel-name\").eq(2).text(\"Test Steps\")");
    if (CoreConfig.getInstance().extentReportIssueLogging()) {
      setScript(readScriptFile("BugReporting.js"));
    }
    extentReportImpl.attachReporter(newHtmlReporter);
    return newHtmlReporter;
  }

  @Override
  public IReporter getReporter() {
    return extentReportImpl;
  }

  @Override
  public ITest getTest() {
    return extentTestImpl;
  }

  @Override
  public void createNode(String nodeName, boolean isParent, boolean isthirdNode) {
    extentTestImpl.createNode(nodeName, isParent, isthirdNode, extentReportImpl);
  }

  @Override
  public void clearParentTest() {
    extentTestImpl.clearParentTest();
  }

  @Override
  public void clearChildTest() {
    extentTestImpl.clearChildTest();
  }

  @Override
  public void addScreenshot(TestStatus status, String message, String sanitizedPath)
      throws IOException {
    if (PASS == status) {
      extentTestImpl.passScreenshot(message, sanitizedPath);
    } else if (FAIL == status) {
      extentTestImpl.failScreenshot(message, sanitizedPath);
    } else if (INFO == status) {
      extentTestImpl.infoScreenshot(message, sanitizedPath);
    }
  }

  @Override
  public void setScript(String script) {
    htmlReporter.config().setJS(htmlReporter.config().getJS() + ';' + script);
  }

  private Path prepareFilesystemAndGetReportFullPath() {
    Path reportPath =
        CoreConfig.getInstance().reportPath().resolve(REPORT_FOLDER_NAME).toAbsolutePath();
    makeDirectory(reportPath);
    String fileName = CoreConfig.getInstance().reportName();
    Path fullPath = reportPath.resolve(fileName).toAbsolutePath();
    log.debug(
        "Creating new report, reporting to directory {}, using filename {} (full path: {})",
        reportPath,
        fileName,
        fullPath);
    cleanupSnapshotDirectoryIfNeeded(reportPath.resolve(SCREENSHOT_FOLDER_NAME));
    return fullPath;
  }

  private void cleanupSnapshotDirectoryIfNeeded(Path screenshotPath) {
    try {
      File file = new File(screenshotPath.toString());
      if (file.exists() && file.isDirectory()) {
        ReportUtil.archiveDefaultReport();
        FileUtils.cleanDirectory(file);
      } else {
        makeDirectory(screenshotPath);
      }
    } catch (IOException e) {
      log.error(
          "Exception occurred while cleaning screenshot directory due to: {}", e.getMessage());
    }
  }
}
