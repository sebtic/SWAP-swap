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
 * <http://www.gnu.org/licenses/>. $Id: ProxyServlet.java 125 2012-04-04
 * 14:59:59Z sebtic $
 */
package org.projectsforge.swap.proxy.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.PreDestroy;
import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpHeaders;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.io.EofException;
import org.eclipse.jetty.util.IO;
import org.projectsforge.swap.core.environment.Environment;
import org.projectsforge.swap.core.handlers.HandlerContext;
import org.projectsforge.swap.core.handlers.HandlerExecutor;
import org.projectsforge.swap.core.handlers.HandlerFilter;
import org.projectsforge.swap.core.handlers.Resource;
import org.projectsforge.swap.core.http.CacheManager;
import org.projectsforge.swap.core.http.Mime;
import org.projectsforge.swap.core.http.Request;
import org.projectsforge.swap.core.http.Response;
import org.projectsforge.swap.core.http.Util;
import org.projectsforge.swap.handlers.mime.MimeHandler;
import org.projectsforge.swap.handlers.mime.RequestFilter;
import org.projectsforge.swap.handlers.mime.ResponseFilter;
import org.projectsforge.swap.handlers.mime.StatisticsCollector;
import org.projectsforge.swap.handlers.mime.StatisticsCollectorInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * A {@link HttpServlet} implementing the proxy.
 * 
 * @author Sébastien Aupetit
 */
public class ProxyServlet extends GenericServlet {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 0L;

  /** The logger. */
  private final Logger logger = LoggerFactory.getLogger(ProxyServlet.class);

  /**
   * The default port to connect to if it is unspecified in the connect request.
   */
  private int defaultPort = 80;

  /** The cache manager. */
  @Autowired
  @Qualifier("http.cacheManager")
  private CacheManager cacheManager;

  /** The environment. */
  @Autowired
  private Environment environment;

  @Autowired(required = false)
  private StatisticsCollectorInterceptor[] statisticsCollectorInterceptors;

  /*
   * (non-Javadoc)
   * @see javax.servlet.Servlet#destroy()
   */
  @Override
  public void destroy() {

  }

