package com.nagarro.driven.client.selenium;

import org.openqa.selenium.By;

/**
 * Finds the web locator element.
 *
 * @author nagarro
 */
public class WebLocator {

    /* The By instance. */
    private final By by;

    /*
     * Constructor to instantiate the by object.
     */
    public WebLocator(final By by) {
        this.by = by;
    }

    /**
     * Gets the by object.
     *
     * @return the by object
     */
    public By getBy() {
        return by;
    }

    @Override
    public String toString() {
        return by.toString();
    }
}
