/**
 * Copyright 2012 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr> This
 * software is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This software is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this software. If not, see
 * <http://www.gnu.org/licenses/>. $Id$
 */
package org.projectsforge.swap.proxy.certificate;

import java.io.Serializable;

/**
 * The Class CertificateTarget.
 * 
 * @author Sébastien Aupetit
 */
public class CertificateTarget implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The hostname. */
  private final String hostname;

  /** The port. */
  private final int port;

  /**
   * Instantiates a new certificate target.
   * 
   * @param hostname the hostname
   * @param port the port
   */
  public CertificateTarget(final String hostname, final int port) {
    this.hostname = hostname;
    this.port = port;
    if (hostname == null || hostname.isEmpty()) {
      throw new IllegalArgumentException("Hostname must not be null or empty");
    }
    if (port <= 0) {
      throw new IllegalArgumentException("Invalid port number");
    }
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof CertificateTarget)) {
      return false;
    }
    final CertificateTarget other = (CertificateTarget) obj;
    if (hostname == null) {
      if (other.hostname != null) {
        return false;
      }
    } else if (!hostname.equals(other.hostname)) {
      return false;
    }
    if (port != other.port) {
      return false;
    }
    return true;
  }

  /**
   * Gets the hostname.
   * 
   * @return the hostname
   */
  public String getHostname() {
    return hostname;
  }

  /**
   * Gets the port.
   * 
   * @return the port
   */
  public int getPort() {
    return port;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
    result = prime * result + port;
    return result;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return hostname + ':' + port;
  }
}
