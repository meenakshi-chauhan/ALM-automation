package com.nagarro.driven.client.selenium;

import com.nagarro.driven.core.reporting.api.TestReportLogger;
import com.nagarro.driven.core.reporting.api.TestStatus;
import com.nagarro.driven.core.util.AutomationFrameworkException;
import com.nagarro.driven.core.util.Sleeper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Predicate;

import static java.nio.file.Files.delete;

public class SeleniumHelperMethods {

    private static final Logger log = LoggerFactory.getLogger(SeleniumHelperMethods.class);
    private final TestReportLogger reportLog;
    private final WebDriver webDriver;

    public SeleniumHelperMethods(WebDriver webDriver, TestReportLogger testReportLogger) {
        this.webDriver = webDriver;
        reportLog = testReportLogger;
    }

    /**
     * Checks whether the specific WebElement has the expectedClass
     */
    public static boolean hasClass(WebElement webElement, String expectedClass) {
        String[] classes = webElement.getAttribute("class").split(" ");
        return ArrayUtils.contains(classes, expectedClass);
    }

    /**
     * This method executes the javascript.
     */
    private Object evaluateScript(String script, WebElement element) {
        Object result = null;
        try {
            log.info("Executing script: {}", script);
            JavascriptExecutor executor = (JavascriptExecutor) webDriver;
            result = executor.executeScript(script, element);
            reportLog.reportLogger(TestStatus.PASS, "Script executed successfuly");
        } catch (Exception e) {
            log.error("Error in javaScript :{}", e.getMessage());
            reportLog.reportLogger(
                    TestStatus.FAIL, "Error in executin script:" + script + ": " + e.getMessage() + "");
            reportLog.reportErrorLogger(TestStatus.FAIL, e);
        }
        return result;
    }

    /**
     * This method executes the javascript.
     */
    private Object evaluateScript(String script) {
        Object result = null;
        try {
            log.info("Executing script: {}", script);
            JavascriptExecutor executor = (JavascriptExecutor) webDriver;
            result = executor.executeScript(script);
            reportLog.reportLogger(TestStatus.PASS, "Script executed successfuly");
        } catch (Exception e) {
            log.error("Error in javaScript :{}", e.getMessage());
            reportLog.reportLogger(
                    TestStatus.FAIL, "Error in executin script:" + script + ": " + e.getMessage() + "");
            reportLog.reportErrorLogger(TestStatus.FAIL, e);
        }
        return result;
    }

    /**
     * This method scroll the page up.
     */
    public void pageUp() {
        evaluateScript("scroll(0, -250);", null);
    }

    /**
     * This method scroll the page down.
     */
    public void pageDown() {
        evaluateScript("scroll(0, 250);", null);
    }

    /**
     * This method accepts the alert on the page.
     */
    public void acceptAlert() {
        log.info("Accepting alert");
        int count = 0;
        while (count++ < 5) {
            try {
                webDriver.switchTo().alert().accept();
                reportLog.reportLogger(TestStatus.PASS, "Alert accepted");
                log.info("Alert accepted");
                break;
            } catch (Exception ex) {
                Sleeper.silentSleep(4000);
            }
        }
    }

    /**
     * This method dismiss the alert on the page.
     */
    public void dismissAlert() {
        log.info("Cancelling the alert");
        int count = 0;
        while (count++ < 5) {
            try {
                webDriver.switchTo().alert().dismiss();
                reportLog.reportLogger(TestStatus.PASS, "Alert cancelled");
                log.info("Alert cancelled");
                break;
            } catch (Exception ex) {
                Sleeper.silentSleep(4000);
            }
        }
    }

    /**
     * This method switches the focus to the tab whose tab number provided.
     */
    public void switchToTab(int tabNumber) {
        String action = "Switching to tab number :";
        try {
            ArrayList<String> tabs = new ArrayList<>(webDriver.getWindowHandles());
            log.info("Switching to tab :{}", tabNumber);
            if (tabs.size() > tabNumber) {
                webDriver.switchTo().window(tabs.get(tabNumber));
                log.debug("Switching to tab : {}", tabNumber);
                reportLog.reportLogger(TestStatus.PASS, action + tabNumber + " is done.");

            } else {
                log.error("Invalid tab number provided: {}", tabNumber);
                reportLog.reportLogger(
                        TestStatus.FAIL, action + tabNumber + " is not possible due to invalid tab number");
            }
        } catch (Exception e) {
            log.error("Error in switching to tab {} : {}", tabNumber, e.getMessage());
            reportLog.reportLogger(
                    TestStatus.FAIL, action + tabNumber + " is not possible due to error:" + e.getMessage());
            reportLog.reportErrorLogger(TestStatus.FAIL, e);
        }
    }

