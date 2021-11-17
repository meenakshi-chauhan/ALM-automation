package com.nagarro.driven.core.weblocator.file.util;

import com.nagarro.driven.core.constant.FrameworkCoreConstant;

import javax.xml.bind.JAXBException;

/**
 * Read and return the value of web locator for xml file type.
 *
 * @author nagarro
 */
public class WebLocatorXmlFile extends WebLocatorFileType {

    public WebLocatorXmlFile() throws JAXBException {
        super(FrameworkCoreConstant.XML_FILE_EXTENSION, FrameworkCoreConstant.XML_MEDIA_TYPE);
    }
}