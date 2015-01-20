package org.projectsforge.swap.core.mime.css;

import org.projectsforge.utils.propertyregistry.IntegerProperty;
import org.projectsforge.utils.propertyregistry.PropertyHolder;
import org.projectsforge.utils.propertyregistry.SystemValueHolder;

public class CssPropertyHolder implements PropertyHolder {

  public static final IntegerProperty cachedNamesMaxSize = new IntegerProperty(
      new SystemValueHolder("org.projectsforge.swap.mime.css.cachedNamesMaxSize"), 2048);

}
