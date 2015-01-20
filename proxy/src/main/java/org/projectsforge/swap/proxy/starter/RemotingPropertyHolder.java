package org.projectsforge.swap.proxy.starter;

import org.projectsforge.utils.propertyregistry.IntegerProperty;
import org.projectsforge.utils.propertyregistry.PropertyHolder;
import org.projectsforge.utils.propertyregistry.StringProperty;

public class RemotingPropertyHolder implements PropertyHolder {

  public static final StringProperty serverName = new StringProperty("remoting.server.name",
      "localhost");

  public static final IntegerProperty serverPort = new IntegerProperty("remoting.server.port", 9080);

}
