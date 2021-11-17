package com.nagarro.driven.client.selenium;

import com.nagarro.driven.client.ui.api.UnexpectedClientException;
import com.nagarro.driven.core.config.CoreConfig;
import com.nagarro.driven.core.reporting.api.TestReportLogger;
import com.nagarro.driven.core.util.AutomationFrameworkException;
import com.nagarro.driven.core.weblocator.file.util.WebLocatorFileTypeFactory;
import com.nagarro.driven.core.weblocator.model.ElementProperty;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

public class WebLocatorLoader {

    /* The logger. */
    private static final Logger log = LoggerFactory.getLogger(WebLocatorLoader.class);
    private static final CoreConfig.CoreConfigSpec coreConfig = CoreConfig.getInstance();

    private WebLocatorLoader() {
    }

    /**
     * Finds the page element.
     *
     * @param pageName    page name tag in web locator file.
     * @param elementName name of element.
     * @return instance of weblocator class.
     */
    public static WebLocator findWebLocatorForElement(
            final String pageName, final String elementName, final WebDriver webdriver, TestReportLogger reportLog,
            final Object... dynamicLocatorValue) {
        return findWebLocatorForElement(
                pageName, elementName, webdriver, reportLog, WebLocatorLoader::getWebLocator, dynamicLocatorValue);
    }

    public static WebLocator findWebLocatorForElement(
            final String pageName,
            String elementName,
            WebDriver webdriver, TestReportLogger reportLog,
            BiFunction<String, String, WebLocator> webLocatorProvider, Object... dynamicLocatorValue) {
        WebLocator webLocator = null;
        String path = System.getProperty("user.dir", ".");
        path = path.substring(0, path.lastIndexOf(File.separator));
        String projectName = path.substring(path.lastIndexOf(File.separator) + 1);


        List<ElementProperty> locators = new ArrayList<>();
        try {
            locators = WebLocatorFileTypeFactory.getInstance().getLocator(pageName,
                    elementName);
        } catch (JAXBException exception) {
            log.warn("Not able to read OR file for page {}", pageName, exception);
        }
        if (!locators.isEmpty()) {
            for (final ElementProperty elementProperty : locators) {
                if (0 == dynamicLocatorValue.length) {
                    webLocator = webLocatorProvider.apply(elementProperty.getValue(), elementProperty.getType());
                } else {
                    webLocator = webLocatorProvider.apply(String.format(elementProperty.getValue(), dynamicLocatorValue), elementProperty.getType());
                }
                try {
                    webdriver.findElement(webLocator.getBy());

                    return webLocator;
                } catch (NoSuchElementException e) {
                    log.warn("Not able to find element: {} with locator : {}.", elementName, webLocator);
                }
            }
        } else {
            log.error("NameOfElement or SectionName is given wrong in object repository file or PageName in Page " +
                    "Object Class is wrong");
        }
        webdriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        throw new NoSuchElementException(
                String.format(
                        "no valid WebLocator found in object repository for page: %s and element: %s",
                        pageName, elementName));
    }

    public static WebLocator getWebLocator(final String locatorValue, final String locatorType) {
        switch (locatorType.toLowerCase()) {
            case "id":
                return new WebLocator(By.id(locatorValue));
            case "name":
                return new WebLocator(By.name(locatorValue));
            case "classname":
            case "class":
                return new WebLocator(By.className(locatorValue));
            case "tagname":
                return new WebLocator(By.tagName(locatorValue));
            case "linktext":
                return new WebLocator(By.linkText(locatorValue));
            case "partiallinktext":
                return new WebLocator(By.partialLinkText(locatorValue));
            case "cssselector":
                return new WebLocator(By.cssSelector(locatorValue));
            case "xpath":
                return new WebLocator(By.xpath(locatorValue));
            default:
                throw new UnexpectedClientException("Can't create locators for " + locatorValue);
        }
    }
}