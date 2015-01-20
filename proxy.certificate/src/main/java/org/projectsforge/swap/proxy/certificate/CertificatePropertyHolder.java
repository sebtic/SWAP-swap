package org.projectsforge.swap.proxy.certificate;

import org.projectsforge.utils.propertyregistry.PropertyHolder;
import org.projectsforge.utils.propertyregistry.StringProperty;

public class CertificatePropertyHolder implements PropertyHolder {
  public static final StringProperty trustKeystoreName = new StringProperty(
      "org.projectsforge.swap.certificate.trustKeystoreName", "trustKeystore");

  public static final StringProperty trustKeystorePassword = new StringProperty(
      "org.projectsforge.swap.certificate.trustKeystorePassword", "dummy password");

  public static final StringProperty pemPassword = new StringProperty(
      "org.projectsforge.swap.certificate.pemPassword", "dummy key password");
  
  public static final StringProperty pemDirectory = new StringProperty("org.projectsforge.swap.certificate.pemDirectory", "generated-cert");

}
