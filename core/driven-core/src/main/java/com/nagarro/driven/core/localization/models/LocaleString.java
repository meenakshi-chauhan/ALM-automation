package com.nagarro.driven.core.localization.models;



import javax.xml.bind.annotation.*;


@XmlAccessorType(XmlAccessType.FIELD)
public class LocaleString {

  @XmlAttribute(name="name")
  private String name;

  @XmlValue
  String localeText;

  /**
   * Gets the name of element.
   *
   * @return the name of element
   */
  public String getLocaleString() {
    return localeText;
  }

  /**
   * Sets the name of element.
   *
   * @param elementValue the name of element
   */
  public void setLocaleString(String elementValue) {
    this.localeText = elementValue;
  }
  /**
   * Gets the name of element.
   *
   * @return the name of element
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of element.
   *
   * @param name the name of element
   */
  public void setName(String name) {
    this.name = name;
  }

}
