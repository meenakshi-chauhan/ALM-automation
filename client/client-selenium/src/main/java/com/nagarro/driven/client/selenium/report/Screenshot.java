package com.nagarro.driven.client.selenium.report;

import com.nagarro.driven.client.selenium.SeleniumDriverFactory;
import com.nagarro.driven.core.constant.FrameworkCoreConstant;
import com.nagarro.driven.core.reporting.api.ISnapshotter;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Screenshot is an implementing class of ISnapshotter to take the snapshot on failure or pass.
 *
 * @author nagarro
 */
public class Screenshot implements ISnapshotter {

    private static final Logger log = LoggerFactory.getLogger(Screenshot.class);

    @Override
    public File takeSnapshot() {
        final WebDriver webDriver = SeleniumDriverFactory.getDriver();
        final TakesScreenshot screenshot = ((TakesScreenshot) webDriver);
        final File srcFile = screenshot.getScreenshotAs(OutputType.FILE);
        File imageFile = null;

        try {
            imageFile =
                    File.createTempFile(
                            FrameworkCoreConstant.SCREENSHOT_TEMP_FILE_NAME,
                            FrameworkCoreConstant.SCREENSHOT_EXTENSION);
            FileUtils.copyFile(srcFile, imageFile);
        } catch (final IOException e) {
            log.error("Exception occured while taking and saving snapshot in selenium.");
        }

        return imageFile;
    }
}
