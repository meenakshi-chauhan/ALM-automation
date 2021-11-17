package com.nagarro.driven.client.ui.api;

import java.io.File;

public interface ElementAppium extends Interactable {

  boolean isSelected();

  String getAttribute(String attributeName);

  String getText();

  String getElementType();

  boolean isEnabled();

  boolean isDisplayed();

  File takeScreenshot();

  void hover();

  void scrollToElement();

  Location getLocation();
}
