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
package org.projectsforge.swap.proxy.tester;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.projectsforge.swap.proxy.proxy.ProxyPropertyHolder;

/**
 * A simple resource web server that simplify unit tests with the client proxy.
 * 
 * @author Sébastien Aupetit
 */
public class SimpleResourceServer {

  /** The port. */
  private int serverPort = 9999;

  /** The resources. */
  private final List<Resource> resources = new ArrayList<>();

  /** The server. */
  private Server server;

  /**
   * Instantiates a new simple resource server. The classpath directory
   * TEST-WEB-INF is defined as a source of resources.
   * 
   * @param environment the environment
   * @throws IOException
   */
  public SimpleResourceServer() throws IOException {
    for (final URL url : Collections.list(getClass().getClassLoader().getResources("TEST-WEB-INF"))) {
      addResource(Resource.newResource(url));
    }
  }

  /**
   * Adds a resource to the server.
   * 
   * @param resource the resource
   */
  public void addResource(final Resource resource) {
    resources.add(resource);
  }

  /**
   * Gets a connection through the proxy toward a resource .
   * 
   * @param path the path
   * @return the connection
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public HttpURLConnection getConnection(final String path) throws IOException {
    final Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost",
        ProxyPropertyHolder.httpPort.get()));
    final URL url = new URL("http://localhost:" + serverPort + "/" + path.replace(" ", "%20"));
    return (HttpURLConnection) url.openConnection(proxy);
  }

  /**
   * Gets the content of a resource through the proxy.
   * 
   * @param path the path
   * @return the content
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public String getContent(final String path) throws IOException {
    final HttpURLConnection connection = getConnection(path);
    connection.connect();

    try (final InputStream is = connection.getInputStream()) {
      return IOUtils.toString(is, "UTF-8");
    } finally {
      connection.disconnect();
    }
  }

  /**
   * Gets the server port.
   * 
   * @return the server port
   */
  public int getServerPort() {
    return serverPort;
  }

  /**
   * Wait for the server to stop.
   * 
   * @throws InterruptedException the interrupted exception
   */
  public void join() throws InterruptedException {
    server.join();
  }

  /**
   * Sets the server port.
   * 
   * @param serverPort the new server port
   */
  public void setPort(final int serverPort) {
    this.serverPort = serverPort;
  }

  /**
   * Start the server.
   * 
   * @throws Exception the exception
   */
  public void start() throws Exception {
    server = new Server();
    server.setStopAtShutdown(true);
    server.setSendServerVersion(true);
    server.setSendDateHeader(true);
    server.setGracefulShutdown(1000);

    final SelectChannelConnector connector = new SelectChannelConnector();
    connector.setHost("localhost");
    connector.setPort(serverPort);
    server.addConnector(connector);

    final HandlerCollection handlers = new HandlerCollection();
    server.setHandler(handlers);

    final ResourceHandler handler = new ResourceHandler();
    handler.setBaseResource(new ResourceCollection(resources.toArray(new Resource[0])));
    handler.setDirectoriesListed(true);
    handlers.addHandler(handler);
    handlers.addHandler(new DefaultHandler());
    server.start();
  }

  /**
   * Stop the server.
   * 
   * @throws Exception the exception
   */
  public void stop() throws Exception {
    server.stop();
  }
}
