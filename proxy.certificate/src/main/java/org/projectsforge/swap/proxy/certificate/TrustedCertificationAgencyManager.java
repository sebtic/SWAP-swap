/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr> This file
 * is part of SWAP. SWAP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version. SWAP is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with SWAP. If not, see
 * <http://www.gnu.org/licenses/>. $Id: TrustedCertificationAgencyManager.java
 * 17 2010-03-10 08:39:15Z sebtic $
 */
package org.projectsforge.swap.proxy.certificate;

import java.io.File;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import javax.annotation.PostConstruct;
import javax.swing.JOptionPane;
import org.projectsforge.swap.core.environment.Environment;
import org.projectsforge.swap.core.environment.impl.EnvironmentPropertyHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The class that manages a trusted CA manager.
 * 
 * @author Sébastien Aupetit
 */
public class TrustedCertificationAgencyManager extends CertificationAgencyManager {

  /** The environment. */
  @Autowired
  private Environment environment;

  /** The logger. */
  private final Logger logger = LoggerFactory.getLogger(TrustedCertificationAgencyManager.class);

  /**
   * Inits the manager by loading certificates and/or creating the CA
   * certificate.
   */
  @PostConstruct
  public void init() {
    boolean loaded = false;

    try {
      loadCA();
      loaded = true;
    } catch (final Exception e) {
      loaded = false;
      if (environment.getAllowUserInteraction()) {
        JOptionPane
            .showMessageDialog(
                null,
                String
                    .format(
                        "The trusted CA certificate can not be loaded. A new one will be generated.\nIt must be imported into your user agent for correct handling of trusted SSL website. The PEM file is located at %s.",
                        new File(EnvironmentPropertyHolder.configurationDirectory.get(),
                            getPemFileName())), "CA certificate can not be loaded",
                JOptionPane.WARNING_MESSAGE);
      }
      logger.debug("An error occurred while loading the trusted CA. A new one will be generated.",
          e);
    }

    if (loaded) {
      try {
        checkCertificateValidity();
        loaded = true;
      } catch (final CertificateExpiredException e) {
        loaded = false;
        if (environment.getAllowUserInteraction()) {
          JOptionPane
              .showMessageDialog(
                  null,
                  String
                      .format(
                          "The trusted CA has expired. A new one will be generated.\nIt must be imported into your user agent for correct handling of trusted SSL website. The PEM file is located at %s.",
                          new File(EnvironmentPropertyHolder.configurationDirectory.get(),
                              getPemFileName())), "CA certificate expired",
                  JOptionPane.WARNING_MESSAGE);
        }
        logger.debug("The trusted CA certificate has expired", e);
      } catch (final CertificateNotYetValidException e) {
        loaded = false;
        if (environment.getAllowUserInteraction()) {
          JOptionPane
              .showMessageDialog(
                  null,
                  String
                      .format(
                          "The trusted CA is not yet valid. A new one will be generated.\nIt must be imported into your user agent for correct handling of trusted SSL website. The PEM file is located at %s.",
                          new File(EnvironmentPropertyHolder.configurationDirectory.get(),
                              getPemFileName())), "CA certificate not yet valid",
                  JOptionPane.WARNING_MESSAGE);
        }
        logger.debug("The trusted CA certificate is not yet valid", e);
      } catch (final Exception e) {
        loaded = false;
        if (environment.getAllowUserInteraction()) {
          JOptionPane
              .showMessageDialog(
                  null,
                  String
                      .format(
                          "The trusted CA is not valid. A new one will be generated.\nIt must be imported into your user agent for correct handling of trusted SSL website. The PEM file is located at %s.",
                          new File(EnvironmentPropertyHolder.configurationDirectory.get(),
                              getPemFileName())), "CA certificate not valid",
                  JOptionPane.WARNING_MESSAGE);
        }
        logger.debug("The trusted CA certificate is not valid", e);
      }
    }

    if (!loaded) {
      try {
        generateCA();
      } catch (final Exception e) {
        if (environment.getAllowUserInteraction()) {
          JOptionPane.showMessageDialog(null, "Can not generate the trusted CA. Exiting...",
              "Can not generate the trusted CA", JOptionPane.ERROR_MESSAGE);
        }
        logger.error("Can not generate the trusted CA", e);

        environment.stop();
      }
      try {
        saveCA();
      } catch (final Exception e) {
        if (environment.getAllowUserInteraction()) {
          JOptionPane.showMessageDialog(null, "Can not save the trusted CA. Exiting...",
              "Can not save the trusted CA", JOptionPane.ERROR_MESSAGE);
        }
        logger.error("Can not save the trusted CA", e);

        environment.stop();
      }
    }
  }
}
