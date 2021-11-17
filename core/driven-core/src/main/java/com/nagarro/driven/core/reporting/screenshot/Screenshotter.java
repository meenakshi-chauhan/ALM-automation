package com.nagarro.driven.core.reporting.screenshot;

import com.google.common.eventbus.Subscribe;
import com.nagarro.driven.core.constant.FrameworkCoreConstant;
import com.nagarro.driven.core.util.AutomationFrameworkException;
import com.nagarro.driven.core.util.EventBusHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Screenshotter is used to capture the screenshot.
 * 
 * @author nagarro
 */
public class Screenshotter {

	private static final Logger log = LoggerFactory.getLogger(Screenshotter.class);
	public static final Screenshotter INSTANCE = new Screenshotter();

	/*
	 * Default constructor of Screenshotter.
	 */
	private Screenshotter() {
	}

	/**
	 * This method is used to register the screenshot event in the eventbus.
	 */
	public static void init() {
		log.debug("Registering the screenshot event in eventbus");
		EventBusHolder.register(INSTANCE);
	}

	/**
	 * Takes the screenshot whenever the screenshot event is posted.
	 * 
	 * @param event,
	 *            the screenshot event
	 */
	@Subscribe
	public static void screenshotEvent(ScreenshotEvent event) {
		log.debug("Taking screenshot using AWT robot.");
		try {
			Rectangle screenSize = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
			log.debug("Display size: {}", screenSize);
			BufferedImage screenshotImage = new Robot().createScreenCapture(screenSize);
			log.debug("Screenshot taken");
			File imageFile = File.createTempFile(FrameworkCoreConstant.SCREENSHOT_TEMP_FILE_NAME,
					FrameworkCoreConstant.SCREENSHOT_EXTENSION);
			ImageIO.write(screenshotImage, FrameworkCoreConstant.SCREENSHOT_EXTENSION_TYPE, imageFile);
			event.addScreenshot(imageFile);
		} catch (AWTException | IOException e) {
			throw new AutomationFrameworkException("Couldn't take screenshot");
		}
	}

}