    /**
     * This method switches focus to parent tab and closes all other tabs.
     */
    public void switchToParentTab() {
        try {
            ArrayList<String> tabs = new ArrayList<>(webDriver.getWindowHandles());
            log.info("Switching to parent tab and closing others");
            for (int tab = (tabs.size() - 1); tab > 0; tab--) {
                webDriver.switchTo().window(tabs.get(tab)).close();
            }
            webDriver.switchTo().window(tabs.get(0));
            log.info("Switching to parent tab is successful.");
            reportLog.reportLogger(TestStatus.PASS, "Switching to parent tab is successful.");

        } catch (Exception e) {
            log.error("Error in switching to parent tab : {}", e.getMessage());
            reportLog.reportLogger(
                    TestStatus.FAIL,
                    "Switching to parent tab is not possible due to error:" + e.getMessage());
            reportLog.reportErrorLogger(TestStatus.FAIL, e);
        }
    }

    /**
     * This method gets the text of element by attribute using JS.
     *
     * @param attrName  the name of attribute
     * @param attrValue the value of attribute
     * @return String text of element found
     */
    public String getTextByAttributeUsingJS(String attrName, String attrValue) {
        log.info("Getting text of Element by its attribute :{} having value:{}", attrName, attrValue);
        Object obj =
                evaluateScript(
                        "return "
                                + "document.querySelectorAll('["
                                + attrName
                                + "=\""
                                + attrValue
                                + "\"]')[0].value");
        if (null != obj) {
            return obj.toString();
        } else {
            return "";
        }
    }

    /**
     * Get the text by element Id using JS.
     *
     * @param controlId the name of control
     * @return String text of element found
     */
    public String getTextByElementIdUsingJS(String controlId) {
        log.info("Getting text of Element by its id :{}", controlId);
        Object obj = evaluateScript("return " + "document.getElementById(\"" + controlId + "\").value");
        if (null != obj) {
            return obj.toString();
        } else {
            return "";
        }
    }

    /**
     * Get the text for dropdown by attribute using JS.
     *
     * @param key   the name of attribute
     * @param value the value of attribute
     * @return String text of element found
     */
    public String getTextForDropDownByAttributeUsingJS(String key, String value) {
        log.info("Getting text for dropdown by its attribute :{} with value :{}", key, value);
        String js =
                "function getSelectedText(elementId) {"
                        + "    var elt = document.querySelectorAll('["
                        + key
                        + "=\""
                        + value
                        + "\"]')[0];"
                        + "    if (elt.selectedIndex == -1)"
                        + "        return null;"
                        + "    return elt.options[elt.selectedIndex].text;"
                        + "}"
                        + "return getSelectedText('"
                        + value
                        + "');";

        Object obj = evaluateScript(js);

        if (null != obj) {
            return obj.toString();
        } else {
            return "";
        }
    }

    /**
     * Count dom elements by selector.
     *
     * @param selector the selector
     * @return the count of dom elements
     */
    public int countDomElementsBySelector(String selector) {
        log.info("Count dom elements by selector: {}.", selector);

        String script = "return document.querySelectorAll('" + selector + "').length;";
        Object obj = evaluateScript(script);
        if (null != obj) {
            return Integer.parseInt(obj.toString());
        } else {
            return 0;
        }
    }

