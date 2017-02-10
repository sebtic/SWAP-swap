package org.projectsforge.swap.proxy.starter;

import org.projectsforge.utils.propertyregistry.IntegerProperty;
import org.projectsforge.utils.propertyregistry.PropertyHolder;
import org.projectsforge.utils.propertyregistry.StringProperty;

public class RemotingPropertyHolder implements PropertyHolder {

  /** Hostname of the remoting server */
  public static final StringProperty serverName = new StringProperty("remoting.server.name", "localhost");

  /** Port of the remoting server */
  public static final IntegerProperty serverPort = new IntegerProperty("remoting.server.port", 9443);

  /** Filename of the local key stores for remoting */
  public static final StringProperty serverKeyStoreName = new StringProperty("remoting.server.keystore.name",
      "remotingKeyStore");

  /** Password of the local key stores for remoting */
  public static final StringProperty serverKeyStorePassword = new StringProperty("remoting.server.keystore.password",
      "A dummy password");

}
