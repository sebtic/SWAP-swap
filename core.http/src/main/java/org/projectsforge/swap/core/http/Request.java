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
 * <http://www.gnu.org/licenses/>. $Id: Request.java 125 2012-04-04 14:59:59Z
 * sebtic $
 */
package org.projectsforge.swap.core.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.projectsforge.swap.core.environment.Environment;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The class that hide request specific things.
 * 
 * @author Sébastien Aupetit
 */
public class Request {

  /** The environment. */
  @Autowired
  private Environment environment;

  /** The cache manager. */
  @Autowired
  private CacheManager cacheManager;

  /** The method. */
  private String method = HttpMethods.GET;

  /** The protocol version. */
  private String protocolVersion = HttpProtocolVersions.HTTP_1_1;

  /** The URL. */
  private URL url = null;

  /** The headers. */
  private Headers headers;

  /** The request input stream. */
  private InputStream requestInputStream;

  /** Indicate if the content can be cached. */
  private boolean cacheable = false;

  /** Indicate if a reload is required. */
  private boolean reloadRequested = true;

  /** The logger. */
  private final Logger logger;

  /**
   * The constructor.
   * 
   * @param request the request
   * @throws IOException Signals that an I/O exception has occurred.
   */
  Request(final HttpServletRequest request) throws IOException {

    // Copying other attributes
    this.method = request.getMethod();
    this.url = Util.getURL(request);
    this.protocolVersion = request.getProtocol();

    logger = org.slf4j.LoggerFactory.getLogger(getClass().getName() + "." + getURL());

    // Copying headers
    final Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      final String name = headerNames.nextElement();
      final Enumeration<String> values = request.getHeaders(name);
      while (values.hasMoreElements()) {
        final String value = values.nextElement();
        getHeaders().add(name, value);
      }
    }

    getHeaders().add(RequestHeaderConstants.X_FORWARDED_FOR, request.getRemoteAddr());