    /**
     * This function can be manipulated according to the element you want to use Verify content under
     * info icon using JS
     *
     * @param element      element.
     * @param textToVerify list of text to verify
     * @return true, if successful.
     */
    public boolean verifyListInfoContent(By element, List<String> textToVerify) {
        boolean result = false;
        log.info("Verifying List content");
        try {
            ArrayList<String> runtimeValues = new ArrayList<>();
            ArrayList<String> values = new ArrayList<>();
            WebElement childList = webDriver.findElement(element);
            List<WebElement> options = childList.findElements(By.tagName("li"));
            for (WebElement option : options) {
                WebElement childHeading = option.findElement(By.tagName("h5"));
                String optionText = (String) evaluateScript("return arguments[0].innerText;", childHeading);
                if (optionText != null) {
                    runtimeValues.add(optionText.trim());
                }
            }
            for (String item : textToVerify) {
                values.add(item);
            }
            if (runtimeValues.containsAll(values) || runtimeValues.equals(values)) {
                reportLog.reportLogger(TestStatus.PASS, "List verified successfuly");
                result = true;
            }
        } catch (NoSuchElementException e) {
            reportLog.reportLogger(TestStatus.FAIL, "Element not found" + e.getMessage());
            reportLog.reportErrorLogger(TestStatus.FAIL, e);
            throw (e);
        } catch (TimeoutException e) {
            reportLog.reportLogger(TestStatus.FAIL, "Timeout Exception :" + e.getMessage());
            reportLog.reportErrorLogger(TestStatus.FAIL, e);
            throw (e);
        } catch (Exception e) {
            reportLog.reportLogger(TestStatus.FAIL, " Exception :" + e.getMessage());
            reportLog.reportErrorLogger(TestStatus.FAIL, e);
            throw (e);
        }
        return result;
    }

    /**
     * Verify info icon heading using JS
     *
     * @param headingPath  heading Path.
     * @param textToVerify Text user want to verify.
     * @return true, if Successful.
     */
    public boolean verifyInfoHeading(By headingPath, String textToVerify) {
        boolean result = false;
        WebElement element = webDriver.findElement(headingPath);
        String theTextIWant = (String) evaluateScript("return arguments[0].innerHTML;", element);
        if (theTextIWant != null && theTextIWant.contains(textToVerify)) {
            result = true;
        }
        return result;
    }

    /**
     * This method waits for 60 seconds max for ajax call to complete.
     */
    public void waitForAjax() {
        int seconds = 10;
        try {
            log.info("Waiting for ajax requests to finish");
            if ((Long) evaluateScript("return jQuery.active") > 0) {
                new WebDriverWait(webDriver, seconds)
                        .until(
                                driver -> {
                                    boolean isAjaxCallComplete =
                                            (boolean) evaluateScript("return jQuery.active == 0");
                                    if (isAjaxCallComplete) {
                                        log.info("Ajax call completed.");
                                    }
                                    return isAjaxCallComplete;
                                });
            }
        } catch (TimeoutException e) {
            log.error("Timeout Exception Occured :{}", e.getMessage());
            reportLog.reportLogger(TestStatus.FAIL, "Timeout Exception :" + e.getMessage());
            reportLog.reportErrorLogger(TestStatus.FAIL, e);
        } catch (Exception e) {
            log.error("Exception occured :{}", e.getMessage());
            reportLog.reportLogger(TestStatus.FAIL, " Exception :" + e.getMessage());
            reportLog.reportErrorLogger(TestStatus.FAIL, e);
        }
    }

    /**
     * Returns the number of IMG elements under the given DOM element.
     *
     * @param element element.
     * @return int image count.
     */
    public int countImagesPresent(By element) {
        int result = 0;
        log.info("Counting images present on Dom Element");
        try {
            WebElement parentElement = webDriver.findElement(element);
            List<WebElement> images = parentElement.findElements(By.tagName("img"));
            result = images.size();
            log.info("Count of images present on Dom Element : {}", result);
            reportLog.reportLogger(TestStatus.PASS, "Count of images present on Dom Element :" + result);
        } catch (NoSuchElementException e) {
            log.error("Exception occurred", e);
            reportLog.reportLogger(TestStatus.FAIL, "Exception occurred : " + e.getMessage());
            reportLog.reportErrorLogger(TestStatus.FAIL, e);
            throw new AutomationFrameworkException("No Such Element Exception");
        }
        return result;
    }

