package com.nagarro.driven.core.reporting.api;

import org.apache.commons.lang3.StringUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to indicate that a method is a business keyword and should be reported in the report.
 * Its optional value can be used as a node name for child node. Otherwise the method name will be
 * used.
 *
 * @author nagarro
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface KeywordReporting {

  String[] value() default StringUtils.EMPTY;

  /**
   * When {@code} hideArguments is set to true, this annotation parameter list won't be reported,
   * ie. when passing passwords or other sensitive data
   *
   * @return if method arguments should be hidden
   */
  boolean hideArguments() default false;
}
