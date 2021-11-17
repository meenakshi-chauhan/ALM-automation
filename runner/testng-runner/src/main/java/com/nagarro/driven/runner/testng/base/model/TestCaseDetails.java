package com.nagarro.driven.runner.testng.base.model;

public class TestCaseDetails {
	private String testName;
	private int totalCount;
	private int passCount;
	private int failCount;
	private int skipCount;
	private double failRatio;

	public TestCaseDetails(String testname, int totalcount, int passcount, int failcount, int skipcount,
			double failratio) {
		this.testName = testname;
		this.totalCount = totalcount;
		this.passCount = passcount;
		this.failCount = failcount;
		this.skipCount = skipcount;
		this.failRatio = failratio;
	}

	public double getFailRatio() {
		return failRatio;
	}

	public void setFailRatio(double failRatio) {
		this.failRatio = failRatio;
	}

	public int getSkipCount() {
		return skipCount;
	}

	public void setSkipCount(int skipCount) {
		this.skipCount = skipCount;
	}

	public int getFailCount() {
		return failCount;
	}

	public void setFailCount(int failCount) {
		this.failCount = failCount;
	}

	public int getPassCount() {
		return passCount;
	}

	public void setPassCount(int passCount) {
		this.passCount = passCount;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

}
