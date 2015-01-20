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
package org.projectsforge.swap.proxy.starter;

import java.io.FileNotFoundException;
import java.security.KeyStoreException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.projectsforge.swap.core.embeddedservlet3jetty.EmbeddedServlet3JettyFactory;
import org.projectsforge.swap.core.environment.Environment;
import org.projectsforge.swap.core.http.HttpPropertyHolder;
import org.projectsforge.swap.core.persistence.H2ConsolePropertyHolder;
import org.projectsforge.swap.core.webui.WebUIInitializer;
import org.projectsforge.swap.proxy.certificate.CertificateManager;
import org.projectsforge.swap.proxy.proxy.ProxyPropertyHolder;
import org.projectsforge.swap.proxy.proxy.ProxyServlet;
import org.projectsforge.swap.proxy.proxy.ProxySslSelectChannelConnector;
import org.projectsforge.swap.proxy.webui.WebUIPropertyHolder;
import org.projectsforge.swap.proxy.webui.WelcomeComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The Class ProxyServerConfiguration.
 * 
 * @author Sébastien Aupetit
 */
@Configuration
public class ProxyServerConfiguration {

  /** The environment. */
  @Autowired
  Environment environment;

  /** The certificate manager. */
  @Autowired
  CertificateManager certificateManager;

  /** The embedded servlet3 jetty factory. */
  @Autowired
  EmbeddedServlet3JettyFactory embeddedServlet3JettyFactory;

  /**
   * Gets the http connection.
   * 
   * @return the http connection
   */
  private SocketConnector getHttpConnection() {
    final SocketConnector connector = new SocketConnector();
    connector.setHost(ProxyPropertyHolder.httpHost.get());
    connector.setPort(ProxyPropertyHolder.httpPort.get());
    return connector;
  }

  /**
   * Gets the https connection.
   * 
   * @return the https connection
   * @throws KeyStoreException the key store exception
   */
  private ProxySslSelectChannelConnector getHttpsConnection() throws KeyStoreException {
    final ProxySslSelectChannelConnector connector = new ProxySslSelectChannelConnector();
    connector.setHost(ProxyPropertyHolder.httpHost.get());
    connector.setPort(ProxyPropertyHolder.httpSecurePort.get());
    connector.setCertificateManager(certificateManager);
    connector.setAgentName(HttpPropertyHolder.agentName.get());
    return connector;
  }

  /**
   * Gets the thread pool.
   * 
   * @return the thread pool
   */
  private ThreadPool getThreadPool() {
    final QueuedThreadPool threadPool = new QueuedThreadPool();
    threadPool.setMinThreads(ProxyPropertyHolder.httpMinThread.get());
    threadPool.setMaxThreads(ProxyPropertyHolder.httpMaxThread.get());
    threadPool.setDaemon(true);
    threadPool.setName("HTTP server");
    threadPool.setMaxIdleTimeMs(60 * 1000);
    return threadPool;
  }

  /**
   * H2concole welcome component.
   * 
   * @return the welcome component
   */
  @Bean
  WelcomeComponent h2concoleWelcomeComponent() {
    return new WelcomeComponent() {

      @Override
      public String getDescription() {
        return "H2 console";
      }

      @Override
      public String getName() {
        return "H2 console";
      }

      @Override
      public int getPriority() {
        return Integer.MAX_VALUE - 2;
      }

      @Override
      public String getUrl() {
        return "/h2console";
      }

      @Override
      public boolean isActive() {
        return true;
      }
    };
  }

  /**
   * Proxy servlet.
   * 
   * @return the proxy servlet
   */
  @Bean
  public ProxyServlet proxyServlet() {
    return new ProxyServlet();
  }

  /**
   * Proxy servlet context handler.
   * 
   * @return the statistics handler
   */
  StatisticsHandler proxyServletContextHandler() {
    final ServletContextHandler proxyServletContextHandler = new ServletContextHandler();
    proxyServletContextHandler.addServlet(new ServletHolder("proxy servlet", proxyServlet()), "/*");
    final StatisticsHandler statisticsHandler = new StatisticsHandler();
    statisticsHandler.setHandler(proxyServletContextHandler);
    return statisticsHandler;
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

    final String virtualHost = WebUIPropertyHolder.hostname.get();

    final List<Handler> handlers = new ArrayList<>();

    if (H2ConsolePropertyHolder.h2ConsoleEnabled.get()) {
      handlers.add(embeddedServlet3JettyFactory.h2consoleServletContextHandler(virtualHost));
    }

    handlers
        .add(embeddedServlet3JettyFactory.newWebAppContext(virtualHost, WebUIInitializer.class));

    handlers.add(proxyServletContextHandler());

    final Server server = embeddedServlet3JettyFactory.newServer(handlers.toArray(new Handler[0]));
    server.setStopAtShutdown(true);
    server.setSendServerVersion(true);
    server.setSendDateHeader(false);
    server.setGracefulShutdown(1000);

    server.setThreadPool(getThreadPool());

    server.setConnectors(new Connector[] { getHttpConnection(), getHttpsConnection() });

    return server;
  }
}