    /**
     * Returns a value indicating whether a given attribute is present for a given element
     *
     * @param element   WebElement to search for a given attribute
     * @param attribute Attribute name to test for existence on element
     */
    public boolean isAttributePresent(WebElement element, String attribute) {
        boolean result = false;
        log.info("Checking presence of attribute of an element");
        try {
            String attrValue = element.getAttribute(attribute);
            if (attrValue != null) {
                result = true;
            }
            log.info("Attribute found : {} of element : {}", attribute, element);
            reportLog.reportLogger(TestStatus.PASS, "Attribute Found");

        } catch (Exception e) {

            log.error(
                    "Attribute not found : {} of element : {} due to Exception : {} ",
                    attribute,
                    element,
                    e.getMessage());
            reportLog.reportLogger(
                    TestStatus.FAIL, "Attribute not Found due to Exception : " + e.getMessage());
            reportLog.reportErrorLogger(TestStatus.FAIL, e);
        }
        return result;
    }

    /**
     * This method get the specified attribute of web element.
     *
     * @param element   element on page.
     * @param attribute name of the attribute whose value user wants to get.
     * @return String the attribute value.
     */
    public String getAttribute(By element, String attribute) {
        String attrValue = null;
        log.info("Retrieving attribute of an element");
        try {
            WebDriverWait wait = new WebDriverWait(webDriver, 30);
            wait.until(ExpectedConditions.presenceOfElementLocated(element));
            attrValue = webDriver.findElement(element).getAttribute(attribute);
            log.info("Attribute {} of element {} is {}", attribute, element, attrValue);
            reportLog.reportLogger(
                    TestStatus.PASS,
                    "Attribute " + attribute + " of element " + element + " is " + attrValue);

        } catch (Exception e) {
            log.error("Attribute not found : {} of element : {} due to Exception", attribute, element, e);
            reportLog.reportLogger(
                    TestStatus.FAIL, "Attribute not Found due to Exception : " + e.getMessage());
            reportLog.reportErrorLogger(TestStatus.FAIL, e);
            throw new AutomationFrameworkException(
                    "Attribute not found : " + attribute + " of element : " + element);
        }
        return attrValue;
    }

    /**
     * This method get the access on window opened.
     *
     * @param element element on web page.
     */
    public void switchTowindow(By element) {
        log.info("Switch to the window opened ");
        try {
            Set<String> windows = webDriver.getWindowHandles();
            for (String currentWindowHandle : windows) {
                webDriver.switchTo().window(currentWindowHandle);
            }
            log.info("Switched to current window. ");
            reportLog.reportLogger(TestStatus.PASS, "switched to window ");
        } catch (Exception e) {

            log.error("Error in switching to window", e);
            reportLog.reportLogger(TestStatus.FAIL, "Error in switching to window : " + e.getMessage());
            reportLog.reportErrorLogger(TestStatus.FAIL, e);
            throw new AutomationFrameworkException("Error in switching to window");
        }
    }

    /**
     * This method press the ENTER key of keyboard.
     *
     * @param textBox text box element.
     */
    public void keyPressEnter(By textBox) {
        log.info("pressing Enter Key");
        try {
            webDriver.findElement(textBox).sendKeys(Keys.ENTER);
            log.info("Enter key is pressed.");
        } catch (Exception e) {
            log.error("Error in pressing Enter Key", e);
            reportLog.reportLogger(TestStatus.FAIL, "Error in pressing Enter Key : " + e.getMessage());
            reportLog.reportErrorLogger(TestStatus.FAIL, e);
            throw new AutomationFrameworkException("Error in pressing Enter Key");
        }
    }

    /**
     * This method verify the presence of specified text on the page.
     *
     * @param text text value user want to verify on web page.
     * @return true, if text is present on the web page.
     */
    public boolean verifyAnyTextOnPage(String text) {
        boolean result = false;
        log.info(" verify the presence of specified text on the page.");

        try {
            Predicate<String> p = x -> x.contains(text);

            if (p.test(webDriver.getPageSource())) {
                log.info("Text: {} is present on the page ", text);
                reportLog.reportLogger(TestStatus.PASS, "Text: " + text + " is present on the page ");
                result = true;
            } else {
                log.info("Text: {} is not present on the page ", text);
                reportLog.reportLogger(TestStatus.PASS, "Text: " + text + " is not present on the page ");
            }
        } catch (Exception e) {
            log.error("Exception occurred in checking text : {} ", e.getMessage());
            reportLog.reportLogger(
                    TestStatus.FAIL, "Exception occurred in checking text : " + e.getMessage());
            reportLog.reportErrorLogger(TestStatus.FAIL, e);
        }
        return result;
    }

