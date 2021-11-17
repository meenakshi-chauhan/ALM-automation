package com.nagarro.driven.client.ui.api;

import java.io.File;

public interface Element<T> extends Interactable {

  boolean isSelected();

  String getAttribute(String attributeName);

  String getCss(String css);

  String getText();

  String getElementType();

  boolean isEnabled();

  boolean isDisplayed();

  File takeScreenshot();

  Object evaluateScript(String script,Object... args);

  Element<T> getChild(T locator);

  void hover();

  void scrollToElement();

}
