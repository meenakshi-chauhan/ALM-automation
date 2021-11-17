package com.nagarro.driven.core.weblocator.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"type", "value"})
public class ElementProperty {

  @XmlElement(name = "Type")
  String type;

  @XmlElement(name = "Value")
  String value;

  public ElementProperty(String type, String value) {
    this.type = type;
    this.value = value;
  }

  public ElementProperty() {
  }

  /**
   * Gets the type.
   *
   * @return the type
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the type.
   *
   * @param type the type
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * Gets the value.
   *
   * @return the value
   */
  public String getValue() {
    return value;
  }

  /**
   * Sets the value.
   *
   * @param value the value
   */
  public void setValue(String value) {
    this.value = value;
  }
}
