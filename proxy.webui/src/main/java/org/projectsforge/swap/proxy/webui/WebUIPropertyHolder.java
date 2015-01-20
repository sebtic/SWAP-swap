package org.projectsforge.swap.proxy.webui;

import org.projectsforge.utils.propertyregistry.PropertyHolder;
import org.projectsforge.utils.propertyregistry.StringProperty;

public class WebUIPropertyHolder implements PropertyHolder {

  public static final StringProperty hostname = new StringProperty("proxy.webui.hostname",
      "proxyapi.swap.projectsforge.org");

}
