package com.nagarro.driven.runner.testng.base.extentreport;

import com.nagarro.driven.core.ITestCaseCount;

public class TestCaseCount implements ITestCaseCount {
	private int passCount;
	private int knownIssueCount;
	private int newIssueCount;
	private int skipCount;

	@Override
	public int getPassCount() {
		return passCount;
	}

	@Override
	public void setPassCount(int passCount) {
		this.passCount = passCount;
	}

	@Override
	public int getKnownIssueCount() {
		return knownIssueCount;
	}

	@Override
	public void setKnownIssueCount(int knownIssueCount) {
		this.knownIssueCount = knownIssueCount;
	}

	@Override
	public int getNewIssueCount() {
		return newIssueCount;
	}

	@Override
	public void setNewIssueCount(int newIssueCount) {
		this.newIssueCount = newIssueCount;
	}

	@Override
	public int getSkipCount() {
		return skipCount;
	}

	@Override
	public void setSkipCount(int skipCount) {
		this.skipCount = skipCount;

	}

	public TestCaseCount() {
		this.knownIssueCount = 0;
		this.newIssueCount = 0;
		this.passCount = 0;
		this.skipCount = 0;
	}

	@Override
	public String toString() {
		StringBuilder temp = new StringBuilder("<span style=\\\"font-size:75%;\\\"><span style=\\\"color:green;\\\">Pass : </span>");
		temp.append(this.passCount);
		temp.append(", <span style=\\\"color:blue;\\\"> Skip : </span>");
		temp.append(this.skipCount);
		temp.append(", <span style=\\\"color:orange;\\\">New Issue : </span>");
		temp.append(this.newIssueCount);
		temp.append(", <span style=\\\"color:red;\\\"> KnownIssue : </span>");
		temp.append(this.knownIssueCount);
		temp.append("</span>");
		return temp.toString();
	}
}
