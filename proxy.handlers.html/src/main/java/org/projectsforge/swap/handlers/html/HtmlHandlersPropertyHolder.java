package org.projectsforge.swap.handlers.html;

import org.projectsforge.utils.propertyregistry.BooleanProperty;
import org.projectsforge.utils.propertyregistry.PropertyHolder;

public class HtmlHandlersPropertyHolder implements PropertyHolder {

  public static final BooleanProperty statisticalDetectionFirst = new BooleanProperty(
      "htmlcharsetdetector.statisticalDetectionFirst", false);
}
