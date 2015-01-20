package org.projectsforge.swap.proxy.proxy;

import org.projectsforge.utils.propertyregistry.IntegerProperty;
import org.projectsforge.utils.propertyregistry.PropertyHolder;
import org.projectsforge.utils.propertyregistry.StringProperty;

public class ProxyPropertyHolder implements PropertyHolder {

  public static final IntegerProperty httpMinThread = new IntegerProperty("proxy.http.minThreads",
      2);

  public static final IntegerProperty httpMaxThread = new IntegerProperty("proxy.http.maxThreads",
      30);

  public static final IntegerProperty httpPort = new IntegerProperty("proxy.http.port", 8080);

  public static final IntegerProperty httpSecurePort = new IntegerProperty("proxy.http.securePort",
      8443);

  public static final StringProperty httpHost = new StringProperty("proxy.http.host", "localhost");

}
