package com.nagarro.driven.core.weblocator.file.util;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class WebLocatorFiles {

  private static List<File> listOfFiles = new ArrayList<>();
  private String fileExtension;

  public WebLocatorFiles(String extension) {
    fileExtension = extension;
  }

  public void recursivePrint(File[] arr, int index, int level) {
    if (index == arr.length) {
      return;
    }

    if (arr[index].isFile()) {
      if (arr[index].getName().endsWith(fileExtension)) {
        listOfFiles.add(arr[index]);
      }
    } else if (arr[index].isDirectory()) {
      if (arr[index].getName().endsWith(fileExtension)) {
        listOfFiles.add(arr[index]);
      }
      recursivePrint(arr[index].listFiles(), 0, level + 1);
    }
    recursivePrint(arr, ++index, level);
  }

  public List<File> getFiles() {
    Path p = Paths.get(System.getProperty("user.dir"));
    String path = p.getParent().toString();
    File maindir = new File(path);

    if (maindir.exists() && maindir.isDirectory()) {
      File[] arr = maindir.listFiles();
      recursivePrint(arr, 0, 0);
    }

    List<File> finalListOfFiles = new ArrayList<>();
    for (File file : listOfFiles) {
      if (!file.getAbsolutePath().toLowerCase().contains("target")
          && file.getAbsolutePath().toLowerCase().contains("resources")) {
        finalListOfFiles.add(file);
      }
    }
    return finalListOfFiles;
  }
}
