package com.nagarro.driven.core.reporting.api;

import java.io.File;

/**
 * 
 * @author nagarro
 */
public interface ISnapshotter {

	/**
	 * Takes the snapshot on pass or fail.
	 * 
	 * @return the file containing the snapshot.
	 */
	public File takeSnapshot();
}