    /**
     * This method delete all the files present in the folder.
     *
     * @param folderPath path to the folder
     */
    public void deleteExistingFilesFromFolder(String folderPath) {
        log.info("Deleteing files from folder with folderpath {}", folderPath);
        try {
            File folder = new File(folderPath);
            File[] files = folder.listFiles();
            int noOfFiles = files.length;
            if (noOfFiles > 0) {
                for (File file : files) {
                    if (file.isFile()) {
                        delete(file.toPath());
                        log.info("Files deleted from the folder at path :{}", folderPath);
                        reportLog.reportLogger(
                                TestStatus.INFO, "Files deleted from the folder at path : " + folderPath);
                    } else {
                        log.info("There are no files to delete at path :{}", folderPath);
                        reportLog.reportLogger(
                                TestStatus.INFO, "There are no files to delete at path : " + folderPath);
                    }
                }
            } else {
                log.info("There are no files to delete at path :{}", folderPath);
                reportLog.reportLogger(
                        TestStatus.INFO, "There are no files to delete at path : " + folderPath);
            }
        } catch (Exception e) {
            log.error("Exception occurred in deleting files", e);
            reportLog.reportLogger(
                    TestStatus.FAIL, "Exception occurred in deleting files : " + e.getMessage());
            reportLog.reportErrorLogger(TestStatus.FAIL, e);
            throw new AutomationFrameworkException("Exception occurred in deleting files " + folderPath);
        }
    }

    /**
     * Press any key yoy need on the element
     *
     * @param element the text box where you want to press the key
     */
    public void keyPress(By element, Keys keyToPress) {
        log.info("Pressing Tab Key");
        try {
            webDriver.findElement(element).sendKeys(keyToPress);
            log.info("Tab key is pressed.");
        } catch (Exception e) {

            log.error("Error in pressing Tab Key", e);
            reportLog.reportLogger(TestStatus.FAIL, "Error in pressing Tab Key : " + e.getMessage());
            reportLog.reportErrorLogger(TestStatus.FAIL, e);
            throw new AutomationFrameworkException("Error in pressing Tab Key");
        }
    }

    /**
     * Verify if file is present or not.
     *
     * @param fileName filename.
     * @return true, if file is present.
     */
    public boolean isFilePresent(String fileName) {
        boolean result = false;
        log.info("Checking file :{} is present or not.", fileName);
        try {
            File file = new File(fileName);
            if (file.exists()) {
                log.info("File :{} is present", fileName);
                reportLog.reportLogger(TestStatus.PASS, "File " + fileName + "is Present");
                result = true;
            } else {
                log.info("File :{} is not present", fileName);
                reportLog.reportLogger(TestStatus.INFO, "File " + fileName + "is not Present");
            }
        } catch (Exception e) {
            log.error("Exception occurred in searching file", e);
            reportLog.reportLogger(
                    TestStatus.FAIL, "Exception occurred in searching file : " + e.getMessage());
            reportLog.reportErrorLogger(TestStatus.FAIL, e);
            throw new AutomationFrameworkException("Exception occurred in searching file " + fileName);
        }
        return result;
    }

    /**
     * Get File name.
     *
     * @param folderPath path to folder.
     * @return String, path to folder
     */
    public String getFileName(String folderPath) {
        String fileName = null;
        log.info("Retrieving the file name from folder path : {} ", folderPath);
        try {
            File folder = new File(folderPath);
            File[] files = folder.listFiles();
            int noOfFiles = files.length;
            fileName = null;
            if (noOfFiles > 0) {
                for (File file : files) {
                    if (file.isFile()) {
                        fileName = file.getAbsolutePath();
                        log.info("File : {} is found", fileName);
                        reportLog.reportLogger(TestStatus.PASS, "File : " + fileName + " is found");
                    } else {
                        log.info("File not found in folder path : {}", folderPath);
                        reportLog.reportLogger(
                                TestStatus.INFO, "File not found in folder path : {}" + folderPath);
                    }
                }
            }
        } catch (Exception e) {
            log.info("Exception occurred in retrieving filename from : {}", folderPath, e);
            reportLog.reportLogger(
                    TestStatus.FAIL,
                    "Exception occurred in retrieving filename from : "
                            + folderPath
                            + " : "
                            + e.getMessage());
            reportLog.reportErrorLogger(TestStatus.FAIL, e);
            throw new AutomationFrameworkException(
                    "Exception occurred in retrieving filename from " + folderPath);
        }
        return fileName;
    }

