package com.nagarro.driven.core.reporting.screenshot;

import java.util.ArrayList;
import java.util.List;

import com.nagarro.driven.core.reporting.api.ISnapshotter;


/**
 * SnapshotterManager manages the list of snapshotter interface instances.
 * 
 * @author nagarro
 */
public class SnapshotterManager {

    /* The list of ISnapshotter. */
	private static final List<ISnapshotter> snapshotterImpl = new ArrayList<>();

	/*
	 * Default constructor.
	 */
	private SnapshotterManager() {
		// no instantiation allowed
	}

	/**
	 * Adds the ISnapshotter instances to the list.
	 * 
	 * @param snapshotter
	 *            the ISnapshotter instances
	 */
	public static void addSnapshotter(final ISnapshotter snapshotter) {
		snapshotterImpl.add(snapshotter);
	}

	/**
	 * Gets the list of ISnapshotter instances.
	 * 
	 * @return the list of ISnapshotter instances
	 */
	public static List<ISnapshotter> getSnapshotter() {
		return snapshotterImpl;
	}
}
