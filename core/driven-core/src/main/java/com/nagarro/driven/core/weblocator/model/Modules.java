package com.nagarro.driven.core.weblocator.model;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"element"})
@XmlRootElement(name = "Modules")
public class Modules {

  @XmlElement(name = "Element")
  List<Element> element;

  /**
   * Gets the element list.
   *
   * @return the list of element
   */
  public List<Element> getElement() {
    return element;
  }

  /**
   * Sets the element list.
   *
   * @param element the list of element
   */
  public void setElement(List<Element> element) {
    this.element = element;
  }
}
