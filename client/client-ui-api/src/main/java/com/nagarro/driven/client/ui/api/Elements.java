package com.nagarro.driven.client.ui.api;

import java.util.List;

public interface Elements<T> extends Interactable {

  int size();

  void selectFromOptions(String visibleText);

  List<Element<T>> getElementList();

  List<String> getText();
}
