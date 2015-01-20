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
 * <http://www.gnu.org/licenses/>. $Id$
 */
package org.projectsforge.swap.core.http;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.channels.UnresolvedAddressException;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.projectsforge.swap.core.environment.Environment;
import org.projectsforge.utils.temporarystreams.ContentHolder;
import org.projectsforge.utils.temporarystreams.TemporaryContentHolder;
import org.projectsforge.utils.temporarystreams.TemporaryStreamsFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Class Response.
 * 
 * @author Sébastien Aupetit
 */
public class Response {

  /** The environment. */
  @Autowired
  private Environment environment;

  /** The headers. */
  private Headers headers = new Headers();

  /** The request. */
  private final Request request;

  /** The mime type. */
  private String mime = "text/html";

  /** The status code (default to 504 GATEWAY time out). */
  private int statusCode = 504;

  /** The status reason. */
  private String statusReason;

  /** The already executed. */
  private boolean alreadyExecuted = false;

  /** The logger. */
  private final Logger logger;

  /** The date at which the last response has be obtained. */
  private long date = 0;

  /** The expiration date of the response. */
  private long expires = -1;

  /** The direct. */
  private boolean direct = false;

  /** The content. */
  private ContentHolder content;

  /** The cache manager. */
  @Autowired
  private CacheManager cacheManager;

  /** The temporary streams factory. */
  @Autowired
  private TemporaryStreamsFactory temporaryStreamsFactory;

  /** The http client. */
  @Autowired
  private ServletHttpClient httpClient;

  /**
   * Instantiates a new response.
   * 
   * @param request the request
   */
  Response(final Request request) {
    this.request = request;
    logger = org.slf4j.LoggerFactory.getLogger(getClass().getName() + "." + request.getURL());
  }

  /**
   * Instantiates a new basic response from another response (Useful headers are
   * kept).
   * 
   * @param context the context response
   */
  public Response(final Response context) {
    this(context.request);
    headers.copy(context.headers);
    statusCode = context.getStatusCode();
    statusReason = context.getStatusReason();
  }

  /**
   * Gets the content.
   * 
   * @return the content
   */
  public ContentHolder getContent() {
    return content;
  }

  /**
   * Gets the headers.
   * 
   * @return the headers
   */
  public Headers getHeaders() {
    return headers;
  }

  /**
   * Gets the mime.
   * 
   * @return the mime
   */
  public String getMime() {
    return mime;
  }

  /**
   * Gets the redirected request.
   * 
   * @return the redirected request
   */
  public Request getRedirectedRequest() {
    if (!isRedirected()) {
      throw new IllegalArgumentException("Request must be a redirect request");
    }
    return cacheManager
        .newContextualRequest(getRequest(), getHeaders().getFirstValue(ResponseHeaderConstants.LOCATION));
  }

  /**
   * Gets the request.
   * 
   * @return the request
   */
  public Request getRequest() {
    return request;
  }

  /**
   * Gets the status code.
   * 
   * @return the status code
   */
  public int getStatusCode() {
    return statusCode;
  }

  /**
   * Gets the status reason.
   * 
   * @return the status reason
   */
  public String getStatusReason() {
    return statusReason;
  }

  /**
   * Checks if is direct.
   * 
   * @return true, if is direct
   */
  public boolean isDirect() {
    return direct;
  }

  /**
   * Checks if is redirected.
   * 
   * @return true, if is redirected
   */
  public boolean isRedirected() {
    if ("GET".equals(getRequest().getMethod())) {
      switch (statusCode) {
        case HttpStatus.SC_MOVED_TEMPORARILY:
          return getHeaders().hasHeader(ResponseHeaderConstants.LOCATION);
        case HttpStatus.SC_MOVED_PERMANENTLY:
        case HttpStatus.SC_TEMPORARY_REDIRECT:
        case HttpStatus.SC_SEE_OTHER:
          return true;
        default:
          return false;
      }
    } else {
      return false;
    }
  }

  /**
   * Test if a response need to be refreshed.
   * 
   * @return true, if the response need to be refreshed
   */
  private boolean needRefresh() {

    if (!alreadyExecuted) {
      logger.trace("needRefresh because !alreadyExecuted");
      return true;
    } else {
      final long now = System.currentTimeMillis();
      final long age = now - date;

      if (request.isReloadRequested()) {
        logger.trace("needRefresh because reload requested");
        return true;
      }

      if (age > CacheManagerPropertyHolder.maxAgeBeforeForcedRefresh.get()) {
        logger.trace("needRefresh because maxage before forced refresh");
        return true;
      }

      if (expires != -1) {
        if (expires <= now) {
          if (age >= CacheManagerPropertyHolder.maxAgeForForcedExpiration.get()) {
            logger.trace("needRefresh because maxage for forced expiration");
            return true;
          }
        } else {
          return true;
        }
      }
      return false;
    }
  }

