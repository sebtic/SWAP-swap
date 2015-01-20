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
 * <http://www.gnu.org/licenses/>. $Id: ResponseHeaderConstants.java 83
 * 2011-06-08 15:37:32Z sebtic $
 */
package org.projectsforge.swap.core.http;

import java.util.Arrays;
import java.util.List;

/**
 * HTTP response header constants.
 * 
 * @author Sébastien Aupetit
 */
public interface ResponseHeaderConstants {

  /** The Constant ACCEPT_RANGES. */
  public final static String ACCEPT_RANGES = "Accept-Ranges";

  /** The Constant AGE. */
  public final static String AGE = "Age";

  /** The Constant ALLOW. */
  public final static String ALLOW = "Allow";

  /** The Constant CACHE_CONTROL. */
  public final static String CACHE_CONTROL = "Cache-Control";

  /** The Constant CONTENT_ENCODING. */
  public final static String CONTENT_ENCODING = "Content-Encoding";

  /** The Constant CONTENT_LANGUAGE. */
  public final static String CONTENT_LANGUAGE = "Content-Language";

  /** The Constant CONTENT_LENGTH. */
  public final static String CONTENT_LENGTH = "Content-Length";

  /** The Constant CONTENT_LOCATION. */
  public final static String CONTENT_LOCATION = "Content-Location";

  /** The Constant CONTENT_MD5. */
  public final static String CONTENT_MD5 = "Content-MD5";

  /** The Constant CONTENT_RANGE. */
  public final static String CONTENT_RANGE = "Content-Range";

  /** The Constant CONTENT_TYPE. */
  public final static String CONTENT_TYPE = "Content-Type";

  /** The Constant DATE. */
  public final static String DATE = "Date";

  /** The Constant ETAG. */
  public final static String ETAG = "ETag";

  /** The Constant EXPIRES. */
  public final static String EXPIRES = "Expires";

  /** The Constant CONNECTION. */
  public final static String CONNECTION = "Connection";

  /** The Constant KEEP_ALIVE. */
  public final static String KEEP_ALIVE = "Keep-Alive";

  /** The Constant LAST_MODIFIED. */
  public final static String LAST_MODIFIED = "Last-Modified";

  /** The Constant LOCATION. */
  public final static String LOCATION = "Location";

  /** The Constant MIME_VERSION. */
  public final static String MIME_VERSION = "MIME-Version";

  /** The Constant PRAGMA. */
  public final static String PRAGMA = "Pragma";

  /** The Constant PROXY_AUTHENTICATE. */
  public final static String PROXY_AUTHENTICATE = "Proxy-Authenticate";

  /** The Constant PROXY_CONNECTION. */
  public final static String PROXY_CONNECTION = "Proxy-Connection";

  /** The Constant REFRESH. */
  public final static String REFRESH = "Refresh";

  /** The Constant RETRY_AFTER. */
  public final static String RETRY_AFTER = "Retry-After";

  /** The Constant SERVER. */
  public final static String SERVER = "Server";

  /** The Constant SET_COOKIE. */
  public final static String SET_COOKIE = "Set-Cookie";

  /** The Constant SET_COOKIE2. */
  public final static String SET_COOKIE2 = "Set-Cookie2";

  /** The Constant TE. */
  public final static String TE = "TE";

  /** The Constant TRAILER. */
  public final static String TRAILER = "Trailer";

  /** The Constant TRANSFER_ENCODING. */
  public final static String TRANSFER_ENCODING = "Transfer-Encoding";

  /** The Constant UPGRADE. */
  public final static String UPGRADE = "Upgrade";

  /** The Constant VARY. */
  public final static String VARY = "Vary";

  /** The Constant VIA. */
  public final static String VIA = "Via";

  /** The Constant WARNING. */
  public final static String WARNING = "Warning";

  /** The Constant WWW_AUTHENTICATE. */
  public final static String WWW_AUTHENTICATE = "WWW-Authenticate";

  /** The Constant X_FORWARDED_FOR. */
  public final static String X_FORWARDED_FOR = "X-Forwarded-For";

  /** The headers that are removed when the response is sent to the client. */
  public final static List<String> cachedResponseFilter = Arrays.asList(
      ResponseHeaderConstants.CACHE_CONTROL, ResponseHeaderConstants.PROXY_CONNECTION,
      ResponseHeaderConstants.CONNECTION, ResponseHeaderConstants.KEEP_ALIVE,
      ResponseHeaderConstants.TRANSFER_ENCODING, ResponseHeaderConstants.TE,
      ResponseHeaderConstants.TRAILER, ResponseHeaderConstants.UPGRADE,
      ResponseHeaderConstants.DATE, ResponseHeaderConstants.CACHE_CONTROL,
      ResponseHeaderConstants.ETAG, ResponseHeaderConstants.EXPIRES,
      ResponseHeaderConstants.LAST_MODIFIED, ResponseHeaderConstants.PRAGMA,
      ResponseHeaderConstants.CONTENT_ENCODING, ResponseHeaderConstants.CONTENT_LENGTH,
      ResponseHeaderConstants.CONTENT_MD5, ResponseHeaderConstants.MIME_VERSION);

}
