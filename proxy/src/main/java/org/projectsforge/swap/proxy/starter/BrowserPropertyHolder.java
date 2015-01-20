package org.projectsforge.swap.proxy.starter;

import org.projectsforge.utils.propertyregistry.PropertyHolder;
import org.projectsforge.utils.propertyregistry.StringProperty;

public class BrowserPropertyHolder implements PropertyHolder {

  public static final StringProperty browserCommand = new StringProperty(
      "swap.advancedconfiguration.browser.cmd", "opera");

  public static final StringProperty browserAgrs = new StringProperty(
      "swap.advancedconfiguration.browser.args", "{url}");

}