    requestInputStream = request.getInputStream();
  }

  /**
   * Instantiates a new basic request based on another request. Among other,
   * authentification and cookies are added to the new request.
   * 
   * @param context the context request
   * @param url the uri
   * @throws MalformedURLException the malformed url exception
   */
  Request(final Request context, final String url) throws MalformedURLException {
    logger = org.slf4j.LoggerFactory.getLogger(getClass().getName() + "." + url);

    // copy headers
    getHeaders().copy(context.getHeaders());

    this.url = new URL(context.getURL(), url);
    getHeaders().set(RequestHeaderConstants.HOST, this.url.getHost());
    getHeaders().set(RequestHeaderConstants.REFERER, context.getURL().toExternalForm());
  }

  /**
   * Instantiates a new basic request.
   * 
   * @param url the url
   */
  Request(final URL url) {
    logger = org.slf4j.LoggerFactory.getLogger(getClass().getName() + "." + url);
    this.url = url;
    getHeaders().set(RequestHeaderConstants.HOST, url.getHost());
  }

  /**
   * Do request.
   * 
   * @param allowRedirect the allow redirect
   * @return the response
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws CircularRedirectionException the circular redirection exception
   */
  public synchronized Response doRequest(final boolean allowRedirect) throws IOException, CircularRedirectionException {
    List<URL> redirects = null;

    Response lastResponse = cacheManager.getResponse(this);
    lastResponse.refresh(null);

    while (lastResponse.isRedirected()) {
      if (redirects == null) {
        redirects = new ArrayList<>();
      }

      redirects.add(lastResponse.getRequest().getURL());

      final Request redirectRequest = lastResponse.getRedirectedRequest();
      if (redirects.contains(redirectRequest.getURL())) {
        // circular redirect
        throw new CircularRedirectionException("Circular redirection detected: " + redirects);
      }

      logger.info("Redirecting from {} to {}", lastResponse.getRedirectedRequest(), redirectRequest);
      lastResponse = cacheManager.getResponse(redirectRequest);
      lastResponse.refresh(null);
    }
    return lastResponse;
  }

  /**
   * Do request.
   * 
   * @param httpServletResponse the http servlet response
   * @return the response
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public synchronized Response doRequest(final HttpServletResponse httpServletResponse) throws IOException {
    final Response response = cacheManager.getResponse(this);
    response.refresh(httpServletResponse);
    return response;
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
    if (obj instanceof Request) {
      final Request other = (Request) obj;

      if (!getHeaders().equals(other.getHeaders())) {
        return false;
      }
      if (!method.equals(other.method)) {
        return false;
      }
      if (!protocolVersion.equals(other.protocolVersion)) {
        return false;
      }
      if (!url.equals(other.url)) {
        return false;
      }
      return true;
    }
    return false;
  }

  /**
   * Gets the headers.
   * 
   * @return the headers
   */
  public Headers getHeaders() {
    if (headers == null) {
      headers = new Headers();
    }
    return headers;
  }

  /**
   * Gets the logger.
   * 
   * @return the logger
   */
  public Logger getLogger() {
    return logger;
  }

  /**
   * Gets the method.
   * 
   * @return the method
   */
  public String getMethod() {
    return method;
  }

  /**
   * Gets the protocol version.
   * 
   * @return the protocol version
   */
  public String getProtocolVersion() {
    return protocolVersion;
  }

  /**
   * Gets the request input stream.
   * 
   * @return the request input stream
   */
  public InputStream getRequestInputStream() {
    return requestInputStream;
  }

  /**
   * Gets the request URL.
   * 
   * @return the URL
   */
  public URL getURL() {
    return url;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + getHeaders().hashCode();
    result = prime * result + method.hashCode();
    result = prime * result + protocolVersion.hashCode();
    result = prime * result + url.hashCode();
    return result;
  }

  /**
   * Inits the.
   */
  @PostConstruct
  void init() {
    // Set Via and X-Forwarded-For headers

    final String via = "1.1 " + HttpPropertyHolder.agentName.get();
    boolean found = false;

    for (final Header header : getHeaders().getHeaders(RequestHeaderConstants.VIA)) {
      if (via.equals(header.getValue())) {
        found = true;
      }
    }

    if (!found) {
      getHeaders().add(RequestHeaderConstants.VIA, via);
    }
    updateProperties();
  }

  /**
   * Test if the request can be stored in a cache.
   * 
   * @return true if cacheable, false otherwise.
   */
  public boolean isCacheable() {
    return cacheable;
  }

  /**
   * Test if a reload is required.
   * 
   * @return true if a reload is required, false otherwise
   */
  public boolean isReloadRequested() {
    return reloadRequested;
  }

  /**
   * Sets the cacheable.
   * 
   * @param cacheable the new cacheable
   */
  public void setCacheable(final boolean cacheable) {
    this.cacheable = cacheable;
  }

  /**
   * Sets the headers.
   * 
   * @param headers the new headers
   */
  public void setHeaders(final Headers headers) {
    this.getHeaders().copy(headers);
  }

  /**
   * Sets the method.
   * 
   * @param method the new method
   */
  public void setMethod(final String method) {
    this.method = method;
  }

  /**
   * Sets the protocol version.
   * 
   * @param protocolVersion the new protocol version
   */
  public void setProtocolVersion(final String protocolVersion) {
    this.protocolVersion = protocolVersion;
  }

  /**
   * Sets the reload requested.
   * 
   * @param reloadRequested the reload requested
   */
  public void setReloadRequested(final boolean reloadRequested) {
    this.reloadRequested = reloadRequested;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getURL().toExternalForm();
  }

  /**
   * Update the cached properties of the request.
   */
  protected void updateProperties() {
    setReloadRequested(false);
    setCacheable(true);

    if (!"GET".equals(method)) {
      setCacheable(false);
    }

    if (getHeaders().hasHeader(RequestHeaderConstants.PRAGMA)) {
      setReloadRequested(true);
    }

    if (getHeaders().hasHeader(RequestHeaderConstants.CACHE_CONTROL)) {
      for (final Header header : getHeaders().getHeaders(RequestHeaderConstants.CACHE_CONTROL)) {
        if ("max-age=0".equals(header.getValue()) || "no-cache".equals(header.getValue())) {
          setReloadRequested(true);
        }
      }
    }

    if (getHeaders().hasHeader(RequestHeaderConstants.AUTHORIZATION)) {
      setCacheable(false);
    }
  }
}
