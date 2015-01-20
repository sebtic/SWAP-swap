package org.projectsforge.swap.core.persistence;

import org.projectsforge.utils.propertyregistry.BooleanProperty;
import org.projectsforge.utils.propertyregistry.PropertyHolder;

public class H2ConsolePropertyHolder implements PropertyHolder {

  public static final BooleanProperty h2ConsoleEnabled = new BooleanProperty("h2console.enabled",
      true);

}
