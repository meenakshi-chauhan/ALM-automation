package com.nagarro.driven.core.weblocator.file.util;

import com.nagarro.driven.core.constant.FrameworkCoreConstant;

import javax.xml.bind.JAXBException;

/**
 * Read and return the value of web locator for xml file type.
 *
 * @author nagarro
 */
public class WebLocatorJsonFile extends WebLocatorFileType {

    public WebLocatorJsonFile() throws JAXBException {
        super(FrameworkCoreConstant.JSON_FILE_EXTENSION, FrameworkCoreConstant.JSON_MEDIA_TYPE);
    }
}