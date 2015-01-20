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
 * <http://www.gnu.org/licenses/>. $Id: ProxySslSelectChannelConnector.java 98
 * 2011-11-24 12:10:32Z sebtic $
 */
package org.projectsforge.swap.proxy.proxy;

// ========================================================================
// Copyright (c) 2004-2009 Mort Bay Consulting Pty. Ltd.
// ------------------------------------------------------------------------
// All rights reserved. This program and the accompanying materials
// are made available under the terms of the Eclipse Public License v1.0
// and Apache License v2.0 which accompanies this distribution.
// The Eclipse Public License is available at
// http://www.eclipse.org/legal/epl-v10.html
// The Apache License v2.0 is available at
// http://www.opensource.org/licenses/apache2.0.php
// You may elect to redistribute this code under either of these licenses.
// ========================================================================

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.security.KeyStoreException;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import org.eclipse.jetty.http.HttpSchemes;
import org.eclipse.jetty.io.AsyncEndPoint;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.nio.AsyncConnection;
import org.eclipse.jetty.io.nio.SslConnection;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslCertificates;
import org.projectsforge.swap.proxy.certificate.CertificateManager;

/**
 * A modified SslSelectChannelConnector implementation that manages proxied
 * connection and switching of SSL certificates according to the CONNECT HTTP
 * method.
 * 
 * @author Sébastien Aupetit
 */
public class ProxySslSelectChannelConnector extends SelectChannelConnector {

  /** The certificate manager. */
  private CertificateManager certificateManager;

  /** The agent name. */
  private String agentName;

  /* ------------------------------------------------------------ */
  /**
   * Instantiates a new adaptable proxy ssl select channel connector.
   * 
   * @throws KeyStoreException the key store exception
   */
  public ProxySslSelectChannelConnector() throws KeyStoreException {
    setUseDirectBuffers(false);
    setSoLingerTime(30000);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.eclipse.jetty.server.nio.SelectChannelConnector#customize(org.eclipse
   * .jetty.io.EndPoint, org.eclipse.jetty.server.Request)
   */
  @Override
  public void customize(final EndPoint endpoint, final Request request) throws IOException {
    request.setScheme(HttpSchemes.HTTPS);
    super.customize(endpoint, request);

    final SslConnection.SslEndPoint sslEndpoint = (SslConnection.SslEndPoint) endpoint;
    final SSLEngine sslEngine = sslEndpoint.getSslEngine();
    final SSLSession sslSession = sslEngine.getSession();

    SslCertificates.customize(sslSession, endpoint, request);
  }

  /**
   * Gets the certificate manager.
   * 
   * @return the certificate manager
   */
  public CertificateManager getCertificateManager() {
    return certificateManager;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.eclipse.jetty.server.nio.SelectChannelConnector#newConnection(java.
   * nio.channels.SocketChannel, org.eclipse.jetty.io.AsyncEndPoint)
   */
  @Override
  protected AsyncConnection newConnection(final SocketChannel channel, final AsyncEndPoint endpoint) {
    final ProxySSLEngine engine = new ProxySSLEngine(certificateManager);
    engine.setUseClientMode(false);
    final SslConnection connection = newSslConnection(endpoint, engine);
    final AsyncConnection delegate = newPlainConnection(channel, connection.getSslEndPoint());
    connection.getSslEndPoint().setConnection(delegate);
    connection.setAllowRenegotiate(false);
    return connection;
  }

  /**
   * New plain connection.
   * 
   * @param channel the channel
   * @param endPoint the end point
   * @return the async connection
   */
  protected AsyncConnection newPlainConnection(final SocketChannel channel,
      final AsyncEndPoint endPoint) {
    return super.newConnection(channel, endPoint);
  }

  /**
   * New ssl connection.
   * 
   * @param endpoint the endpoint
   * @param engine the engine
   * @return the ssl connection
   */
  protected SslConnection newSslConnection(final AsyncEndPoint endpoint, final ProxySSLEngine engine) {
    return new ProxySslConnection(engine, endpoint, agentName);
  }

  /**
   * Sets the agent name.
   * 
   * @param agentName the new agent name
   */
  public void setAgentName(final String agentName) {
    this.agentName = agentName;
  }

  /**
   * Sets the certificate manager.
   * 
   * @param certificateManager the new certificate manager
   */
  public void setCertificateManager(final CertificateManager certificateManager) {
    this.certificateManager = certificateManager;
  }

}
