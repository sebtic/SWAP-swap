package org.projectsforge.swap.core.environment.impl;

import java.io.File;
import org.projectsforge.utils.propertyregistry.BooleanProperty;
import org.projectsforge.utils.propertyregistry.FileProperty;
import org.projectsforge.utils.propertyregistry.PropertyHolder;
import org.projectsforge.utils.visitor.Visitor;

public class VisitorPropertyHolder implements PropertyHolder {

  public static final FileProperty statisticsDir = new FileProperty(
      Visitor.VISITOR_STATISTICS_DIR_PROPERTY, null, true);

  public static final BooleanProperty enableProfiling = new BooleanProperty(
      Visitor.ENABLE_PROFILING_PROPERTY, true);

  static {
    statisticsDir.set(new File(EnvironmentPropertyHolder.configurationDirectory.get(),
        "visitorstatistics"));
  }
}
