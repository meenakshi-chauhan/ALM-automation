package com.nagarro.driven.core.util;

import com.nagarro.driven.core.config.CoreConfig;
import com.nagarro.driven.core.constant.FrameworkCoreConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Utility class containing all the methods used in reporting.
 *
 * @author nagarro
 */
public class ReportUtil {

  private static final Logger log = LoggerFactory.getLogger(ReportUtil.class);
  private static final CoreConfig.CoreConfigSpec config = CoreConfig.getInstance();

  private ReportUtil() {
    // not allowed
  }

  /**
   * Returns a unique text which is the string of the current datetime in the
   * FrameworkCoreConstant.TIMESTAMP_FORMAT
   *
   * @return unique generated name.
   */
  public static String getCurrentDateTimeAsString() {
    SimpleDateFormat dateFormat = new SimpleDateFormat(FrameworkCoreConstant.TIMESTAMP_FORMAT);
    Date now = new Date();
    String uniqueText = dateFormat.format(now);
    log.info("Unique name generated");
    return (uniqueText);
  }

  /**
   * Returns a unique screenshot name with test name append
   *
   * @return screenshot name.
   */
  public static String getScreenshotName(String testName) {
    if (testName.length() > 100) testName = testName.substring(0, 99);
    return testName + "_" + getCurrentDateTimeAsString() + "_FAILED";
  }

  /** Archive the previous report. */
  public static void archiveDefaultReport() {
    String archiveFileName =
        (ReportUtil.getCurrentDateTimeAsString() + FrameworkCoreConstant.REPORT_ARCHIVE_EXTENSION)
            .replace(':', '_');
    Path archiveFile =
        config
            .reportPath()
            .resolve(FrameworkCoreConstant.ARCHIVE_FOLDER_NAME)
            .resolve(archiveFileName);
    archiveReport(archiveFile);
  }

  /** Archive the report at given file. */
  public static void archiveReport(Path archiveDestinationFile) {
    makeDirectory(archiveDestinationFile.getParent());
    try {
      zipFolder(
          config.reportPath().resolve(FrameworkCoreConstant.REPORT_FOLDER_NAME),
          archiveDestinationFile);
    } catch (Exception archiveException) {
      log.error(
          "Error in creating zip folder for archive due to: {}", archiveException.getMessage());
    }
  }

  /**
   * Creates a directory if it doesn't already exist on a specific path.
   *
   * @param filePath path of the file to be created
   */
  public static void makeDirectory(Path filePath) {
    if (!filePath.toFile().exists()) {
      filePath.toFile().mkdirs();
    }
  }

  /*
   * Zip the report created.
   */
  private static void zipFolder(Path dataPath, Path zipPath) throws IOException {
    try (ZipOutputStream zipOutput = new ZipOutputStream(new FileOutputStream(zipPath.toFile()))) {
      Files.walkFileTree(
          dataPath,
          new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes)
                throws IOException {
              zipOutput.putNextEntry(new ZipEntry(dataPath.relativize(file).toString()));
              Files.copy(file, zipOutput);
              zipOutput.closeEntry();
              return FileVisitResult.CONTINUE;
            }
          });
    }
  }
}
