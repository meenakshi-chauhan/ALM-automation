package com.nagarro.driven.client.ng;

import com.nagarro.driven.client.selenium.WebLocator;
import com.nagarro.driven.client.selenium.WebLocatorLoader;
import com.nagarro.driven.core.reporting.api.TestReportLogger;
import com.paulhammant.ngwebdriver.ByAngular;
import org.openqa.selenium.WebDriver;

public class NgWebLocatorLoader {
    private NgWebLocatorLoader() {
    }

    public static WebLocator forType(String locatorType, String locatorValue) {
        return getWebLocator(locatorType, locatorValue);
    }

    /**
     * Finds the page element.
     *
     * @param pageName    page name tag in web locator file.
     * @param elementName name of element.
     * @return instance of weblocator class.
     */
    public static WebLocator findWebLocatorForElement(
            final String pageName, final String elementName, WebDriver webDriver, TestReportLogger reportLog, final Object... dynamicLocatorValue) {
        return WebLocatorLoader.findWebLocatorForElement(
                pageName, elementName, webDriver, reportLog, NgWebLocatorLoader::getWebLocator, dynamicLocatorValue);
    }

    public static WebLocator getWebLocator(final String locatorType, final String locatorValue) {
        switch (locatorType.toLowerCase()) {
            case "angular_buttontext":
                return new WebLocator(ByAngular.buttonText(locatorValue));
            case "angular_model":
                return new WebLocator(ByAngular.model(locatorValue));
            case "angular_partialbuttontext":
                return new WebLocator(ByAngular.partialButtonText(locatorValue));
            case "angular_exactbinding":
                return new WebLocator(ByAngular.exactBinding(locatorValue));
            case "angular_binding":
                return new WebLocator(ByAngular.binding(locatorValue));
            case "angular_repeater":
                return new WebLocator(ByAngular.repeater(locatorValue));
            case "angular_exactrepeater":
                return new WebLocator(ByAngular.exactRepeater(locatorValue));
            case "angular_options":
                return new WebLocator(ByAngular.options(locatorValue));
            default:
                // by default we use the regular Selenium implementation for e.g. locatorType = xpath
                return WebLocatorLoader.getWebLocator(locatorType, locatorValue);
        }
    }
}
