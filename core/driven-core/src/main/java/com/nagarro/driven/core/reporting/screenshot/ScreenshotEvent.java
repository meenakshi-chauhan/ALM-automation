package com.nagarro.driven.core.reporting.screenshot;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nagarro.driven.core.util.EventBusHolder;

/**
 * ScreenshotEvent is used to post the screenshot event to take screenshot.
 * 
 * @author nagarro
 */
public class ScreenshotEvent {

	private final List<Path> screenshotPathList = new ArrayList<>();
	private static final Logger log = LoggerFactory.getLogger(ScreenshotEvent.class);

	/**
	 * Collects the screenshot by posting the event of screenshot.
	 * 
	 * @return List of all the paths of screenshots.
	 */
	public static List<Path> collectScreenshot() {
		ScreenshotEvent event = new ScreenshotEvent();
		log.debug("Posting the screenshot event.");
		EventBusHolder.post(event);
		return event.getScreenshotPathList();
	}

	/**
	 * Add the file path to the screenshot list.
	 * 
	 * @param file,
	 *            the file of screenshot.
	 */
	public void addScreenshot(File file) {
		log.debug("Adding the file path {} to the list", file.toPath());
		screenshotPathList.add(file.toPath());
	}

	/**
	 * Add the screenshot path in the screenshot list.
	 * 
	 * @param path,
	 *            the path of the screenshot
	 */
	public void addScreenshot(Path path) {
		log.debug("Adding the screenshot path {} to the list", path);
		screenshotPathList.add(path);
	}

	/**
	 * Gets the list of all the paths of screenshot.
	 * 
	 * @return the list of all the paths of screenshot.
	 */
	public List<Path> getScreenshotPathList() {
		return new ArrayList<>(screenshotPathList);
	}
}