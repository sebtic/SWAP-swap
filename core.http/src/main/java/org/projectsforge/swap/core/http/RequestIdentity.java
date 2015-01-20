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
package org.projectsforge.swap.core.http;

import java.net.URL;

/**
 * The Class RequestIdentity.
 * 
 * @author Sébastien Aupetit
 */
class RequestIdentity {

  /** The headers. */
  private final Headers headers = new Headers();

  /** The method. */
  private final String method;

  /** The protocol version. */
  private final String protocolVersion;

  /** The url. */
  private final URL url;

  /**
   * Instantiates a new request identity.
   * 
   * @param request the request
   */
  public RequestIdentity(final Request request) {
    headers.copy(request.getHeaders());
    // remove non differentiating headers
    headers.removeAll(RequestHeaderConstants.REFERER);

    method = request.getMethod();
    protocolVersion = request.getProtocolVersion();
    url = request.getURL();
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
    if (!(obj instanceof RequestIdentity)) {
      return false;
    }
    final RequestIdentity other = (RequestIdentity) obj;
    if (headers == null) {
      if (other.headers != null) {
        return false;
      }
    } else if (!headers.equals(other.headers)) {
      return false;
    }
    if (method == null) {
      if (other.method != null) {
        return false;
      }
    } else if (!method.equals(other.method)) {
      return false;
    }
    if (protocolVersion == null) {
      if (other.protocolVersion != null) {
        return false;
      }
    } else if (!protocolVersion.equals(other.protocolVersion)) {
      return false;
    }
    if (url == null) {
      if (other.url != null) {
        return false;
      }
    } else if (!url.equals(other.url)) {
      return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((headers == null) ? 0 : headers.hashCode());
    result = prime * result + ((method == null) ? 0 : method.hashCode());
    result = prime * result + ((protocolVersion == null) ? 0 : protocolVersion.hashCode());
    result = prime * result + ((url == null) ? 0 : url.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "RequestIdentity [method=" + method + ", url=" + url + ", protocolVersion="
        + protocolVersion + ", headers=" + headers + "]";
  }
}
