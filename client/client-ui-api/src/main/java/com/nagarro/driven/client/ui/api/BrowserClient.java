package com.nagarro.driven.client.ui.api;


public interface BrowserClient<T> extends UiClient<T> {

  void go(String url);

  String getCurrentUrl();

  void refresh();

  Object evaluateScript(String script, Object ... args);

  History history();

  Prompt prompt();
}