    /**
     * Gets the URL for PDF window.
     *
     * @return String the URL for PDF window
     */
    public String getURLForPDFWindow() {
        String pdfWindowURL = null;
        log.info("Getting the URL for PDF window.");
        try {
            ArrayList<String> tabs = new ArrayList<>(webDriver.getWindowHandles());
            webDriver.switchTo().window(tabs.get(1));
            pdfWindowURL = webDriver.getCurrentUrl();
            log.info("Swtiched to child window ");
            reportLog.reportLogger(TestStatus.PASS, "Swtiched to child window ");
        } catch (Exception e) {
            log.info("Could not find the url due to exception ", e);
            reportLog.reportLogger(
                    TestStatus.FAIL, "Could not find the url due to exception : {}" + e.getMessage());
            reportLog.reportErrorLogger(TestStatus.FAIL, e);
            throw new AutomationFrameworkException("Could not find the pdf url");
        }
        return pdfWindowURL;
    }

    /**
     * This will read the PDF and parse it.
     *
     * @return whether the required text is matched with the PDF content
     */
    // dependency added in Apache PDFBox » 1.8.4
    public String getPDFContent(int startPage, int endPage) {
        log.info("Verifying the Pdf Content");

        PDFTextStripper pdfStripper = null;
        PDDocument pdDoc = null;
        COSDocument cosDoc = null;
        String parsedText = null;

        try {
            URL url = new URL(webDriver.getCurrentUrl());
            BufferedInputStream file = new BufferedInputStream(url.openStream());
            PDFParser parser = new PDFParser(file);

            parser.parse();
            cosDoc = parser.getDocument();
            pdfStripper = new PDFTextStripper();
            pdfStripper.setStartPage(startPage);
            pdfStripper.setEndPage(endPage);

            pdDoc = new PDDocument(cosDoc);
            parsedText = pdfStripper.getText(pdDoc);
        } catch (MalformedURLException e2) {
            log.error("URL string could not be parsed : {}", e2.getMessage());
            reportLog.reportLogger(TestStatus.FAIL, "URL string could not be parsed " + e2.getMessage());
            reportLog.reportErrorLogger(TestStatus.FAIL, e2);
        } catch (IOException e) {
            log.error("Unable to open PDF Parser : {} ", e.getMessage());
            reportLog.reportLogger(
                    TestStatus.FAIL, "Unable to open PDF Parser due to Exception " + e.getMessage());
            reportLog.reportErrorLogger(TestStatus.FAIL, e);
            try {
                if (cosDoc != null) {
                    cosDoc.close();
                }
                if (pdDoc != null) {
                    pdDoc.close();
                }
            } catch (Exception e1) {
                e.printStackTrace();
            }
        }

        log.info(" ++++++++++++++PDF Content starts+++++++++++++++++");
        log.info(parsedText);
        log.info("+++++++++++++++++ PDF content ends++++++++++++++++");

        return parsedText;
    }

