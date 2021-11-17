package com.nagarro.driven.core;

public interface ITestCaseCount {

	public int getPassCount();

	public void setPassCount(int passCount);

	public int getKnownIssueCount();

	public void setKnownIssueCount(int knownIssueCount);

	public int getNewIssueCount();

	public void setNewIssueCount(int newIssueCount);

	public int getSkipCount();

	public void setSkipCount(int skipCount);
}
