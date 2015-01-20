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
package org.projectsforge.swap.server.starter;

import java.io.FileNotFoundException;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.projectsforge.swap.core.embeddedservlet3jetty.EmbeddedServlet3JettyFactory;
import org.projectsforge.swap.core.environment.Environment;
import org.projectsforge.swap.core.persistence.H2ConsolePropertyHolder;
import org.projectsforge.swap.server.remoting.RemotingInitializer;
import org.projectsforge.utils.annotations.AnnotationScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The Class ServerConfiguration.
 * 
 * @author Sébastien Aupetit
 */
@Configuration
public class ServerConfiguration {

  /** The environment. */
  @Autowired
  Environment environment;

  /** The embedded servlet3 jetty factory. */
  @Autowired
  EmbeddedServlet3JettyFactory embeddedServlet3JettyFactory;

  /** The annotation scanner. */
  @Autowired
  private AnnotationScanner annotationScanner;

  /**
   * Gets the http connection.
   * 
   * @return the http connection
   */
  private SocketConnector getHttpConnection() {
    final SocketConnector connector = new SocketConnector();
    connector.setHost(ServerPropertyHolder.httpHost.get());
    connector.setPort(ServerPropertyHolder.httpPort.get());
    return connector;
  }

  /**
   * Gets the thread pool.
   * 
   * @return the thread pool
   */
  private ThreadPool getThreadPool() {
    final QueuedThreadPool threadPool = new QueuedThreadPool();

    threadPool.setMinThreads(ServerPropertyHolder.httpMinThread.get());
    threadPool.setMaxThreads(ServerPropertyHolder.httpMaxThread.get());

    threadPool.setDaemon(false);
    threadPool.setName("HTTP server");
    threadPool.setMaxIdleTimeMs(2000);
    return threadPool;
  }

  /**
   * Server.
   * 
   * @return the server
   * @throws KeyStoreException the key store exception
   * @throws FileNotFoundException the file not found exception
   */
  @Bean(destroyMethod = "stop", initMethod = "start")
  Server server() throws KeyStoreException, FileNotFoundException {

    final List<Handler> handlers = new ArrayList<>();

    if (H2ConsolePropertyHolder.h2ConsoleEnabled.get()) {
      handlers.add(embeddedServlet3JettyFactory.h2consoleServletContextHandler(null));
    }

    handlers.add(embeddedServlet3JettyFactory.newWebAppContext(null, RemotingInitializer.class));

    final Server server = embeddedServlet3JettyFactory.newServer(handlers.toArray(new Handler[0]));
    server.setStopAtShutdown(true);
    server.setSendServerVersion(true);
    server.setSendDateHeader(true);
    server.setGracefulShutdown(1000);

    server.setThreadPool(getThreadPool());

    server.setConnectors(new Connector[] { getHttpConnection() });

    return server;
  }

}
