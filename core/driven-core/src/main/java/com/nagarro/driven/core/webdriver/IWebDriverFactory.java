package com.nagarro.driven.core.webdriver;

import com.nagarro.driven.core.driver.api.IDriverFactory;

/**
 * Interface for creation/instantiation of a WebDriver and setting priority
 *
 * @author nagarro
 */
public interface IWebDriverFactory<T extends AbstractWebDriver> extends IDriverFactory<T> {
}
