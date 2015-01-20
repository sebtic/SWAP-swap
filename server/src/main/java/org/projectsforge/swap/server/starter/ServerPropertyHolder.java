package org.projectsforge.swap.server.starter;

import org.projectsforge.utils.propertyregistry.IntegerProperty;
import org.projectsforge.utils.propertyregistry.PropertyHolder;
import org.projectsforge.utils.propertyregistry.StringProperty;

public class ServerPropertyHolder implements PropertyHolder {

  public static final IntegerProperty httpMinThread = new IntegerProperty("server.http.minThreads",
      2);

  public static final IntegerProperty httpMaxThread = new IntegerProperty("server.http.maxThreads",
      30);

  public static final IntegerProperty httpPort = new IntegerProperty("server.http.port", 9080);

  public static final StringProperty httpHost = new StringProperty("server.http.host", "");

}
