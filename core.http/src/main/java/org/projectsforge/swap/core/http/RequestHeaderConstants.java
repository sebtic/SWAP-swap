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
 * <http://www.gnu.org/licenses/>. $Id: RequestHeaderConstants.java 91
 * 2011-07-18 16:28:31Z sebtic $
 */
package org.projectsforge.swap.core.http;

import java.util.Arrays;
import java.util.List;

/**
 * HTTP request header constants.
 * 
 * @author Sébastien Aupetit
 */
public interface RequestHeaderConstants {

  /** The Constant ACCEPT. */
  public final static String ACCEPT = "Accept";

  /** The Constant ACCEPT_CHARSET. */
  public final static String ACCEPT_CHARSET = "Accept-Charset";

  /** The Constant ACCEPT_ENCODING. */
  public final static String ACCEPT_ENCODING = "Accept-Encoding";

  /** The Constant ACCEPT_LANGUAGE. */
  public final static String ACCEPT_LANGUAGE = "Accept-Language";

  /** The Constant AUTHORIZATION. */
  public final static String AUTHORIZATION = "Authorization";

  /** The Constant CACHE_CONTROL. */
  public final static String CACHE_CONTROL = "Cache-Control";

  /** The Constant CONNECTION. */
  public final static String CONNECTION = "Connection";

  /** The Constant CONTENT_LENGTH. */
  public final static String CONTENT_LENGTH = "Content-Length";

  /** The Constant CONTENT_TYPE. */
  public final static String CONTENT_TYPE = "Content-Type";

  /** The Constant COOKIE. */
  public final static String COOKIE = "Cookie";

  /** The Constant DATE. */
  public final static String DATE = "Date";

  /** The Constant EXPECT. */
  public final static String EXPECT = "Expect";

  /** The Constant FROM. */
  public final static String FROM = "From";

  /** The Constant HOST. */
  public final static String HOST = "Host";

  /** The Constant IF_MATCH. */
  public final static String IF_MATCH = "If-Match";

  /** The Constant IF_MODIFIED_SINCE. */
  public final static String IF_MODIFIED_SINCE = "If-Modified-Since";

  /** The Constant IF_NONE_MATCH. */
  public final static String IF_NONE_MATCH = "If-None-Match";

  /** The Constant IF_RANGE. */
  public final static String IF_RANGE = "If-Range";

  /** The Constant IF_UNMODIFIED_SINCE. */
  public final static String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";

  /** The Constant KEEP_ALIVE. */
  public final static String KEEP_ALIVE = "Keep-Alive";

  /** The Constant MAX_FORWARDS. */
  public final static String MAX_FORWARDS = "Max-Forwards";

  /** The Constant PRAGMA. */
  public final static String PRAGMA = "Pragma";

  /** The Constant PROXY_AUTHORIZATION. */
  public final static String PROXY_AUTHORIZATION = "Proxy-Authorization";

  /** The Constant PROXY_CONNECTION. */
  public final static String PROXY_CONNECTION = "Proxy-Connection";

  /** The Constant RANGE. */
  public final static String RANGE = "Range";

  /** The Constant REFERER. */
  public final static String REFERER = "Referer";

  /** The Constant TE. */
  public final static String TE = "TE";

  /** The Constant UPGRADE. */
  public final static String UPGRADE = "Upgrade";

  /** The Constant USER_AGENT. */
  public final static String USER_AGENT = "User-Agent";

  /** The Constant VIA. */
  public final static String VIA = "Via";

  /** The Constant WARNING. */
  public final static String WARNING = "Warning";

  /** The Constant TRAILER. */
  public final static String TRAILER = "Trailer";

  /** The Constant TRANSFER_ENCODING. */
  public final static String TRANSFER_ENCODING = "Transfer-Encoding";

  /** The Constant X_FORWARDED_FOR. */
  public final static String X_FORWARDED_FOR = "X-Forwarded-For";

  public final static String SWAP_UUID = "SWAP_UUID";

  /** The headers that must be removed when forwarding a request. */
  public final static List<String> forwardRequestFilter = Arrays.asList(
      RequestHeaderConstants.ACCEPT_ENCODING, RequestHeaderConstants.CACHE_CONTROL,
      RequestHeaderConstants.CONNECTION, RequestHeaderConstants.PROXY_CONNECTION,
      RequestHeaderConstants.KEEP_ALIVE, RequestHeaderConstants.TRANSFER_ENCODING,
      RequestHeaderConstants.TE, RequestHeaderConstants.TRAILER, RequestHeaderConstants.UPGRADE,
      RequestHeaderConstants.IF_MATCH, RequestHeaderConstants.IF_MODIFIED_SINCE,
      RequestHeaderConstants.IF_NONE_MATCH, RequestHeaderConstants.IF_RANGE,
      RequestHeaderConstants.IF_UNMODIFIED_SINCE, RequestHeaderConstants.SWAP_UUID);

}
