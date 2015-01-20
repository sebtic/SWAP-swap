/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr>
 * 
 * This file is part of SWAP.
 * 
 * SWAP is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * SWAP is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with SWAP. If not, see <http://www.gnu.org/licenses/>.
 * 
 * $Id: UntrustedCertificationAgencyManager.java 17 2010-03-10 08:39:15Z sebtic
 * $
 */
package org.projectsforge.swap.proxy.certificate;

import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import javax.annotation.PostConstruct;
import javax.swing.JOptionPane;
import org.projectsforge.swap.core.environment.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The class that manages an untrusted CA manager.
 * 
 * @author Sébastien Aupetit
 */
public class UntrustedCertificationAgencyManager extends CertificationAgencyManager {

  /** The logger. */
  private final Logger logger = LoggerFactory.getLogger(UntrustedCertificationAgencyManager.class);

  /** The environment. */
  @Autowired
  private Environment environment;

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
      logger.debug(
          "An error occurred while loading the untrusted CA. A new one will be generated.", e);
    }

    if (loaded) {
      try {
        checkCertificateValidity();
        loaded = true;
      } catch (final CertificateExpiredException e) {
        loaded = false;
        logger.debug("The untrusted CA certificate has expired", e);
      } catch (final CertificateNotYetValidException e) {
        loaded = false;
        logger.debug("The untrusted CA certificate is not yet valid", e);
      } catch (final Exception e) {
        loaded = false;
        logger.debug("The untrusted CA certificate is not valid", e);
      }
    }

    if (!loaded) {
      try {
        generateCA();
      } catch (final Exception e) {
        JOptionPane.showMessageDialog(null, "Can not generate the untrusted CA. Exiting...",
            "Can not generate the untrusted CA", JOptionPane.ERROR_MESSAGE);
        logger.error("Can not generate the untrusted CA", e);
        environment.stop();
      }
      try {
        saveCA();
      } catch (final Exception e) {
        JOptionPane.showMessageDialog(null, "Can not save the untrusted CA. Exiting...",
            "Can not save the untrusted CA", JOptionPane.ERROR_MESSAGE);
        logger.error("Can not save the untrusted CA", e);
        environment.stop();
      }
    }
  }

}
