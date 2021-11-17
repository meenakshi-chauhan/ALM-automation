package com.nagarro.driven.runner.testng.base.extentreport;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Bug {
	String jiraIssueId() default "";
    String[] tags() default "";
}