  /**
   * Refresh.
   * 
   * @param httpServletResponse the http servlet response
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @SuppressWarnings({ "deprecation", "resource" })
  void refresh(final HttpServletResponse httpServletResponse) throws IOException {
    if (needRefresh()) {
      logger.debug("Refreshing {}", getRequest().getURL());

      try {
        HttpUriRequest httpRequest;
        // 1. HTTP request
        final String url = getRequest().getURL().toExternalForm();
        final String method = getRequest().getMethod();
        // TODO : handle getRequest().getProtocolVersion() ???
        switch (method) {
          case "GET":
            httpRequest = new HttpGet(url);
            break;
          case "POST":
            httpRequest = new HttpPost(url);
            break;
          case "PUT":
            httpRequest = new HttpPut(url);
            break;
          case "DELETE":
            httpRequest = new HttpDelete(url);
            break;
          default:
            throw new IllegalArgumentException("HTTP method not impplemented");
        }

        // 2. HTTP headers
        for (final org.projectsforge.swap.core.http.Header header : getRequest().getHeaders()) {
          httpRequest.setHeader(header.getName(), header.getValue());
        }

        // 3. HTTP accepted encoding
        // TODO test without changing encoding
        httpRequest.setHeader(HttpHeaders.ACCEPT_ENCODING, "identity");

        // 4. HTTP request entity
        final InputStream requestInputStream = getRequest().getRequestInputStream();
        if (requestInputStream != null) {
          if (httpRequest instanceof HttpEntityEnclosingRequest) {
            ((HttpEntityEnclosingRequest) httpRequest).setEntity(new InputStreamEntity(requestInputStream, -1));
          }
        }

        // 5. Do the connection
        try {
          final HttpResponse httpResponse = httpClient.execute(httpRequest);

          // 8. Store status line and response header
          statusCode = httpResponse.getStatusLine().getStatusCode();
          statusReason = httpResponse.getStatusLine().getReasonPhrase();
          headers.clear();
          for (final org.apache.http.Header header : httpResponse.getAllHeaders()) {
            getHeaders().add(header.getName(), header.getValue());
          }

          // 9. Compute MIME of the response entity if there is one
          final org.apache.http.Header contentType = httpResponse.getFirstHeader(RequestHeaderConstants.CONTENT_TYPE
              .toString());
          if (contentType != null) {
            final int index = contentType.getValue().indexOf(';');
            String mimeId;
            if (index > 0) {
              mimeId = contentType.getValue().substring(0, index);
            } else {
              mimeId = contentType.getValue();
            }
            if (mimeId.isEmpty()) {
              mime = null;
            } else {
              mime = mimeId;
            }

          } else {
            mime = null;
          }

          // 10. Detect if the response entity must be stored using MIME and
          // cache
          // manager
          if (mime == null) {
            logger.debug("Content MIME for {} can not be determined", getRequest().getURL());
            direct = true;
          } else {
            logger.debug("Content MIME for {} is {}", getRequest().getURL(), mime);
            if (httpServletResponse == null) {
              direct = false;
            } else if (cacheManager.isMimeHandled(mime) && statusCode == 200) {
              direct = false;
            } else {
              direct = true;
            }
          }

          // 11. Process response entity
          OutputStream outputStream;
          if (direct) {
            httpServletResponse.setStatus(statusCode, statusReason);
            Util.sendResponseHeaders(httpServletResponse, getHeaders());
            outputStream = httpServletResponse.getOutputStream();
            content = null;
          } else {
            content = new TemporaryContentHolder(temporaryStreamsFactory);
            outputStream = ((TemporaryContentHolder) content).getOutputStream();
          }

          // conn.receiveResponseEntity(httpResponse);
          final HttpEntity responseEntity = httpResponse.getEntity();
          if (responseEntity != null) {
            try (final InputStream in = responseEntity.getContent()) {
              Util.dumpTo(in, outputStream);
            } catch (final EOFException e) {
              logger.debug("An exception occurred while sending response", e);
            }
          }
          if (!direct || responseEntity == null) {
            outputStream.close();
          }
        } catch (final RuntimeException e) {
          httpRequest.abort();
        }

        logger.info("Refreshed with response status code: {} (MIME: {})", statusCode, mime);

        // 12. Update some properties
        alreadyExecuted = true;
        final Header header = headers.getFirst(ResponseHeaderConstants.EXPIRES);
        if (header != null) {
          expires = header.getValueAsDateField();
        } else {
          expires = 0;
        }
        date = System.currentTimeMillis();

      } catch (final UnresolvedAddressException | SocketTimeoutException | UnknownHostException e) {
        if (direct) {
          httpServletResponse.setStatus(HttpServletResponse.SC_GATEWAY_TIMEOUT);
        }
        setStatusCode(HttpServletResponse.SC_GATEWAY_TIMEOUT);
        logger.debug("refresh failed with timeout", e);
      } catch (final IOException e) {
        if (direct) {
          httpServletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        logger.error("refresh failed", e);
      }
    } else {
      logger.info("Using cached version");
    }
  }

  /**
   * Sets the content.
   * 
   * @param content the new content
   */
  public void setContent(final ContentHolder content) {
    if (this.content != null && this.content != content) {
      this.content.release();
    }
    this.content = content;
  }

  /**
   * Sets the headers.
   * 
   * @param headers the new headers
   */
  public void setHeaders(final Headers headers) {
    this.headers = headers;
  }

  /**
   * Sets the mime type.
   * 
   * @param mime the new mime
   */
  public void setMime(final String mime) {
    this.mime = mime;
  }

  /**
   * Sets the status code.
   * 
   * @param statusCode the new status code
   */
  public void setStatusCode(final int statusCode) {
    this.statusCode = statusCode;
  }

  /**
   * Sets the status reason.
   * 
   * @param statusReason the new status reason
   */
  public void setStatusReason(final String statusReason) {
    this.statusReason = statusReason;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return String.format("Response [request=%s, mime=%s, statusCode=%s, statusReason=%s, headers=%s]", request, mime,
        statusCode, statusReason, headers);
  }
}
