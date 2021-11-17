package com.nagarro.driven.core.util;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClipboardUtil {

  private static final Logger log = LoggerFactory.getLogger(ClipboardUtil.class);

  public String getDataFromClipboard() {
    try {
      return (String)
          Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
    } catch (IllegalStateException
        | HeadlessException
        | IOException
        | UnsupportedFlavorException e) {
      log.debug("Unable to get data from clipboard due to following error : {}", e.getMessage());
    }
    return null;
  }
}
