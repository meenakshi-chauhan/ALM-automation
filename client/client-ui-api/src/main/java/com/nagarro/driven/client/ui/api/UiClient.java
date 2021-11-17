package com.nagarro.driven.client.ui.api;

import com.nagarro.driven.client.ui.api.table.Table;

public interface UiClient<T> {

  void quit();

  Window window();

  Element<T> element(T locator);

  Element<T> element(String pageName, String elementName);

  Element<T> element(String pageName, String elementName, Object ... dynamicLocatorValue);

  Elements elements(T locator);

  Elements elements(String pageName, String elementName);

  Table<T> table(T tableLocator, T rowLocator);

  Table<T> table(String pageName, String tableElementName, String rowElementName);
}
