package org.projectsforge.swap.core.environment.impl;

import java.io.File;
import org.projectsforge.utils.propertyregistry.FileProperty;
import org.projectsforge.utils.propertyregistry.IntegerProperty;
import org.projectsforge.utils.propertyregistry.PropertyHolder;
import org.projectsforge.utils.propertyregistry.StringProperty;
import org.projectsforge.utils.propertyregistry.SystemValueHolder;
import org.projectsforge.utils.temporarystreams.TemporaryStreamsFactory;

public class TemporaryStreamPropertyHolder implements PropertyHolder {
  public static final IntegerProperty inMemoryMaxSize = new IntegerProperty(new SystemValueHolder(
      TemporaryStreamsFactory.inMemoryMaxSizeProperty), 128 * 1024 * 1024);

  public static final StringProperty prefix = new StringProperty(new SystemValueHolder(
      TemporaryStreamsFactory.prefixProperty), "temp");

  public static final StringProperty suffix = new StringProperty(new SystemValueHolder(
      TemporaryStreamsFactory.suffixProperty), ".tmp");

  public static final FileProperty directory = new FileProperty(new SystemValueHolder(
      TemporaryStreamsFactory.directoryProperty), new File("swap"));

}
