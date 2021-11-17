package com.nagarro.driven.client.selenium;

import com.nagarro.driven.client.selenium.report.Screenshot;
import com.nagarro.driven.core.driver.api.DriverOptions;
import com.nagarro.driven.core.reporting.api.TestReportLogger;
import com.nagarro.driven.core.reporting.screenshot.SnapshotterManager;
import com.nagarro.driven.core.util.AutomationFrameworkException;
import com.nagarro.driven.core.webdriver.IWebDriverFactory;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;

/**
 * Creates the selenium driver options and hosts some common methods
 *
 * @author nagarro
 */
public abstract class SeleniumAbstractDriverFactory
        implements IWebDriverFactory<SeleniumAbstractDriver> {
    protected static boolean noScreenShotterFound() {
        return SnapshotterManager.getSnapshotter().stream().noneMatch(s -> s instanceof Screenshot);
    }

    /**
     * Creates the capabilities of chrome by setting various options in it.
     *
     * @return the capabilities of chrome.
     */
    protected ChromeOptions createChromeOptions() {
        ChromeOptions options = new ChromeOptions();

        options.addArguments("--no-sandbox");

        // Start Chrome w/ disabled extensions
        options.addArguments("--disable-extensions");
        options.addArguments("--lang=eng");

        // Start Chrome in screen
        options.addArguments("--start-maximized");

        options.addArguments("--disable-notifications");

        return options;
    }

    /**
     * Creates the capabilities of firefox by setting various options in it.
     *
     * @return the capabilities of firefox.
     */
    protected FirefoxOptions createFirefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        FirefoxProfile profile = new FirefoxProfile();

        // Start Mozilla with disable web notifications
        profile.setPreference("dom.webnotifications.enabled", false);

        // Allows the untrusted websites to run in the browser
        profile.setPreference("webdriver_assume_untrusted_issuer", true);

        // Start Mozilla assuming the Untrusted certificates accepting
        profile.setPreference("setAssumeUntrustedCertificateIssuer", true);
        profile.setPreference("security.enterprise_roots.enabled", true);
        options.setProfile(profile);
        return options;
    }

    /**
     * Creates the capabilities of IE by setting various options in it.
     *
     * @return the capabilities of IE.
     */
    protected InternetExplorerOptions createIEOptions() {
        InternetExplorerOptions options = new InternetExplorerOptions();

        // Start IE ignoring security domains and popups on the website
        options.setCapability(InternetExplorerDriver.NATIVE_EVENTS, false);
        options.setCapability(
                InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);

        // Start IE, to run with default zoom size, and prevents it from
        // throwing error
        options.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
        return options;
    }

    /**
     * This method should never be used any more, use {@link #createConcrete(DriverOptions,
     * TestReportLogger)} instead
     */
    @Override
    public SeleniumAbstractDriver createConcrete(DriverOptions options) {
        throw new AutomationFrameworkException(
                "Use createConcrete(DriverOptions, TestReportLogger) in order to create a valid Driver's instance");
    }
}
