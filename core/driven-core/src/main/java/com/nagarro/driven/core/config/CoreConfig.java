package com.nagarro.driven.core.config;

import java.nio.file.Path;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;
import org.aeonbits.owner.ConfigFactory;
import org.aeonbits.owner.Mutable;

/**
 * This class is used to create the instance of the core config and contains interface of
 * CoreConfigSpec.
 *
 * @author nagarro
 */
public class CoreConfig {

  /* The instance of CoreConfigSpec */
  private static final CoreConfigSpec INSTANCE =
      ConfigFactory.create(CoreConfigSpec.class, System.getProperties(), System.getenv());

  /**
   * Gets the instance of CoreConfigSpec.
   *
   * @return the instance of CoreConfigSpec.
   */
  public static CoreConfigSpec getInstance() {
    return INSTANCE;
  }

  @LoadPolicy(LoadType.MERGE)
  @Sources({"classpath:com/nagarro/driven/core/config/CoreConfig.properties"})
  public interface CoreConfigSpec extends Mutable {

    @Key("core.report.path")
    @DefaultValue("C:/driven")
    @ConverterClass(PathConverter.class)
    Path reportPath();

    @Key("core.report.name")
    @DefaultValue("report.html")
    String reportName();

    @Key("core.report.screenshot.pass")
    @DefaultValue("true")
    boolean screenshotOnPass();

    @Key("core.report.screenshot.fail")
    @DefaultValue("true")
    boolean screenshotOnFail();

    @Key("core.report.level.business")
    @DefaultValue("false")
    boolean businessReport();

    @Key("core.report.level.test.automation")
    @DefaultValue("true")
    boolean testAutomationReport();

    @Key("core.web.element.file.type")
    @DefaultValue("XML")
    String webElementFileType();

    @Key("core.util.Waiter.waitSeconds")
    @DefaultValue("120")
    long waitSeconds();

    @Key("core.util.Waiter.pauseMilliseconds")
    @DefaultValue("1000")
    long pauseMilliseconds();

    @Key("core.gspec.file.path")
    @DefaultValue(
        "D:\\A2A_DRIVEN\\root\\project_pom\\pom_espo_galen_tests\\src\\test\\resources\\gspecs")
    String gspecFilePath();

    @Key("core.enable.TestRail")
    @DefaultValue("false")
    boolean enableTestRail();

    @Key("core.report.level")
    @DefaultValue("3")
    String reportLevel();

    @Key("jira.bug.logging.on.fail")
    @DefaultValue("false")
    boolean enableJiraBugLogging();

    @Key("reporting.extent.log.issue.id")
    @DefaultValue("false")
    boolean extentReportIssueLogging();

    @Key("self.healing.enable")
    @DefaultValue("false")
    boolean isSelfHealingEnabled();

    @Key("thread.executor.wait.seconds")
    @DefaultValue("30")
    int threadExecutorWaitSeconds();
  }
}
