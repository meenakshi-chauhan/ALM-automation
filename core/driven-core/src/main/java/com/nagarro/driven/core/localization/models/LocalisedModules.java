package com.nagarro.driven.core.localization.models;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"localeString"})
@XmlRootElement(name = "LocalisedModules")
public class LocalisedModules {

  @XmlElement(name = "LocaleString")
  List<LocaleString> localeStrings;

  /**
   * Gets the localeString  list.
   *
   * @return the list of localeString
   */
  public List<LocaleString> getLocaleStrings() {
    return localeStrings;
  }

  /**
   * Sets the element list.
   *
   * @param localeString the list of localeStringText
   */
  public void setLocaleStrings(List<LocaleString> localeString) {
    this.localeStrings = localeString;
  }
}
