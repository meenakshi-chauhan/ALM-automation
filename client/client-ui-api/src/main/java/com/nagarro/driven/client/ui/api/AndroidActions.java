package com.nagarro.driven.client.ui.api;

public interface AndroidActions {

  void pressAndroidBackKey();

  void hideKeyboard();

  void closeApp();

  double batteryLevel();

  boolean isKeyboardShown();

  void openNotifications();

  void scrollByIdUptoDescription(String idOfElementToScroll, String description);

  void scrollByClassNameUptoDescription(String classOfElementToScroll, String description);

  void scrollByClassNameUptoText(String classOfElementToScroll, String text);

  void scrollByIdUptoText(String idOfElementToScroll, String text);

  void pressEnter();

  boolean isDeviceLocked();

  void unlockDevice();

  String getURL();
}
