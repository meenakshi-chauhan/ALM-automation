package com.nagarro.driven.client.ui.api;

public interface UiClientWindows<T> extends UiClientAppium {

  ElementWindows element(T locator);

  ElementWindows element(String pageName, String elementName);

  ElementWindows element(String pageName, String elementName, Object ... dynamicLocatorValue);

  Elements elements(T locator);

  Elements elements(String pageName, String elementName);

  void clickByCoordinate(int x, int y);

  void pageDown();

  void pageUp();

  void quit();

  void close();

  void launchApp();

  History history();
}
