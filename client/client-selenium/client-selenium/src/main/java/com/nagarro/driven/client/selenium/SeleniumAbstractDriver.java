package com.nagarro.driven.client.selenium;

import com.nagarro.driven.client.ui.api.Dimension;
import com.nagarro.driven.client.ui.api.*;
import com.nagarro.driven.client.ui.api.table.Column;
import com.nagarro.driven.client.ui.api.table.Row;
import com.nagarro.driven.client.ui.api.table.Table;
import com.nagarro.driven.core.reporting.api.TestReportLogger;
import com.nagarro.driven.core.reporting.api.TestStatus;
import com.nagarro.driven.core.util.Sleeper;
import com.nagarro.driven.core.util.Waiter;
import com.nagarro.driven.core.webdriver.AbstractWebDriver;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * SeleniumAbstractDriver is an abstract class which contains all the events/functions related to
 * selenium.
 *
 * @author nagarro
 */
public abstract class SeleniumAbstractDriver extends AbstractWebDriver
        implements BrowserClient<WebLocator> {
    private static final Logger log = LoggerFactory.getLogger(SeleniumAbstractDriver.class);
    private final WebDriver webDriver;
    private final SeleniumWindow window;

    /**
     * Assigns the webdriver and creates the object for TestReportLogger.
     */
    protected SeleniumAbstractDriver(WebDriver webDriver, TestReportLogger reportLog) {
        super(reportLog);
        log.debug("Creating WebDriverClient");
        this.window = new SeleniumWindow();
        this.webDriver = webDriver;
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    /**
     * @return instance of SeleniumHelperMethods class
     */
    public SeleniumHelperMethods helpers() {
        return new SeleniumHelperMethods(webDriver, reportLog);
    }

    /**
     * Abstract method for wait for page to load, implemented in ng client.
     */
    public abstract void waitForPageToLoad();

    @Override
    public void quit() {
        log.debug("Closing WebDriver");
        webDriver.quit();
        reportLog.reportLogger(TestStatus.PASS, "Closing web driver");
    }

    @Override
    public Window window() {
        return window;
    }

    @Override
    public Element<WebLocator> element(WebLocator locator) {
        return new SeleniumElement(locator);
    }

    @Override
    public Element<WebLocator> element(String pageName, String elementName) {
        return new SeleniumElement(pageName, elementName);
    }

    @Override
    public Element<WebLocator> element(
            String pageName, String elementName, Object... dynamicLocatorValue) {
        return new SeleniumElement(pageName, elementName, dynamicLocatorValue);
    }

    @Override
    public Elements elements(WebLocator locator) {
        return new SeleniumElements(locator);
    }

    @Override
    public Elements elements(String pageName, String elementName) {
        return new SeleniumElements(pageName, elementName);
    }

    @Override
    public Table<WebLocator> table(WebLocator tableLocator, WebLocator rowLocator) {
        return new SeleniumTable(tableLocator, rowLocator);
    }

    @Override
    public Table<WebLocator> table(String pageName, String tableElementName, String rowElementName) {
        return new SeleniumTable(pageName, tableElementName, rowElementName);
    }

    @Override
    public void go(String url) {
        log.info("Navigating to URL: {}", url);
        webDriver.get(url);
        reportLog.reportLogger(TestStatus.PASS, "Navigating to URL: " + "\"" + url + "\"");
        Sleeper.silentSleep(6000);
        this.waitForPageToLoad();
    }

    @Override
    public String getCurrentUrl() {
        String currentUrl = webDriver.getCurrentUrl();
        log.info("Retrieving current URL: {}", currentUrl);
        reportLog.reportLogger(TestStatus.PASS, "Retrieving current URL: " + "\"" + currentUrl + "\"");
        return currentUrl;
    }

    @Override
    public void refresh() {
        log.info("Reloading page");
        reportLog.reportLogger(TestStatus.PASS, "Refreshing the page");
        webDriver.navigate().refresh();
        this.waitForPageToLoad();
    }

    /**
     * This method executes the javascript.
     */
    @Override
    public Object evaluateScript(String script, Object... args) {
        Object result = null;
        try {
            log.info("Executing script: {}", script);
            JavascriptExecutor executor = (JavascriptExecutor) webDriver;
            result = executor.executeScript(script, args);
            reportLog.reportLogger(TestStatus.PASS, "Script executed successfuly");
        } catch (Exception e) {
            log.error("Error in javaScript :{}", e.getMessage());
            reportLog.reportLogger(
                    TestStatus.FAIL, "Error in executin script:" + script + ": " + e.getMessage() + "");
            reportLog.reportErrorLogger(TestStatus.FAIL, e);
        }
        return result;
    }

    @Override
    public History history() {
        return new SeleniumHistory();
    }

    @Override
    public Prompt prompt() {
        return new SeleniumPrompt();
    }

    @Override
    public String driverName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public void cleanUp() {
        quit();
    }

    public class SeleniumTable extends SeleniumElement implements Table<WebLocator> {

        private final WebLocator rowLocator;

        private SeleniumTable(WebLocator tableLocator, WebLocator rowLocator) {
            super(tableLocator);
            this.rowLocator = rowLocator;
            Waiter.waitFor(webElement::isDisplayed);
        }

        private SeleniumTable(String pageName, String tableElementName, String rowElementName) {
            this(
                    WebLocatorLoader.findWebLocatorForElement(pageName, tableElementName, webDriver, reportLog),
                    WebLocatorLoader.findWebLocatorForElement(pageName, rowElementName, webDriver, reportLog));
        }

        @Override
        public Row<WebLocator> getRow(Column<WebLocator> column, String value) {
            Optional<Row<WebLocator>> optionalRow = getOptionalRow(column, value);

            if (optionalRow.isPresent()) {
                reportLog.reportLogger(
                        TestStatus.PASS,
                        "Retrieving the row with text "
                                + "\""
                                + value
                                + "\" in column with index "
                                + "\""
                                + column.getIndex()
                                + "\"");
                return optionalRow.get();
            } else {
                reportLog.reportLogger(
                        TestStatus.ERROR,
                        "Couldn't find the row with text "
                                + "\""
                                + value
                                + "\" in column with index "
                                + "\""
                                + column.getIndex()
                                + "\"");
                throw new NoSuchElementException(
                        "Couldn't find row with text " + value + " in column with index " + column.getIndex());
            }
        }

        @Override
        public Row<WebLocator> getRow(int index) {
            reportLog.reportLogger(
                    TestStatus.PASS, "Retrieving the row with the index " + "\"" + index + "\"");
            return getRows().get(index);
        }

        @Override
        public Row<WebLocator> getRow(WebLocator rowLocator) {
            reportLog.reportLogger(
                    TestStatus.PASS, "Retrieving the row for the weblocator " + "\"" + rowLocator + "\"");
            return new SeleniumRow(webElement.findElement(rowLocator.getBy()));
        }

        @Override
        public Row<WebLocator> getHeader(WebLocator headerLocator) {
            reportLog.reportLogger(TestStatus.PASS, "Retrieving the row for the header");
            return getRow(headerLocator);
        }

        @Override
        public List<Row<WebLocator>> getRows() {
            reportLog.reportLogger(TestStatus.PASS, "Getting the list of all the rows");
            return webElement.findElements(rowLocator.getBy()).stream()
                    .map(SeleniumRow::new)
                    .collect(toList());
        }

        @Override
        public boolean rowExists(Column<WebLocator> column, String value) {
            boolean isPresent = getOptionalRow(column, value).isPresent();
            reportLog.reportLogger(
                    TestStatus.PASS,
                    "Row exists status for value " + "\"" + value + "\" is " + "\"" + isPresent + "\"");
            return isPresent;
        }

        private Optional<Row<WebLocator>> getOptionalRow(Column<WebLocator> column, String value) {
            return getRows().stream().filter(r -> r.getCell(column).getText().equals(value)).findFirst();
        }
    }

    private class SeleniumRow extends SeleniumElement implements Row<WebLocator> {

        private SeleniumRow(WebElement webElement) {
            super(webElement);
        }

        @Override
        public Element<WebLocator> getCell(Column<WebLocator> column) {
            reportLog.reportLogger(
                    TestStatus.PASS, "Getting the cell of column: " + "\"" + column + "\"");
            return new SeleniumElement(
                    webElement.findElements(column.getColumnsLocator().getBy()).get(column.getIndex()));
        }
    }

    public class SeleniumElement implements Element<WebLocator> {

        WebElement webElement;
        String elementName;

        public SeleniumElement(WebLocator locator) {
            this(webDriver.findElement(locator.getBy()));
            waitForPageToLoad();
            Waiter.waitFor(webElement::isDisplayed);
        }

        private SeleniumElement(String pageName, String elementName, Object... dynamicLocatorValue) {
            this(
                    WebLocatorLoader.findWebLocatorForElement(
                            pageName, elementName, webDriver, reportLog, dynamicLocatorValue));
            this.elementName = elementName;
        }

        private SeleniumElement(WebElement webElement) {
            this.webElement = webElement;
        }

        public SeleniumElement(WebLocator locator, String elementName) {
            this(locator);
            this.elementName = elementName;
        }

        public WebElement getWebElement() {
            return webElement;
        }

        /**
         * This method executes the javascript on a element
         */
        @Override
        public Object evaluateScript(String script, Object... args) {
            Object result = null;
            try {
                log.info("Executing script: {}", script);
                JavascriptExecutor executor = (JavascriptExecutor) webDriver;
                result = executor.executeScript(script, webElement, args);
                reportLog.reportLogger(TestStatus.PASS, "Script executed successfuly");
            } catch (Exception e) {
                log.error("Error in javaScript :{}", e.getMessage());
                reportLog.reportLogger(
                        TestStatus.FAIL, "Error in executin script:" + script + ": " + e.getMessage() + "");
                reportLog.reportErrorLogger(TestStatus.FAIL, e);
            }
            return result;
        }

        @Override
        public boolean isSelected() {
            log.debug("Retrieving select status for element: {} : {}", elementName, webElement);
            boolean selected = webElement.isSelected();
            log.debug("Selected: {}", selected);
            reportLog.reportLogger(
                    TestStatus.PASS,
                    "Select status of element: "
                            + "\""
                            + elementName
                            + "\""
                            + " is "
                            + "\""
                            + selected
                            + "\"");
            return selected;
        }

        @Override
        public String getAttribute(String attributeName) {
            log.debug(
                    "Retrieving attribute {} from element:{} : {}", attributeName, elementName, webElement);
            String attribute = webElement.getAttribute(attributeName);
            log.debug("{}: {}", attributeName, attribute);
            reportLog.reportLogger(
                    TestStatus.PASS,
                    "Attribute of element: " + "\"" + elementName + "\"" + " is " + "\"" + attribute + "\"");
            return attribute;
        }

        @Override
        public String getCss(String css) {
            log.debug("Retrieving css {} from element:{} : {}", css, elementName, webElement);
            String cssValue = webElement.getCssValue(css);
            log.debug("{}: {}", css, cssValue);
            reportLog.reportLogger(TestStatus.INFO,
                    String.format("Css of element %s is %s", elementName, cssValue));
            return cssValue;
        }

        @Override
        public String getText() {
            log.debug("Retrieving text from element: {} : {}", elementName, webElement);
            String text = webElement.getText();
            log.debug("Text: {}", text);
            reportLog.reportLogger(
                    TestStatus.PASS,
                    "Text of element: " + "\"" + elementName + "\"" + " is " + "\"" + text + "\"");
            return text;
        }

        @Override
        public String getElementType() {
            log.debug("Retrieving tag name from element: {} : {}", elementName, webElement);
            String tagName = webElement.getTagName();
            log.debug("Tag name: {}", tagName);
            reportLog.reportLogger(
                    TestStatus.PASS,
                    "Tag name of element: " + "\"" + elementName + "\"" + " is " + "\"" + tagName + "\"");
            return tagName;
        }

        @Override
        public boolean isEnabled() {
            log.debug("Retrieving enabled status from element: {} : {}", elementName, webElement);
            boolean enabled = webElement.isEnabled();
            log.debug("Enabled: {}", enabled);
            reportLog.reportLogger(
                    TestStatus.PASS,
                    "Enabled status of element: "
                            + "\""
                            + elementName
                            + "\""
                            + " is "
                            + "\""
                            + enabled
                            + "\"");
            return enabled;
        }

        @Override
        public boolean isDisplayed() {
            log.debug("Retrieving displayed status from element: {} : {}", elementName, webElement);
            try {
                boolean displayed = webElement.isDisplayed();
                log.debug("Displayed: {}", displayed);
                reportLog.reportLogger(
                        TestStatus.PASS,
                        "Display status of element: "
                                + "\""
                                + elementName
                                + "\""
                                + " is "
                                + "\""
                                + displayed
                                + "\"");
                return displayed;
            } catch (NoSuchElementException e) {
                log.error("Displayed: Couldn't find element");
                reportLog.reportLogger(TestStatus.ERROR, "Displayed: Couldn't find element");
                reportLog.reportErrorLogger(TestStatus.ERROR, e);
                return false;
            }
        }

        @Override
        public void click() {
            log.debug("Clicking on element: {} : {} ", elementName, webElement);
            webElement.click();
            waitForPageToLoad();
            reportLog.reportLogger(TestStatus.PASS, "clicking the element: " + "\"" + elementName + "\"");
        }

        @Override
        public void clear() {
            log.debug("Clearing element: {} : {} ", elementName, webElement);
            webElement.clear();
            reportLog.reportLogger(TestStatus.PASS, "Clearing element " + "\"" + elementName + "\"");
        }

        @Override
        public void sendKeys(String keys) {
            log.debug("Sending keys {} to element: {} : {}", keys, elementName, webElement);
            webElement.sendKeys(keys);
            reportLog.reportLogger(
                    TestStatus.PASS, "Sending Keys " + keys + " to element: " + "\"" + elementName + "\"");
        }

        @Override
        public File takeScreenshot() {
            log.debug("Taking screenshot");
            reportLog.reportLogger(TestStatus.PASS, "Taking screenshot ");
            return webElement.getScreenshotAs(OutputType.FILE);
        }

        @Override
        public void selectDropdown(String visibleText) {
            Select select = new Select(webElement);
            waitForPageToLoad();
            select.selectByVisibleText(visibleText);
            reportLog.reportLogger(
                    TestStatus.PASS,
                    "Selecting dropdown for element : " + elementName + "\"" + visibleText + "\"");
        }

        @Override
        public void hover() {
            Actions action = new Actions(webDriver);
            action.moveToElement(webElement).perform();
        }

        /**
         * This method scrolls the page upto the particular webElement given.
         */
        public void scrollToElement() {
            log.debug("Scrolling to element:{} : {}", elementName, webElement);
            evaluateScript("arguments[0].scrollIntoView();");
            reportLog.reportLogger(
                    TestStatus.PASS,
                    "Scrolling to element: " + "\"" + elementName + "\"" + " is done " + "\"");
        }

        @SuppressWarnings("rawtypes")
        @Override
        public Element getChild(WebLocator locator) {
            Element<WebLocator> child = new SeleniumElement(webElement.findElement(locator.getBy()));
            reportLog.reportLogger(
                    TestStatus.PASS,
                    "Getting child for element : "
                            + elementName
                            + "\""
                            + child.getText()
                            + "\""
                            + "for webLocator ");
            return child;
        }

        public SeleniumElements getChildren(WebLocator locator) {
            List<Element<WebLocator>> elements =
                    webElement.findElements(locator.getBy()).stream()
                            .map(SeleniumElement::new)
                            .collect(toList());

            reportLog.reportLogger(TestStatus.PASS, "Getting children for element : " + elementName);
            return new SeleniumElements(elements);
        }
    }

    public class SeleniumElements implements Elements {

        private final List<Element<WebLocator>> elements;
        String elementName;

        public SeleniumElements(WebLocator webLocator) {
            elements =
                    webDriver.findElements(webLocator.getBy()).stream()
                            .map(SeleniumElement::new)
                            .collect(toList());
            Waiter.waitFor(elements::isEmpty);
        }

        public SeleniumElements(List<Element<WebLocator>> elements) {
            this.elements = elements;
        }

        public SeleniumElements(WebLocator webLocator, String elementName) {
            this(webLocator);
            this.elementName = elementName;
        }

        private SeleniumElements(String pageName, String elementName) {
            this(WebLocatorLoader.findWebLocatorForElement(pageName, elementName, webDriver, reportLog));
            this.elementName = elementName;
        }

        @Override
        public void click() {
            elements.forEach(Element::click);
            reportLog.reportLogger(TestStatus.INFO, "Clicking the elements ");
        }

        @Override
        public void clear() {
            elements.forEach(Element::clear);
            reportLog.reportLogger(TestStatus.INFO, "Clearing the elements ");
        }

        @Override
        public void sendKeys(String keys) {
            reportLog.reportLogger(TestStatus.INFO, "Sending key " + "\"" + keys + "\"");
            elements.forEach(e -> e.sendKeys(keys));
        }

        @Override
        public void selectDropdown(String visibleText) {
            reportLog.reportLogger(
                    TestStatus.INFO, "Selecting dropdown for " + "\"" + visibleText + "\"");
            elements.forEach(e -> e.selectDropdown(visibleText));
        }

        @Override
        public void selectFromOptions(String visibleText) {
            reportLog.reportLogger(
                    TestStatus.PASS, "Selecting an Option from dropdown " + "\"" + visibleText + "\"");
            elements.stream()
                    .filter(e -> e.getText() != null && e.getText().equals(visibleText))
                    .findFirst()
                    .ifPresent(Element::click);
        }

        @Override
        public int size() {
            reportLog.reportLogger(TestStatus.INFO, "Element size is " + "\"" + elements.size() + "\"");
            return elements.size();
        }

        @Override
        public List<Element<WebLocator>> getElementList() {
            return elements;
        }

        @Override
        public List<String> getText() {
            return elements.stream()
                    .filter(Objects::nonNull)
                    .map(Element::getText)
                    .filter(StringUtils::isNotEmpty)
                    .collect(toList());
        }
    }

    private class SeleniumWindow implements Window {

        private SeleniumWindow() {
        }

        @Override
        public void close() {
            log.debug("Closing browser window");
            webDriver.close();
            reportLog.reportLogger(TestStatus.PASS, "Closing browser window");
        }

        @Override
        public String getTitle() {
            log.debug("Retrieving window title");
            String title = webDriver.getTitle();
            log.debug("Title: {}", title);
            reportLog.reportLogger(TestStatus.PASS, "Retrieving window title " + "\"" + title + "\"");
            return title;
        }

        @Override
        public void maximize() {
            log.debug("Maximizing browser window");
            webDriver.manage().window().maximize();
            reportLog.reportLogger(TestStatus.PASS, "Maximizing browser window");
        }

        @Override
        public void fullscreen() {
            log.debug("Setting browser window to fullscreen");
            webDriver.manage().window().fullscreen();
            reportLog.reportLogger(TestStatus.PASS, "Setting browser window to fullscreen");
        }

        @Override
        public Dimension getSize() {
            log.debug("Retrieving window size");
            org.openqa.selenium.Dimension size = webDriver.manage().window().getSize();
            log.debug("Window size - width: {}, height: {}", size.width, size.height);
            reportLog.reportLogger(
                    TestStatus.PASS,
                    "Getting window size - width "
                            + "\""
                            + size.width
                            + "\""
                            + " height "
                            + "\""
                            + size.height
                            + "\"");
            return new Dimension(size.width, size.height);
        }

        @Override
        public void setSize(Dimension size) {
            log.debug("Setting window size to {}", size);
            webDriver
                    .manage()
                    .window()
                    .setSize(new org.openqa.selenium.Dimension(size.getWidth(), size.getHeight()));
            reportLog.reportLogger(
                    TestStatus.PASS,
                    "Setting window size - width "
                            + "\""
                            + size.getWidth()
                            + "\""
                            + " height "
                            + "\""
                            + size.getHeight()
                            + "\"");
        }

        @Override
        public Optional<File> takeScreenshot() {
            reportLog.reportLogger(TestStatus.PASS, "Taking screenshot ");
            if (webDriver instanceof TakesScreenshot) {
                return Optional.of(((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE));
            } else {
                return Optional.empty();
            }
        }
    }

    private class SeleniumHistory implements History {

        private SeleniumHistory() {
        }

        @Override
        public void back() {
            log.debug("Navigating back");
            webDriver.navigate().back();
            reportLog.reportLogger(TestStatus.PASS, "Navigating back ");
        }

        @Override
        public void forward() {
            log.debug("Navigating forward");
            webDriver.navigate().forward();
            reportLog.reportLogger(TestStatus.PASS, "Navigating forward ");
        }
    }

    public class SeleniumPrompt implements Prompt {

        @Override
        public void dismissAlert() {
            log.debug("Dismissing alert");
            webDriver.switchTo().alert().dismiss();
            reportLog.reportLogger(TestStatus.PASS, "Dismissing alert text");
        }

        @Override
        public void acceptAlert() {
            log.debug("Accepting alert");
            webDriver.switchTo().alert().accept();
            reportLog.reportLogger(TestStatus.PASS, "Accepting alert text");
        }

        @Override
        public String getAlertText() {
            log.debug("Retrieving alert text");
            String text = webDriver.switchTo().alert().getText();
            log.debug("Text: {}", text);
            reportLog.reportLogger(TestStatus.PASS, "Getting alert text: " + "\"" + text + "\"");
            return text;
        }

        @Override
        public void sendAlertText(String keys) {
            log.debug("Send keys to alert: {}", keys);
            webDriver.switchTo().alert().sendKeys(keys);
            reportLog.reportLogger(TestStatus.PASS, "Send Alert text: " + "\"" + keys + "\"");
        }

        @Override
        public boolean isDisplayed() {
            log.debug("Checking if alert is displayed...");
            try {
                webDriver.switchTo().alert();
                log.debug("...alert is displayed");
                reportLog.reportLogger(TestStatus.PASS, "Alert is displayed");
                return true;
            } catch (NoAlertPresentException e) {
                log.error("Alert isn't displayed");
                reportLog.reportLogger(TestStatus.ERROR, "Alert is not displayed");
                reportLog.reportErrorLogger(TestStatus.ERROR, e);
                return false;
            }
        }
    }
}