  /**
   * Do connect.
   * 
   * @param httpServletRequest the http servlet request
   * @param httpServletResponse the http servlet response
   * @throws IOException Signals that an I/O exception has occurred.
   */
  private void doConnect(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
      throws IOException {
    final HttpURI uri = new HttpURI(httpServletRequest.getRequestURI());

    final InputStream in = httpServletRequest.getInputStream();
    final OutputStream out = httpServletResponse.getOutputStream();

    try (final Socket socket = new Socket(uri.getHost(), uri.getPort() == -1 ? defaultPort : uri.getPort())) {
      httpServletResponse.setStatus(HttpServletResponse.SC_OK);
      httpServletResponse.setHeader(HttpHeaders.CONNECTION, "close");
      httpServletResponse.flushBuffer();

      IO.copyThread(socket.getInputStream(), out);
      IO.copy(in, socket.getOutputStream());
    }
  }

  /**
   * Do proxying.
   * 
   * @param httpServletRequest the http servlet request
   * @param httpServletResponse the http servlet response
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @SuppressWarnings("deprecation")
  private void doProxying(final HttpServletRequest httpServletRequest, final HttpServletResponse httpServletResponse)
      throws IOException {
    final StatisticsCollector statisticsCollector = new StatisticsCollector();
    statisticsCollector.startTimer(StatisticsCollector.PROXY_ALL_TIMER_KEY);

    Request request = cacheManager.newServletRequest(httpServletRequest);
    statisticsCollector.setValue(StatisticsCollector.PROXY_ORIGINALREQUEST_VALUE_KEY, request);
    final String url = request.toString();
    final Logger contextLogger = LoggerFactory.getLogger(HandlerContext.class.getSimpleName() + "." + url);
    try {
      // Filter request
      request = doRequestFiltering(statisticsCollector, contextLogger, request);
      if (request == null) {
        statisticsCollector.setValue(StatisticsCollector.PROXY_REQUESTFILTERED_VALUE_KEY, Boolean.TRUE);
        httpServletResponse.sendError(404, "Blocked request");
      } else {
        statisticsCollector.setValue(StatisticsCollector.PROXY_REQUESTFILTERED_VALUE_KEY, Boolean.FALSE);
        logger.info("Serving {}", request);

        statisticsCollector.startTimer(StatisticsCollector.PROXY_GETRESPONSE_TIMER_KEY);
        final Response response = request.doRequest(httpServletResponse);
        statisticsCollector.stopTimer(StatisticsCollector.PROXY_GETRESPONSE_TIMER_KEY);
        statisticsCollector.setValue(StatisticsCollector.PROXY_ORIGINALRESPONSE_VALUE_KEY, response);
        final String responseMime = response.getMime();

        Response result = response;

        // filter response
        if (doProxyingResponseFiltering(statisticsCollector, contextLogger, response, responseMime)) {
          statisticsCollector.setValue(StatisticsCollector.PROXY_RESPONSEFILTERED_VALUE_KEY, Boolean.FALSE);
          if (!response.isDirect() && response.getContent() != null) {
            // something to do, response is stored and status code is 200
            result = doProxyingMimeHandle(statisticsCollector, contextLogger, response, responseMime);
          }
        } else {
          statisticsCollector.setValue(StatisticsCollector.PROXY_RESPONSEFILTERED_VALUE_KEY, Boolean.TRUE);
        }

        if (!response.isDirect()) {
          // send result
          httpServletResponse.setStatus(result.getStatusCode(), result.getStatusReason());
          // send headers
          Util.sendResponseHeaders(httpServletResponse, result.getHeaders());
          if (result.getContent() != null) {
            try (final InputStream in = result.getContent().getInputStream()) {
              try (final ServletOutputStream out = httpServletResponse.getOutputStream()) {
                Util.dumpTo(in, out);
              }
            } catch (final IOException e) {
              logger.debug("An exception occurred", e);
            }
          }
        }
      }
      httpServletResponse.flushBuffer();
    } finally {
      statisticsCollector.stopTimer(StatisticsCollector.PROXY_ALL_TIMER_KEY);
    }
    logger.info("Service done in {}ms for {}",
        statisticsCollector.getElapsedTimer(StatisticsCollector.PROXY_ALL_TIMER_KEY), url);
    if (statisticsCollectorInterceptors != null) {
      for (final StatisticsCollectorInterceptor interceptor : statisticsCollectorInterceptors) {
        interceptor.intercept(statisticsCollector);
      }
    }
  }

  private Response doProxyingMimeHandle(final StatisticsCollector statisticsCollector, final Logger contextLogger,
      final Response response, final String responseMime) {
    statisticsCollector.startTimer(StatisticsCollector.PROXY_MIMEHANDLER_ALL_TIMER_KEY);

    try {
      final Resource<Response> responseResource = new Resource<Response>(MimeHandler.OUTPUT_RESPONSE);

      try (final HandlerContext<MimeHandler> mimeHandlerContext = environment
          .autowireBean(new HandlerContext<MimeHandler>(contextLogger, MimeHandler.class,
              new HandlerExecutor<MimeHandler>() {
                @Override
                public void execute(final HandlerContext<MimeHandler> context, final Class<?> handlerClass,
                    final MimeHandler handler) throws Exception {
                  statisticsCollector.startTimer(StatisticsCollector.PROXY_MIMEHANDLER_DO_TIMER_KEY
                      + handler.getClass().getCanonicalName());
                  try {
                    contextLogger.debug("Handling response with {}", handlerClass.getName());
                    handler.handle(context, statisticsCollector, response);
                  } finally {
                    statisticsCollector.stopTimer(StatisticsCollector.PROXY_MIMEHANDLER_DO_TIMER_KEY
                        + handler.getClass().getCanonicalName());
                  }
                }

                @Override
                public void shutdown(final HandlerContext<MimeHandler> context, final Class<?> handlerClass,
                    final MimeHandler handler) throws Exception {
                  for (final Method method : handler.getClass().getMethods()) {
                    if (method.getAnnotation(PreDestroy.class) != null && method.getParameterTypes().length == 0) {
                      try {
                        method.invoke(handler, new Object[] {});
                      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        logger.warn("An error occurred while calling " + method, e);
                      }
                    }
                  }
                }
              }, new HandlerFilter() {
                @Override
                public boolean accept(final Class<?> handlerClass) {
                  final Mime mime = handlerClass.getAnnotation(Mime.class);
                  if (mime != null) {
                    for (final String m : mime.mime()) {
                      if ("*".equals(m) || responseMime.equalsIgnoreCase(m)) {
                        return true;
                      }
                    }
                  } else {
                    contextLogger.error("@Mime annotation is missing for handler class {}", handlerClass.getName());
                  }
                  return false;
                }

              }))) {
        mimeHandlerContext.addResource(responseResource);

        try {
          if (mimeHandlerContext.execute() && responseResource.get() != null) {
            // send modified response
            statisticsCollector.setValue(StatisticsCollector.PROXY_EFFECTIVERESPONSE_VALUE_KEY, responseResource.get());
            return responseResource.get();

          }
        } catch (final InterruptedException e) {
          // nothing to do
        }

        // send the original response if we did not already return
        statisticsCollector.setValue(StatisticsCollector.PROXY_EFFECTIVERESPONSE_VALUE_KEY, response);
        return response;
      }
    } finally {
      statisticsCollector.stopTimer(StatisticsCollector.PROXY_MIMEHANDLER_ALL_TIMER_KEY);
    }
  }

  private boolean doProxyingResponseFiltering(final StatisticsCollector statisticsCollector,
      final Logger contextLogger, final Response response, final String responseMime) {
    statisticsCollector.startTimer(StatisticsCollector.PROXY_RESPONSEFILTERING_ALL_TIMER_KEY);
    try {
      final AtomicBoolean continueHandling = new AtomicBoolean(true);

      try (final HandlerContext<ResponseFilter> preHandlerContext = environment
          .autowireBean(new HandlerContext<ResponseFilter>(contextLogger, ResponseFilter.class,
              new HandlerExecutor<ResponseFilter>() {
                @Override
                public void execute(final HandlerContext<ResponseFilter> context, final Class<?> handlerClass,
                    final ResponseFilter handler) throws Exception {
                  statisticsCollector.startTimer(StatisticsCollector.PROXY_RESPONSEFILTERING_DO_TIMER_KEY
                      + handler.getClass().getCanonicalName());
                  try {
                    contextLogger.debug("Filtering response with {}", handlerClass.getName());
                    if (!handler.filter(context, statisticsCollector, response)) {
                      continueHandling.set(false);
                    }
                  } finally {
                    statisticsCollector.stopTimer(StatisticsCollector.PROXY_RESPONSEFILTERING_DO_TIMER_KEY
                        + handler.getClass().getCanonicalName());
                  }
                }

                @Override
                public void shutdown(final HandlerContext<ResponseFilter> context, final Class<?> handlerClass,
                    final ResponseFilter handler) throws Exception {
                  for (final Method method : handler.getClass().getMethods()) {
                    if (method.getAnnotation(PreDestroy.class) != null && method.getParameterTypes().length == 0) {
                      try {
                        method.invoke(handler, new Object[] {});
                      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        logger.warn("An error occurred while calling " + method, e);
                      }
                    }
                  }
                }
              }, new HandlerFilter() {
                @Override
                public boolean accept(final Class<?> handlerClass) {
                  final Mime mime = handlerClass.getAnnotation(Mime.class);
                  if (mime != null) {
                    for (final String m : mime.mime()) {
                      if ("*".equals(m) || responseMime.equalsIgnoreCase(m)) {
                        return true;
                      }
                    }
                  } else {
                    contextLogger.error("@Mime annotation is missing for handler class {}", handlerClass.getName());
                  }
                  return false;
                }

              }))) {
        return preHandlerContext.execute() && continueHandling.get();
      }
    } finally {
      statisticsCollector.stopTimer(StatisticsCollector.PROXY_RESPONSEFILTERING_ALL_TIMER_KEY);
    }
  }

  private Request doRequestFiltering(final StatisticsCollector statisticsCollector, final Logger contextLogger,
      final Request request) {
    statisticsCollector.startTimer(StatisticsCollector.PROXY_REQUESTFILTERING_ALL_TIMER_KEY);

    try {
      final Resource<Request> requestResource = new Resource<Request>(RequestFilter.OUTPUT_REQUEST);

      final AtomicBoolean continueHandling = new AtomicBoolean(true);
      try (final HandlerContext<RequestFilter> handlerContext = environment
          .autowireBean(new HandlerContext<RequestFilter>(contextLogger, RequestFilter.class,
              new HandlerExecutor<RequestFilter>() {
                @Override
                public void execute(final HandlerContext<RequestFilter> context, final Class<?> handlerClass,
                    final RequestFilter handler) throws Exception {
                  statisticsCollector.startTimer(StatisticsCollector.PROXY_REQUESTFILTERING_DO_TIMER_KEY
                      + handler.getClass().getCanonicalName());
                  try {
                    contextLogger.debug("Filtering request with {}", handlerClass.getName());
                    if (!handler.filter(context, statisticsCollector, request)) {
                      continueHandling.set(false);
                    }
                  } finally {
                    statisticsCollector.stopTimer(StatisticsCollector.PROXY_REQUESTFILTERING_DO_TIMER_KEY
                        + handler.getClass().getCanonicalName());
                  }
                }

                @Override
                public void shutdown(final HandlerContext<RequestFilter> context, final Class<?> handlerClass,
                    final RequestFilter handler) throws Exception {
                  for (final Method method : handler.getClass().getMethods()) {
                    if (method.getAnnotation(PreDestroy.class) != null && method.getParameterTypes().length == 0) {
                      try {
                        method.invoke(handler, new Object[] {});
                      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        logger.warn("An error occurred while calling " + method, e);
                      }
                    }
                  }
                }
              }, new HandlerFilter() {
                @Override
                public boolean accept(final Class<?> handlerClass) {
                  return true;
                }

              }))) {
        handlerContext.addResource(requestResource);

        Request result = request;

        if (handlerContext.execute()) {
          if (continueHandling.get()) {
            try {
              if (requestResource.get() != null) {

                result = requestResource.get();
              }
            } catch (final InterruptedException e) {
              // we keep original request
            }
          } else {
            result = null;
          }
        }

        statisticsCollector.setValue(StatisticsCollector.PROXY_EFFECTIVEREQUEST_VALUE_KEY, result);

        return result;
      }
    } finally {
      statisticsCollector.stopTimer(StatisticsCollector.PROXY_REQUESTFILTERING_ALL_TIMER_KEY);
    }
  }

  /**
   * Gets the default port.
   * 
   * @return the default port
   */
  public int getDefaultPort() {
    return defaultPort;
  }

  /*
   * (non-Javadoc)
   * @see javax.servlet.Servlet#getServletInfo()
   */
  @Override
  public String getServletInfo() {
    return "Smart Web Accessibility Proxy Servlet";
  }

  /*
   * (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#service(javax.servlet.ServletRequest,
   * javax.servlet.ServletResponse)
   */
  @Override
  public void service(final ServletRequest servletRequest, final ServletResponse servletResponse)
      throws ServletException, IOException {

    try {

      final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
      final HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

      if ("CONNECT".equalsIgnoreCase(httpServletRequest.getMethod())) {
        doConnect(httpServletRequest, httpServletResponse);
      } else if ("HEAD".equalsIgnoreCase(httpServletRequest.getMethod())) {
        if (httpServletRequest.getProtocol().endsWith("1.1")) {
          httpServletResponse.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        } else {
          httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
      } else if ("GET".equalsIgnoreCase(httpServletRequest.getMethod())) {
        doProxying(httpServletRequest, httpServletResponse);
      } else if ("POST".equalsIgnoreCase(httpServletRequest.getMethod())) {
        doProxying(httpServletRequest, httpServletResponse);
      } else if ("PUT".equalsIgnoreCase(httpServletRequest.getMethod())) {
        doProxying(httpServletRequest, httpServletResponse);
      } else if ("DELETE".equalsIgnoreCase(httpServletRequest.getMethod())) {
        doProxying(httpServletRequest, httpServletResponse);
      } else {
        if (httpServletRequest.getProtocol().endsWith("1.1")) {
          httpServletResponse.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        } else {
          httpServletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
      }
    } catch (final RuntimeException e) {
      logger.warn("An exception occurred", e);
      throw e;
    } catch (final EofException e) {
      logger.debug("EOF exception", e);
    } catch (final IOException e) {
      logger.warn("An exception occurred", e);
      throw e;
    }
    // httpServletResponse.flushBuffer();
  }

  /**
   * Sets the default port.
   * 
   * @param defaultPort the new default port
   */
  public void setDefaultPort(final int defaultPort) {
    this.defaultPort = defaultPort;
  }
}
