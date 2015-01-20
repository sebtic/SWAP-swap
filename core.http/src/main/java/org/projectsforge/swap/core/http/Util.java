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
 * <http://www.gnu.org/licenses/>. $Id: Util.java 125 2012-04-04 14:59:59Z
 * sebtic $
 */
package org.projectsforge.swap.core.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class containing only static methods.
 * 
 * @author Sébastien Aupetit
 */
public class Util {
  /** The logger. */
  private final static Logger logger = LoggerFactory.getLogger(Util.class);

  /**
   * Dump to.
   * 
   * @param in the in
   * @param out the out
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void dumpTo(final InputStream in, final OutputStream out) throws IOException {
    final byte[] buffer = new byte[2048];
    int len;
    do {
      len = in.read(buffer);
      if (len > 0) {
        out.write(buffer, 0, len);
      }
    } while (len > 0);
    out.flush();
  }

  /**
   * Compute the URL from a request.
   * 
   * @param request the request
   * @return the URI
   * @throws MalformedURLException the malformed url exception
   */
  public static URL getURL(final HttpServletRequest request) throws MalformedURLException {
    return new URL(request.getScheme(), request.getServerName(), request.getServerPort(),
        request.getRequestURI()
            + (request.getQueryString() == null ? "" : "?" + request.getQueryString()));
  }

  /**
   * Send the response headers to the client. A filtering is applied.
   * 
   * @param response the response
   * @param headers the headers
   */
  public static void sendResponseHeaders(final HttpServletResponse response, final Headers headers) {

    final Headers modifiedHeaders = new Headers();
    modifiedHeaders.copy(headers);
    // remove problematic headers
    modifiedHeaders.removeAll(ResponseHeaderConstants.cachedResponseFilter);
    // disable client-side caching
    modifiedHeaders.set("Cache-Control", "no-cache");
    modifiedHeaders.set("Pragma", "no-cache");
    modifiedHeaders.set("Expires", "-1");

    Util.logger.debug("Response headers sent : {}", modifiedHeaders);
    for (final Header header : modifiedHeaders) {
      response.addHeader(header.getName(), header.getValue());
    }
  }
}
