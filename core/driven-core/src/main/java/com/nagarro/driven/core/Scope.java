package com.nagarro.driven.core;

import com.google.common.collect.Maps;
import com.nagarro.driven.core.driver.api.DriverOptions;
import com.nagarro.driven.core.driver.api.IDriver;
import com.nagarro.driven.core.driver.api.IDriverFactory;
import com.nagarro.driven.core.reporting.api.TestReportLogger;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static java.lang.String.format;

/**
 * Scope is the abstract class used to create a map for drivers for different application.
 *
 * @author nagarro
 */
public abstract class Scope {
  private static final Map<String, IDriver> webDrivers = Maps.newConcurrentMap();
  private static IDriver iDriver = null;
  private static final List<String[]> issueIDList = new ArrayList<>();
  private static final Map<String, ITestCaseCount> resultCount =
      Collections.synchronizedMap(new HashMap<>());

  /**
   * Finds and creates the driver and put its entry in webDriver map.
   *
   * @param options the driver options
   * @return the driver
   */
  public static IDriver findOrCreate(DriverOptions options) {
    String key =
        format(
            "%s_%s_%s",
            Thread.currentThread().getId(), options.getDriverName(), options.getApplicationName());
    return webDrivers.computeIfAbsent(key, k -> lookUpDriverFactories(options));
  }

  /**
   * Finds and creates the driver and put its entry in webDriver map.
   *
   * @param options the driver options
   * @param testReportLogger - report logger which should be used for this driver
   * @return the driver
   */
  public static IDriver findOrCreate(DriverOptions options, TestReportLogger testReportLogger) {
    String key =
        format(
            "%s_%s_%s",
            Thread.currentThread().getId(), options.getDriverName(), options.getApplicationName());
    return webDrivers.computeIfAbsent(key, k -> lookUpDriverFactories(options, testReportLogger));
  }

  /*
   * This method is used to search which client has canIstantiate method true
   * and create the driver for that particular client only.
   */
  private static IDriver lookUpDriverFactories(DriverOptions options) {
    return lookUpDriverFactories(options, null);
  }

  /*
   * This method is used to search which client has canIstantiate method true
   * and create the driver for that particular client only.
   */
  private static synchronized IDriver lookUpDriverFactories(
      DriverOptions options, TestReportLogger testReportLogger) {
    Reflections reflections = new Reflections("com.nagarro.driven.client");
    Set<Class<? extends IDriverFactory>> driverFactorySet =
        reflections.getSubTypesOf(IDriverFactory.class);
    driverFactorySet.forEach(
        fac -> {
          IDriverFactory<?> obj;
          try {
            obj = ConstructorUtils.invokeConstructor(fac);
            boolean b =
                (boolean) fac.getMethod("canInstantiate", DriverOptions.class).invoke(obj, options);
            if (b) {
              iDriver =
                  testReportLogger == null
                      ? obj.createConcrete(options)
                      : obj.createConcrete(options, testReportLogger);
            }
          } catch (NoSuchMethodException
              | IllegalAccessException
              | InvocationTargetException
              | InstantiationException e) {
            e.printStackTrace();
          }
        });
    return iDriver;
  }

  /**
   * Removes the entry of web driver from the map.
   *
   * @param threadID the thread id
   */
  public static void removeThreadEntry(Thread threadID) {
    for (Map.Entry<String, IDriver> entry : webDrivers.entrySet()) {
      if (entry.getKey().contains(String.valueOf(threadID.getId()))) {
        IDriver driver = entry.getValue();
        driver.cleanUp();
        webDrivers.remove(entry.getKey());
      }
    }
  }

  /**
   * Get the count of each status(pass, fail, skip) for all tests of a particular Test Class
   *
   * @param className name of a class to get the count.
   * @return resultCount.
   */
  public static ITestCaseCount getResultCount(String className) {
    return resultCount.get(className);
  }

  /**
   * Set the count of each status(pass, fail, skip) of a tests for a particular Test Class
   *
   * @param className name of a class to get the count.
   * @param resultCount counts of status.
   */
  public static void setResultCount(String className, ITestCaseCount resultCount) {
    Scope.resultCount.put(className, resultCount);
  }

  /**
   * Return the issue id associated to a test case.
   *
   * @param className
   * @param testName
   * @return Issue id given in Bug annotation or return ''
   */
  public static String getIssueID(String className, String testName) {
    for (String[] s : issueIDList) {
      if (className.equals(s[0]) && testName.equals(s[1])) {
        try {
          if (s[2].length() < 1) {
            return s[2];
          } else {
            return "$" + s[2];
          }
        } catch (Exception e) {
          // No Bug Id associated to the test
        }
      }
    }
    return "";
  }

  /**
   * Set the issue id of test case in list
   *
   * @param issue
   */
  public static void setIssueID(String[] issue) {
    Scope.issueIDList.add(issue);
  }

  /**
   * Utility classes, which are collections of static members, are not meant to be instantiated.
   * Abstract utility classes, which can be extended, should not have public constructors.
   */
  private Scope() {
    throw new IllegalStateException("Utility class");
  }
}
