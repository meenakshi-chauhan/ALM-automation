package com.nagarro.driven.runner.testng.base;

import com.nagarro.driven.runner.testng.base.config.ExecutionConfig;
import com.nagarro.driven.runner.testng.base.model.TestCaseDetails;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import static java.lang.String.format;

public class TestRecordMaintain extends RiskBasedExecutionTestng {

  /** This method checks the Release date and give a call to maintainDetails accordingly */
  public synchronized void maintain(String testName, int status) {
    if (updateTestdetailsFileFlag) {
      if (RELEASE_DATE.isEmpty()) {
        maintainDetails(testName, status, testDetails);
      } else {
        maintainDetails(testName, status, testDetails);
        maintainDetails(testName, status, releasetestDetails);
      }
    }
  }

  /**
   * This methods maintain the test deatils based on their execution results public static final int
   * FAILURE 2 public static final int SKIP 3 public static final int STARTED 16 public static final
   * int SUCCESS 1
   */
  private void maintainDetails(String testName, int status, List<TestCaseDetails> testinfo) {
    int iterator;
    for (iterator = 0; iterator < testinfo.size(); iterator++) {
      TestCaseDetails temp = testinfo.get(iterator);
      if (testName.equals(temp.getTestName())) {
        testinfo.set(iterator, modifydetails(temp, status));
        break;
      }
    }
    if (iterator == testinfo.size()) {
      TestCaseDetails temp = createNewTest(testName);
      testinfo.add(modifydetails(temp, status));
    }
  }

  /** Creates new row of data for a new Test */
  private TestCaseDetails createNewTest(String testName) {
    return new TestCaseDetails(testName, 0, 0, 0, 0, 0);
  }

  /** This method gives Ratio of two values given */
  private double ratio(int numerator, int denominator) {
    return (double) numerator / denominator;
  }

  private double getfailRatio(TestCaseDetails test) {
    if (test.getFailCount() == 0) {
      return ratio(test.getSkipCount(), test.getTotalCount() + test.getPassCount());
    } else {
      return ratio(test.getFailCount(), test.getTotalCount() - test.getSkipCount());
    }
  }

  /** This method modifies details of test given according to its status provided */
  private TestCaseDetails modifydetails(TestCaseDetails test, int status) {
    test.setTotalCount(test.getTotalCount() + 1);
    switch (status) {
      case 1:
        test.setPassCount(test.getPassCount() + 1);
        break;
      case 2:
        test.setFailCount(test.getFailCount() + 1);
        break;
      case 3:
      case -1:
        test.setSkipCount(test.getSkipCount() + 1);
        break;
      default:
        break;
    }
    test.setFailRatio(getfailRatio(test));
    return test;
  }

  /** This method writes the complete testdetails into database */
  public void saveDetails() {
    if (updateTestdetailsFileFlag) {
      if (RELEASE_DATE.isEmpty()) {
        sendDataToDatabase(TABLE_NAME, testDetails);
      } else {
        sendDataToDatabase(TABLE_NAME, testDetails);
        sendDataToDatabase(RELEASE_TABLE_NAME, releasetestDetails);
      }
    }
  }

  /** Create the table into database if not exists */
  private boolean createTableIfNotExists(String tableName, Statement stmt) {
    try {
      String sqlQuery =
          "create database if not exists " + ExecutionConfig.getInstance().databaseName() + ";";
      stmt.execute(sqlQuery);
      stmt.execute("use " + ExecutionConfig.getInstance().databaseName() + ";");
      sqlQuery =
          "CREATE  TABLE IF NOT EXISTS `"
              + tableName
              + "` (\r\n"
              + "  `TestName` VARCHAR(150) NOT NULL ,\r\n"
              + "  `TotalCount` INT ,\r\n"
              + "  `PassCount` INT ,\r\n"
              + "  `FailCount` INT ,\r\n"
              + "  `SkipCount` INT ,\r\n"
              + "  `FailRatio` DOUBLE ,\r\n"
              + "  PRIMARY KEY (`TestName`) );";
      return stmt.execute(sqlQuery);
    } catch (Exception e) {
      LOG.debug("Unable to create table database : {}", e.getMessage());
      return false;
    }
  }

  /** Writes the information in "data" into the table with name "tableName" in database */
  private void sendDataToDatabase(String tableName, List<TestCaseDetails> data) {
    try {
      try (Connection con =
          DriverManager.getConnection(
              ExecutionConfig.getInstance().databaseIp()
                  + ":"
                  + ExecutionConfig.getInstance().databasePortNo(),
              ExecutionConfig.getInstance().databaseUserName(),
              ExecutionConfig.getInstance().databasePassword())) {
        Statement stmt = con.createStatement();
        boolean newTableCreated = createTableIfNotExists(tableName, stmt);
        stmt.execute("use " + ExecutionConfig.getInstance().databaseName());
        if (!newTableCreated) {
          stmt.execute("DELETE FROM `" + tableName + "`");
        }
        String testInfoInsertQuery =
            "INSERT INTO `"
                + tableName
                + "`(TestName, "
                + "TotalCount, PassCount, FailCount, SkipCount, FailRatio) values";

        for (TestCaseDetails test : data) {
          String sqlQuery =
              testInfoInsertQuery
                  + "('"
                  + test.getTestName()
                  + "','"
                  + test.getTotalCount()
                  + "','"
                  + test.getPassCount()
                  + "','"
                  + test.getFailCount()
                  + "','"
                  + test.getSkipCount()
                  + "','"
                  + test.getFailRatio()
                  + "')";
          executeStatement(sqlQuery, con);
        }
      }

    } catch (Exception e) {
      LOG.debug("Unable to write execution data into database : {}", e.getMessage());
    }
  }

  private void executeStatement(String sqlQuery, Connection connection) {
    try (PreparedStatement stmt1 =
        connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
      stmt1.executeUpdate(sqlQuery);
    } catch (Exception exception) {
      LOG.error(format("Unable to execute sql query : %s", sqlQuery), exception);
    }
  }
}
