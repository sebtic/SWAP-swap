package org.projectsforge.swap.server.starter;

import org.projectsforge.utils.propertyregistry.IntegerProperty;
import org.projectsforge.utils.propertyregistry.PropertyHolder;
import org.projectsforge.utils.propertyregistry.StringProperty;

public class ServerPropertyHolder implements PropertyHolder {

  public static final IntegerProperty httpMinThread = new IntegerProperty("server.http.minThreads", 2);

  public static final IntegerProperty httpMaxThread = new IntegerProperty("server.http.maxThreads", 30);

  /** Port for remoting over a secure connection */
  public static final IntegerProperty httpsPort = new IntegerProperty("server.https.port", 9443);

  /** Host to listen for over a secure connection for remoting */
  public static final StringProperty httpsHost = new StringProperty("server.https.host", "localhost");

  /** Name of file to use for storing the key stores related to remoting */
  public static final StringProperty httpsKeyStoreName = new StringProperty("server.https.keystorename",
      "serverKeyStore");

  /** Password used to protect the remoting key store */
  public static final StringProperty httpsKeystorePassword = new StringProperty("server.https.keystorepassword",
      "dummy password");
}
