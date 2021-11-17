package com.nagarro.driven.core.weblocator.model;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"nameOfElement", "elementPropertyList"})
public class Element {

  @XmlElement(name = "NameOfElement")
  String nameOfElement;

  @XmlElement(name = "ElementProperty")
  List<ElementProperty> elementPropertyList;

  /**
   * Gets the name of element.
   *
   * @return the name of element
   */
  public String getNameOfElement() {
    return nameOfElement;
  }

  /**
   * Sets the name of element.
   *
   * @param nameOfElement the name of element
   */
  public void setNameOfElement(String nameOfElement) {
    this.nameOfElement = nameOfElement;
  }

  /**
   * Gets the list of element property.
   *
   * @return the list of element property
   */
  public List<ElementProperty> getElementPropertyList() {
    return elementPropertyList;
  }

  /**
   * Sets the list of element property.
   *
   * @param elementPropertyList the list of element property
   */
  public void setElementPropertyList(List<ElementProperty> elementPropertyList) {
    this.elementPropertyList = elementPropertyList;
  }
}
