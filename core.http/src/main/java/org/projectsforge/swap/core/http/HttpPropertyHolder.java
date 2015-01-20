package org.projectsforge.swap.core.http;

import org.projectsforge.utils.propertyregistry.BooleanProperty;
import org.projectsforge.utils.propertyregistry.PropertyHolder;
import org.projectsforge.utils.propertyregistry.StringProperty;
import org.projectsforge.utils.propertyregistry.SystemValueHolder;

public class HttpPropertyHolder implements PropertyHolder {

  public static final BooleanProperty preferIPv4Stack = new BooleanProperty(new SystemValueHolder(
      "java.net.preferIPv4Stack"), true);

  public static final BooleanProperty preferIPv6Addresses = new BooleanProperty(
      new SystemValueHolder("java.net.preferIPv6Addresses"), false);

  public static final StringProperty agentName = new StringProperty("agent.name",
      "SmartWebAccessibilityProxy");

}
