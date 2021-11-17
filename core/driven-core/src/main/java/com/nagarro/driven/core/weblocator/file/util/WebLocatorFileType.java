package com.nagarro.driven.core.weblocator.file.util;

import com.nagarro.driven.core.util.AutomationFrameworkException;
import com.nagarro.driven.core.weblocator.model.Element;
import com.nagarro.driven.core.weblocator.model.ElementProperty;
import com.nagarro.driven.core.weblocator.model.Modules;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Interface for all the file types used to find the web locator.
 *
 * @author nagarro
 */
public abstract class WebLocatorFileType {
  private static final String ELE_NOT_FOUND_MSG = "{} not found in map for pagename {}";
  private static final String OR_EXCEPTION_MSG = "Exception occurred while reading the object repository for page {}";
  private static final String ADD_ENTRY_MSG = "Adding the entry in webLocatorMap for pagename {}";
  private final String orFileExtension;
  private final String orMediaType;
  private static final Logger log = LoggerFactory.getLogger(WebLocatorFileType.class);
  private static final Map<String, Modules> webLocatorMap = new HashMap<>();
  private final JAXBContext jc;
  private final Unmarshaller unmarshaller;


  protected WebLocatorFileType(String fileType, String mediaType) throws JAXBException {
    orFileExtension = fileType;
    orMediaType = mediaType;
    jc = JAXBContextFactory.createContext(new Class[]{Modules.class}, null);
    unmarshaller = jc.createUnmarshaller();
    unmarshaller.setProperty(UnmarshallerProperties.MEDIA_TYPE, orMediaType);
  }


  public Optional<File> findOrFile(String pageName, String fileFormat) {
    return new WebLocatorFiles(fileFormat).getFiles().stream().filter(file -> file.getName().split("[.]")[0].equals(pageName)).findFirst();
  }


  /**
   * Gets the locator value.
   *
   * @param pageName,
   *            name of the page
   * @param elementName,
   *            unique name of the element
   * @return map of locator type and value
   */
  public Map<String, String> getLocatorValue(String pageName, String elementName) {
    Map<String, String> valueOfElement = new HashMap<>();
    Optional<Modules> optionalModules = getModules(pageName);
    if (optionalModules.isEmpty()) {
      log.warn(OR_EXCEPTION_MSG, pageName);
    } else {
      valueOfElement = getElementPropertyList(elementName, pageName, optionalModules.get())
              .stream()
              .collect(Collectors.toMap(ElementProperty::getType, ElementProperty::getValue));
    }
    return valueOfElement;
  }

  /**
   * Gets the locator value.
   *
   * @param pageName,
   *            name of the page
   * @param elementName,
   *            unique name of the element
   * @return List of ElementProperty
   */
  public List<ElementProperty> getLocator(String pageName, String elementName) {
    Optional<Modules> optionalModules = getModules(pageName);
    if (optionalModules.isEmpty()) {
      log.warn(OR_EXCEPTION_MSG, pageName);
      return Collections.emptyList();
    } else {
      return getElementPropertyList(elementName, pageName, optionalModules.get());
    }
  }

  /**
   * Updates the locator value.
   *
   * @param pageName,
   *            name of the page
   * @param elementName,
   *            unique name of the element
   * @param element,
   *            ElementProperty object containing updated values
   *
   */
  public void updateLocator(String pageName, String elementName, ElementProperty element) {
    Optional<File> optionalFile = findOrFile(pageName, orFileExtension);
    if(optionalFile.isEmpty()) {
      log.error("OR for {} with {} format not found", pageName, orFileExtension);
    }
    // Read the webLocatorMap and return ElementProperty List
    Optional<Modules> optionalModules = getModules(pageName);
    if (optionalModules.isEmpty()) {
      log.debug(ELE_NOT_FOUND_MSG, elementName,pageName);
    } else {
      List<ElementProperty> elementPropertyList = new ArrayList<>();
      elementPropertyList.add(element);
      optionalModules.get().getElement().stream().filter(entry -> entry.getNameOfElement().equals(elementName)).forEach(orElement -> {
        orElement.setElementPropertyList(elementPropertyList);
        try {
          Marshaller jaxbMarshaller = jc.createMarshaller();
          jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
          jaxbMarshaller.setProperty(MarshallerProperties.MEDIA_TYPE, orMediaType);
          jaxbMarshaller.marshal(optionalModules.get(), optionalFile.get());
        } catch (JAXBException e) {
          log.error("Element Update on pagename {} failed", pageName, e);
        }
      });
    }
  }

  private List<ElementProperty> getElementPropertyList(String elementName, String pageName, Modules modules) {
    Optional<Element> optionalElement = modules.getElement().stream().filter(element -> element.getNameOfElement().equals(elementName)).findFirst();
    if (optionalElement.isEmpty()) {
      log.warn(ELE_NOT_FOUND_MSG, elementName, pageName);
      return Collections.emptyList();
    } else {
      return optionalElement.get().getElementPropertyList();
    }
  }


  private Optional<Modules> getModules(String pageName) {
    // Read the or file and put the page name and module object in
    // webLocatorMap map.
    Modules modules;
    if (!webLocatorMap.containsKey(pageName)) {
      try {
        Optional<File> optionalFile = findOrFile(pageName, orFileExtension);
        if (optionalFile.isEmpty()) {
        log.debug("OR for {} with {} format not found", pageName, orFileExtension);
        return Optional.empty();
      }
      modules = (Modules) unmarshaller.unmarshal(optionalFile.get());
      log.debug(ADD_ENTRY_MSG, pageName);
      webLocatorMap.put(pageName, modules);
    } catch (JAXBException e) {
      log.error(
              OR_EXCEPTION_MSG,
              pageName);
      throw new AutomationFrameworkException("Couldn't load or serialize object repository", e);
    }
  }
  else {
      modules = webLocatorMap.get(pageName);
    }
  return Optional.of(modules);
}
}
