package com.nagarro.driven.client.ui.api;

import java.io.File;

public interface ElementWindows extends Interactable {

  boolean isSelected();

  void doubleClick();

  String getAttribute(String attributeName);

  String getText();

  String getElementType();

  boolean isEnabled();

  boolean isDisplayed();

  File takeScreenshot();

  void hover();

  void scrollToElement();

  Location getLocation();

  void swipeElement(int x, int y);

  String getCssValue(String propertyName);

  String elementTagName();
}
