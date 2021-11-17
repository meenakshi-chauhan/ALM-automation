package com.nagarro.driven.runner.testng.base.config;

import com.google.inject.Stage;
import java.util.concurrent.TimeUnit;

import org.aeonbits.owner.Config;
import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;
import org.aeonbits.owner.ConfigFactory;

/**
 * This class is used to create the instance of the core config and contains interface of
 * CoreConfigSpec.
 *
 * @author nagarro
 */
public class ExecutionConfig {

  /* The instance of CoreConfigSpec */
  private static final ExecutionConfigSpec INSTANCE =
      ConfigFactory.create(ExecutionConfigSpec.class, System.getProperties(), System.getenv());

  /**
   * Gets the instance of CoreConfigSpec.
   *
   * @return the instance of CoreConfigSpec.
   */
  public static ExecutionConfigSpec getInstance() {
    return INSTANCE;
  }

  @LoadPolicy(LoadType.MERGE)
  @Sources({"classpath:com/nagarro/driven/runner/testng/base/config/ExecutionConfig.properties"})
  public interface ExecutionConfigSpec extends Config {

    @Key("testng.runner.smart.execution")
    @DefaultValue("false")
    boolean smartExecutionStatus();

    @Key("testng.runner.retry.execution.for.failed.testcases")
    @DefaultValue("false")
    boolean retryExecutionForFailedTestcases();

    @Key("max.retry.count")
    @DefaultValue("1")
    int maxRetryCount();

    @Key("testng.testdetails.Table.Name")
    @DefaultValue("TestDetails")
    String testdetailsTableName();

    @Key("testng.testdetails.Table.toBe.updated")
    @DefaultValue("false")
    boolean updateTestdetailsTableFlag();

    @Key("testng.release.Date")
    @DefaultValue("")
    String releaseDate();

    @Key("testng.runner.total.priority.bands")
    @DefaultValue("4")
    int totalPriorityBands();

    @Key("testng.runner.timebox.time")
    @DefaultValue("1")
    double timeboxTimeLimit();

    /** Time unit can be hours, minutes and seconds by default it is minutes */
    @Key("testng.runner.timebox.time.unit")
    @DefaultValue("SECONDS")
    TimeUnit timeboxTimeLimitUnit();

    @Key("testng.runner.timebox.execution.flag")
    @DefaultValue("false")
    boolean timeboxExecutionFlag();

    @Key("testng.testdetails.database.ip")
    @DefaultValue("jdbc:mariadb://localhost")
    String databaseIp();

    @Key("testng.testdetails.database.port.no")
    @DefaultValue("3306")
    String databasePortNo();

    @Key("testng.testdetails.database.name")
    @DefaultValue("driven")
    String databaseName();

    @Key("testng.testdetails.database.username")
    @DefaultValue("root")
    String databaseUserName();

    @Key("testng.testdetails.database.password")
    @DefaultValue("root")
    String databasePassword();

    @Key("testng.live.dashboard.flag")
    @DefaultValue("false")
    boolean dashboardFlag();

    @Key("testng.injection.stage")
    @DefaultValue("DEVELOPMENT")
    Stage injectionStage();
  }
}
