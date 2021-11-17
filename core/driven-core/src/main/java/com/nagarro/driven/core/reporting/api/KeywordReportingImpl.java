package com.nagarro.driven.core.reporting.api;

import com.nagarro.driven.core.config.CoreConfig;
import com.nagarro.driven.core.constant.FrameworkCoreConstant;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * KeywordReportingImpl is an aspect class which creates the third node in the report and takes
 * screenshot for every method where KeywordReporting annotation will be used.
 *
 * @author nagarro
 */
@Aspect
public class KeywordReportingImpl {
  private static final String LEVEL_TWO = "2";
  private static final String LEVEL_THREE = "3";
  private static final Logger LOG = LoggerFactory.getLogger(KeywordReportingImpl.class);

  /* The core configuration instance. */
  private static final CoreConfig.CoreConfigSpec CONFIG = CoreConfig.getInstance();
  private static final String REPORT_LEVEL = CoreConfig.getInstance().reportLevel();
  private String description;
  private String screenshotName;

  @Inject private IReportManager reportManager;
  @Inject private TestReport testReport;

  /**
   * Creates the third node in the report and takes screenshot for every method where TestParent
   * annotation will be used
   *
   * @param point, the method point
   * @return the object
   * @throws Throwable - throwable
   */
  @Around(
      "execution(* *(..)) && @annotation(com.nagarro.driven.core.reporting.api.KeywordReporting)")
  public Object around(ProceedingJoinPoint point) throws Throwable {
    if (REPORT_LEVEL.equalsIgnoreCase(LEVEL_THREE) || REPORT_LEVEL.equalsIgnoreCase(LEVEL_TWO)) {
      Method method = ((MethodSignature) point.getSignature()).getMethod();
      String methodName = method.getName();

      screenshotName = Thread.currentThread().getStackTrace()[3].getMethodName() + "_" + methodName;
      if (screenshotName.length() > 100) screenshotName = screenshotName.substring(0, 99);
      screenshotName =
          screenshotName.concat(
              new SimpleDateFormat("_" + FrameworkCoreConstant.TIMESTAMP_FORMAT)
                  .format(new Date()));

      Object[] args = point.getArgs();
      KeywordReporting annotation = method.getAnnotation(KeywordReporting.class);
      String[] annotatedDescription = annotation.value();
      StringBuilder htmlLineJoiner = new StringBuilder();
      if (StringUtils.EMPTY.equals(annotatedDescription[0])) {
        description = methodName;
      } else {
        htmlLineJoiner.append(methodName);
        if (!annotation.hideArguments()) {
          for (int i = 0; i < args.length; i++) {
            htmlLineJoiner
                .append(StringUtils.SPACE)
                .append(annotatedDescription[i])
                .append(StringUtils.SPACE)
                .append(args[i]);
          }
        }
        description = htmlLineJoiner.toString();
      }

      reportManager.createNode(description, false, true);
    }
    return takeScreenShot(point, screenshotName);
  }

  public Object takeScreenShot(ProceedingJoinPoint point, String screenshotName) throws Throwable {
    try {
      Object result = point.proceed();
      testReport.info(description);
      if (CONFIG.screenshotOnPass()) {
        testReport.takeScreenshot(TestStatus.PASS, StringUtils.EMPTY, screenshotName);
      }
      LOG.debug("Passed: {}", description);

      return result;
    } catch (Exception exception) {
      testReport.error(description);
      if (CONFIG.screenshotOnFail()) {
        testReport.takeScreenshot(TestStatus.FAIL, StringUtils.EMPTY, screenshotName);
      }
      throw exception;
    }
  }
}
