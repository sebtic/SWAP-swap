package org.projectsforge.swap.core.environment.impl;

import org.projectsforge.utils.propertyregistry.FileProperty;
import org.projectsforge.utils.propertyregistry.PropertyHolder;

public class EnvironmentPropertyHolder implements PropertyHolder {

  public static final FileProperty configurationDirectory = new FileProperty(
      "configurationDirectory", null, true);
}
