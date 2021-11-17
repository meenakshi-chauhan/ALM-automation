package com.nagarro.driven.core.weblocator.file.util;

import com.nagarro.driven.core.config.CoreConfig;

import javax.xml.bind.JAXBException;

/**
 * Creates the instance of the appropriate web locator file type.
 *
 * @author nagarro
 */
public class WebLocatorFileTypeFactory {

  private static WebLocatorFileType webLocatorFileType = null;
  private static final CoreConfig.CoreConfigSpec CONFIG = CoreConfig.getInstance();

  /**
   * Gets the instance of the web locator file type.
   *
   * @return the instance of the web locator file type
   */
  public static WebLocatorFileType getInstance() throws JAXBException {
    String file = CONFIG.webElementFileType();

    if (WebLocatorFileTypeEnum.XML.toString().equalsIgnoreCase(file.trim())) {
      webLocatorFileType = new WebLocatorXmlFile();
    } else if (WebLocatorFileTypeEnum.JSON.toString().equalsIgnoreCase(file.trim())) {
      webLocatorFileType = new WebLocatorJsonFile();
    }

    return webLocatorFileType;
  }

  /*
   * Enum for all the file types.
   */
  private enum WebLocatorFileTypeEnum {
    XML,
    EXCEL,
    JSON,
    DATABASE
  }
}
