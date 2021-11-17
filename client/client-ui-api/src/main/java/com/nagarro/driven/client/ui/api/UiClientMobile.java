package com.nagarro.driven.client.ui.api;

import java.util.Set;

public interface UiClientMobile<T> extends UiClientAppium {

  ElementMobile element(T locator);

  ElementMobile element(String pageName, String elementName);

  ElementMobile element(String pageName, String elementName, Object ... dynamicLocatorValue);

  Elements<T> elements(T locator);

  Elements<T> elements(String pageName, String elementName);

  void clickByCoordinates(int x, int y);

  void pageDown();

  void pageUp();

  void quit();

  void go(String url);

  void waitForAppLoad();

  String getDeviceTime();

  Set<String> getContextHandles();

  void switchContext(String context);

  void switchToNativeContext();

  Object executeScript(String script, Object... arguments);
}
