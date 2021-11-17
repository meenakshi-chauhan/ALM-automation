package com.nagarro.driven.client.ui.api;

import java.io.File;
import java.util.Optional;

public interface Window {

  void close();

  String getTitle();

  void maximize();

  void fullscreen();

  Dimension getSize();

  void setSize(Dimension size);

  Optional<File> takeScreenshot();
}
