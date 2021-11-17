package com.nagarro.driven.client.selenium;

import com.nagarro.driven.core.driver.api.DriverOptions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Creates the selenium remote driver for the selenium grid.
 *
 * @author nagarro
 */
public class SeleniumGrid {

    private static final Logger log = LoggerFactory.getLogger(SeleniumGrid.class);
    private final DesiredCapabilities desiredCapabilities;
    private URL gridUrl = null;

    /**
     * Sets the grid url and desired capabilities of the browser.
     *
     * @param gridUrl             the grid url
     * @param desiredCapabilities the capabilities of the browser
     */
    public SeleniumGrid(URL gridUrl, DesiredCapabilities desiredCapabilities) {
        this.gridUrl = gridUrl;
        this.desiredCapabilities = desiredCapabilities;
    }

    /**
     * Sets the driver options.
     */
    public SeleniumGrid(DriverOptions options) {
        this(options.getGridUrl(), options.getCapabilities());
    }

    /**
     * Creates the remote web driver for the selenium grid according to the capabilities and url
     * provided.
     *
     * @return the web driver
     */
    public WebDriver create() {
        log.debug("Creating Grid WebDriver at {} with capabilities {}", gridUrl, desiredCapabilities);
        RemoteWebDriver driver = new RemoteWebDriver(gridUrl, desiredCapabilities);
        log.debug("Remote web driver created : {}", driver);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        return driver;
    }
}
