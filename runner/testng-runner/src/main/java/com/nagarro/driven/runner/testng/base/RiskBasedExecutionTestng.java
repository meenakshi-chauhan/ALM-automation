package com.nagarro.driven.runner.testng.base;

import com.google.common.collect.ImmutableList;
import com.nagarro.driven.runner.testng.base.config.ExecutionConfig;
import com.nagarro.driven.runner.testng.base.model.TestCaseDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.ITestAnnotation;
import org.testng.internal.annotations.IAnnotationTransformer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RiskBasedExecutionTestng implements IAnnotationTransformer {

  protected static final Logger LOG = LoggerFactory.getLogger(RiskBasedExecutionTestng.class);
  private static final int NUMBER_OF_PRIORITY_BANDS =
      ExecutionConfig.getInstance().totalPriorityBands();
  protected List<TestCaseDetails> testDetails = new ArrayList<>();
  protected List<TestCaseDetails> releasetestDetails = new ArrayList<>();
  private static float rangeBand = 1 / (float) NUMBER_OF_PRIORITY_BANDS;
  public static final String RELEASE_DATE = ExecutionConfig.getInstance().releaseDate();
  protected static final String TABLE_NAME = ExecutionConfig.getInstance().testdetailsTableName();
  protected static final String RELEASE_TABLE_NAME = RELEASE_DATE + "_TestExecutionDetails";
  protected final boolean updateTestdetailsFileFlag =
      ExecutionConfig.getInstance().updateTestdetailsTableFlag();
  private final boolean smartExecutionFlag = ExecutionConfig.getInstance().smartExecutionStatus();

  /** Constructor to initialise data from database accordingly based on the RELEASE_DATE input. */
  public RiskBasedExecutionTestng() {
    if (RELEASE_DATE.isEmpty()) {
      if (testDetails.isEmpty()) {
        testDetails = readTestDetailsFromDatabase(TABLE_NAME);
      }
    } else {
      if (releasetestDetails.isEmpty()) {
        releasetestDetails = readTestDetailsFromDatabase(RELEASE_TABLE_NAME);
      }
      if (testDetails.isEmpty()) {
        testDetails = readTestDetailsFromDatabase(TABLE_NAME);
      }
    }
  }

  /**
   * This method is used to assign priority to the testCases based on their fail ratio.
   *
   * @see org.testng.IAnnotationTransformer#transform(org.testng.annotations.ITestAnnotation,
   *     java.lang.Class, java.lang.reflect.Constructor, java.lang.reflect.Method)
   */
  @Override
  public void transform(
      ITestAnnotation testAnnotation, Class class1, Constructor constructor, Method method) {

    if (smartExecutionFlag) {
      (RELEASE_DATE.isEmpty() ? testDetails.stream() : releasetestDetails.stream())
          .filter(
              testCaseDetails ->
                  testCaseDetails
                      .getTestName()
                      .equals(method.getDeclaringClass().getName() + "_" + method.getName()))
          .forEach(
              testCaseDetails -> assignPriority(testAnnotation, testCaseDetails.getFailRatio()));
    }
  }

  /** Assigns the priority to testMethod according to range in which it falls */
  private static void assignPriority(ITestAnnotation testAnnotation, double ratio) {
    if (Float.isInfinite(rangeBand)) {
      rangeBand = 1;
    }
    if (ratio == 1.0) {
      testAnnotation.setPriority(0);
    } else {
      for (int priority = 0; priority < NUMBER_OF_PRIORITY_BANDS; priority++) {
        if (priority == 0) {
          if (0.0 <= ratio && rangeBand > ratio) {
            testAnnotation.setPriority(NUMBER_OF_PRIORITY_BANDS - 1 - priority);
          }
        } else {
          setHigherPriority(priority, ratio, testAnnotation);
        }
      }
    }
  }

  private static void setHigherPriority(
      int priority, double ratio, ITestAnnotation testAnnotation) {
    double initialRange = (double) priority / (double) NUMBER_OF_PRIORITY_BANDS;
    double finalRange = initialRange + rangeBand;
    if (initialRange <= ratio && finalRange > ratio) {
      testAnnotation.setPriority(NUMBER_OF_PRIORITY_BANDS - 1 - priority);
    }
  }

  /**
   * This method returns the data stored in testDetails.
   *
   * @return ratio
   */
  public List<TestCaseDetails> getRatio() {
    return testDetails;
  }

  /** This method is for reading data from Database */
  public List<TestCaseDetails> readTestDetailsFromDatabase(String tableName) {
    List<TestCaseDetails> testdetails = new ArrayList<>();
    try (Connection con =
            DriverManager.getConnection(
                ExecutionConfig.getInstance().databaseIp()
                    + ":"
                    + ExecutionConfig.getInstance().databasePortNo(),
                ExecutionConfig.getInstance().databaseUserName(),
                ExecutionConfig.getInstance().databasePassword());
        Statement stmt = con.createStatement()) {
      String sq = "use " + ExecutionConfig.getInstance().databaseName();
      String sq1 = "select * from `" + tableName + "` order by FailRatio desc";
      stmt.execute(sq);
      try (ResultSet r = stmt.executeQuery(sq1)) {
        while (r.next()) {
          TestCaseDetails test =
              new TestCaseDetails(
                  r.getString(1),
                  r.getInt(2),
                  r.getInt(3),
                  r.getInt(4),
                  r.getInt(5),
                  r.getDouble(6));
          testdetails.add(test);
        }
      }
    } catch (Exception e) {
      LOG.debug("Unable to get execution data from database", e);
    }
    return ImmutableList.copyOf(testdetails);
  }
}
