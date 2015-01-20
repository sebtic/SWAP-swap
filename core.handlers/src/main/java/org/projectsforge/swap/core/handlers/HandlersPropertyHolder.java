package org.projectsforge.swap.core.handlers;

import org.projectsforge.utils.propertyregistry.PropertyHolder;
import org.projectsforge.utils.propertyregistry.StringProperty;

public class HandlersPropertyHolder implements PropertyHolder {
  public static final StringProperty disabledHandlers = new StringProperty("handlers.disabled", "");

}
