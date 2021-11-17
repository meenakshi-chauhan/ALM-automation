package com.nagarro.driven.client.ui.api;

public interface Interactable {

  void click();

  void clear();

  void sendKeys(String keys);

  default void setText(String text) {
    clear();

    if (text != null) {
      sendKeys(text);
    }
  }

  void selectDropdown(String visibleText);
}
