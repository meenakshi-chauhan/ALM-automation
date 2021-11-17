package com.nagarro.driven.runner.cucumbertestng.base;

import com.nagarro.driven.core.config.CoreConfig;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public class CucumberListener implements ISuiteListener {

  private static final Logger log = LoggerFactory.getLogger(CucumberListener.class);
  private static final Path REPORT_PATH = CoreConfig.getInstance().reportPath();
  private static final String REPORT_FOLDER_NAME = "Cucumber_Reports";
  private static final String REPORT_FILE = REPORT_PATH + "/" + REPORT_FOLDER_NAME;

  @Override
  public void onStart(ISuite suite) {
    // not necessary
  }

  @Override
  public void onFinish(ISuite suite) {
    try {
      File reportFolder = new File("target/");
      File reportOutputDirectory = new File(REPORT_FILE);

      if (reportFolder.exists()) {
        List<String> fileNames =
            List.of(requireNonNull(reportFolder.list(new WildcardFileFilter("*.json"))));
        List<String> jsonFiles =
            fileNames.stream()
                .map(s -> reportFolder.getAbsolutePath() + "/" + s)
                .collect(Collectors.toList());
        Configuration configuration = new Configuration(reportOutputDirectory, suite.getName());
        configuration.setStatusFlags(true, true, true);

        ReportBuilder reportBuilder = new ReportBuilder(jsonFiles, configuration);
        reportBuilder.generateReports();
        log.info("Report Generated : {}", configuration.getReportDirectory());
      } else {
        log.warn("No reporting files found!");
      }
    } catch (Exception e) {
      log.error("Error occurred finishing the report", e);
    }
  }
}