    /**
     * This method is used to validate the anchor tags on the page
     */
    public boolean validateInvalidLinks() {
        log.info("validating the anchor tags on the page");
        boolean valid = true;
        try {
            List<WebElement> anchorTagsList = webDriver.findElements(By.tagName("a"));
            log.info("Total no. of links are {}", anchorTagsList.size());
            for (WebElement anchorTagElement : anchorTagsList) {
                if (anchorTagElement != null) {
                    String url = anchorTagElement.getAttribute("href");
                    if (url != null && !url.contains("javascript") && !verifyURLIsValid(url)) {
                        valid = false;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Anchor tags validation failed due to Exception : {}", e.getMessage());
            reportLog.reportLogger(
                    TestStatus.FAIL, "Anchor tags validation failed due to Exception : {}" + e.getMessage());
            reportLog.reportErrorLogger(TestStatus.FAIL, e);
            valid = false;
        }
        return valid;
    }

    /**
     * This method is used to verify if url is valid by checking if it throws error code 200
     *
     * @param url href value derived from the anchor tag
     */
    public boolean verifyURLIsValid(String url) {
        log.info("Validating Url : {}", url);
        boolean valid = false;
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        try {
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() / 100 == 2) {
                reportLog.reportLogger(TestStatus.PASS, "url " + url + " is working fine");
                log.info("url : {} is working fine", url);
                valid = true;
            } else {
                reportLog.reportLogger(TestStatus.PASS, "url " + url + " is working fine");
                log.info("url : {} is working fine", url);
            }
        } catch (Exception e) {
            log.error("Validation could not be done due to exception : {} ", e.getMessage());
            reportLog.reportLogger(
                    TestStatus.FAIL, "Validation could not be done due to exception : " + e.getMessage());
            reportLog.reportErrorLogger(TestStatus.FAIL, e);
        }
        return valid;
    }

    /**
     * This method is used to validate the img tags on the page
     */
    public boolean validateInvalidImages() {
        log.info("Validating all the image links on the page");
        try {
            List<WebElement> imagesList = webDriver.findElements(By.tagName("img"));
            log.info("Total no. of images are {}", imagesList.size());
            for (WebElement imgElement : imagesList) {
                if (imgElement != null && !verifyimageActive(imgElement)) {
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("Exception occured in validating : {}", e.getMessage());
            reportLog.reportLogger(
                    TestStatus.FAIL, "Exception occured in validating : " + e.getMessage());
            reportLog.reportErrorLogger(TestStatus.FAIL, e);
            return false;
        }
        return true;
    }

    /**
     * This method is used to verify if image source is valid by checking if it throws error code 200
     *
     * @param imgElement element derived from the img tag
     */
    public boolean verifyimageActive(WebElement imgElement) {
        boolean valid = false;
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(imgElement.getAttribute("src"));
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() / 100 == 2) {
                reportLog.reportLogger(TestStatus.PASS, "Image can be accessed : " + imgElement);
                log.info("Image cannot be accessed : {}", imgElement);
                valid = true;
            } else {
                reportLog.reportLogger(TestStatus.FAIL, "Image cannot be accessed : " + imgElement);
                log.info("Image can be accessed : {}", imgElement);
            }
        } catch (Exception e) {
            log.error("Exception occurred in accessing image  : {}", e.getMessage());
            reportLog.reportLogger(
                    TestStatus.FAIL, "Exception occurred in accessing image  : " + e.getMessage());
            reportLog.reportErrorLogger(TestStatus.FAIL, e);
        }
        return valid;
    }

    /**
     * Switch to particular frame Id.
     *
     * @param frameID the frame ID to be switched to
     */
    public void switchToFrame(String frameID) {
        log.info("Switching to particular frame Id : {}", frameID);
        try {
            webDriver.switchTo().frame(frameID);
            log.info("Switched to frame with frame id :{}", frameID);
            reportLog.reportLogger(TestStatus.PASS, "Switched to frame with frame id :" + frameID);
        } catch (Exception e) {
            log.error("Exception occurred in switching to frame : {}", e.getMessage());
            reportLog.reportLogger(
                    TestStatus.FAIL, "Exception occurred in switching to frame : " + e.getMessage());
            reportLog.reportErrorLogger(TestStatus.FAIL, e);
        }
    }

    /**
     * This method scrolls the page upto the particular webElement given.
     */
    public void scrollToElement(WebElement webElement) {
        log.debug("Scrolling to element: {}", webElement);
        evaluateScript("arguments[0].scrollIntoView();", webElement);
        reportLog.reportLogger(
                TestStatus.PASS, "Scrolling to element: " + "\"" + webElement + "\"" + " is done " + "\"");
    }

    /**
     * This method clicks on the webElement using javascript.
     */
    public void clickUsingJs(WebElement webElement) {
        log.debug("Click on element: {} using js", webElement);
        evaluateScript("arguments[0].click();", webElement);
        reportLog.reportLogger(
                TestStatus.PASS,
                "Click on element: " + "\"" + webElement + "\"" + " using js is successful " + "\"");
    }
}
