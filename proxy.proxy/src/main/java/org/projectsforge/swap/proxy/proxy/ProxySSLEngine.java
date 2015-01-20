/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr> This file
 * is part of SHS. SWAP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version. SWAP P is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with SHS. If not, see
 * <http://www.gnu.org/licenses/>. $Id$
 */
package org.projectsforge.swap.proxy.proxy;

import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.cert.Certificate;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.security.cert.X509Certificate;
import org.projectsforge.swap.proxy.certificate.CertificateManager;
import org.projectsforge.swap.proxy.certificate.CertificatePropertyHolder;
import org.projectsforge.swap.proxy.certificate.CertificateTarget;

/**
 * The Class ProxySSLEngine.
 * 
 * @author Sébastien Aupetit
 */
public class ProxySSLEngine extends SSLEngine {

  /**
   * The Class ProxySSLSession.
   * 
   * @author Sébastien Aupetit
   */
  private class ProxySSLSession implements SSLSession {

    /** The session. */
    private SSLSession session;

    /** The creation time. */
    private final long creationTime;

    /** The accept large fragments. */
    private final boolean acceptLargeFragments;

    /**
     * Instantiates a new proxy ssl session.
     */
    public ProxySSLSession() {
      creationTime = System.currentTimeMillis();
      acceptLargeFragments = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
        @Override
        public Boolean run() {
          return Boolean.valueOf(System.getProperty("jsse.SSLEngine.acceptLargeFragments", "false"));
        }
      });
    }

    /*
     * (non-Javadoc)
     * @see javax.net.ssl.SSLSession#getApplicationBufferSize()
     */
    @Override
    public int getApplicationBufferSize() {
      if (getSession() == null) {
        return getPacketBufferSize() - 5;
      } else {
        return getSession().getApplicationBufferSize();
      }
    }

    /*
     * (non-Javadoc)
     * @see javax.net.ssl.SSLSession#getCipherSuite()
     */
    @Override
    public String getCipherSuite() {
      if (getSession() == null) {
        throw new IllegalStateException("Not callable at this state");
      } else {
        return getSession().getCipherSuite();
      }
    }

    /*
     * (non-Javadoc)
     * @see javax.net.ssl.SSLSession#getCreationTime()
     */
    @Override
    public long getCreationTime() {
      if (getSession() == null) {
        return this.creationTime;
      } else {
        return getSession().getCreationTime();
      }
    }

    /*
     * (non-Javadoc)
     * @see javax.net.ssl.SSLSession#getId()
     */
    @Override
    public byte[] getId() {
      if (getSession() == null) {
        throw new IllegalStateException("Not callable at this state");
      } else {
        return getSession().getId();
      }
    }

    /*
     * (non-Javadoc)
     * @see javax.net.ssl.SSLSession#getLastAccessedTime()
     */
    @Override
    public long getLastAccessedTime() {
      if (getSession() == null) {
        return creationTime;
      } else {
        return getSession().getLastAccessedTime();
      }
    }

    /*
     * (non-Javadoc)
     * @see javax.net.ssl.SSLSession#getLocalCertificates()
     */
    @Override
    public Certificate[] getLocalCertificates() {
      if (getSession() == null) {
        throw new IllegalStateException("Not callable at this state");
      } else {
        return getSession().getLocalCertificates();
      }
    }

    /*
     * (non-Javadoc)
     * @see javax.net.ssl.SSLSession#getLocalPrincipal()
     */
    @Override
    public Principal getLocalPrincipal() {
      if (getSession() == null) {
        throw new IllegalStateException("Not callable at this state");
      } else {
        return getSession().getLocalPrincipal();
      }
    }

    /*
     * (non-Javadoc)
     * @see javax.net.ssl.SSLSession#getPacketBufferSize()
     */
    @Override
    public int getPacketBufferSize() {
      if (getSession() == null) {
        return acceptLargeFragments ? 33049 : '\u4119';
      } else {
        return getSession().getPacketBufferSize();
      }
    }

    /*
     * (non-Javadoc)
     * @see javax.net.ssl.SSLSession#getPeerCertificateChain()
     */
    @Override
    public X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException {
      if (getSession() == null) {
        throw new IllegalStateException("Not callable at this state");
      } else {
        return getSession().getPeerCertificateChain();
      }
    }

    /*
     * (non-Javadoc)
     * @see javax.net.ssl.SSLSession#getPeerCertificates()
     */
    @Override
    public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
      if (getSession() == null) {
        throw new IllegalStateException("Not callable at this state");
      } else {
        return getSession().getPeerCertificates();
      }
    }

    /*
     * (non-Javadoc)
     * @see javax.net.ssl.SSLSession#getPeerHost()
     */
    @Override
    public String getPeerHost() {
      return null;
    }

    /*
     * (non-Javadoc)
     * @see javax.net.ssl.SSLSession#getPeerPort()
     */
    @Override
    public int getPeerPort() {
      return -1;
    }

    /*
     * (non-Javadoc)
     * @see javax.net.ssl.SSLSession#getPeerPrincipal()
     */
    @Override
    public Principal getPeerPrincipal() throws SSLPeerUnverifiedException {
      if (getSession() == null) {
        throw new SSLPeerUnverifiedException("peer not authenticated");
      } else {
        return getSession().getPeerPrincipal();
      }
    }

    /*
     * (non-Javadoc)
     * @see javax.net.ssl.SSLSession#getProtocol()
     */
    @Override
    public String getProtocol() {
      if (getSession() == null) {
        return "NONE";
      } else {
        return getSession().getProtocol();
      }
    }

    /**
     * Gets the session.
     * 
     * @return the session
     */
    public SSLSession getSession() {
      if (engine != null && session == null) {
        session = engine.getSession();
      }
      return session;
    }

    /*
     * (non-Javadoc)
     * @see javax.net.ssl.SSLSession#getSessionContext()
     */
    @Override
    public SSLSessionContext getSessionContext() {
      if (getSession() == null) {
        return null;
      } else {
        return getSession().getSessionContext();
      }
    }

    /*
     * (non-Javadoc)
     * @see javax.net.ssl.SSLSession#getValue(java.lang.String)
     */
    @Override
    public Object getValue(final String name) {
      if (getSession() == null) {
        throw new IllegalStateException("Not callable at this state");
      } else {
        return getSession().getValue(name);
      }
    }

    /*
     * (non-Javadoc)
     * @see javax.net.ssl.SSLSession#getValueNames()
     */
    @Override
    public String[] getValueNames() {
      if (getSession() == null) {
        throw new IllegalStateException("Not callable at this state");
      } else {
        return getSession().getValueNames();
      }
    }

    /*
     * (non-Javadoc)
     * @see javax.net.ssl.SSLSession#invalidate()
     */
    @Override
    public void invalidate() {
      if (getSession() == null) {
        throw new IllegalStateException("Not callable at this state");
      } else {
        getSession().invalidate();
      }
    }

    /*
     * (non-Javadoc)
     * @see javax.net.ssl.SSLSession#isValid()
     */
    @Override
    public boolean isValid() {
      if (getSession() == null) {
        throw new IllegalStateException("Not callable at this state");
      } else {
        return getSession().isValid();
      }
    }

    /*
     * (non-Javadoc)
     * @see javax.net.ssl.SSLSession#putValue(java.lang.String,
     * java.lang.Object)
     */
    @Override
    public void putValue(final String name, final Object value) {
      if (getSession() == null) {
        throw new IllegalStateException("Not callable at this state");
      } else {
        getSession().putValue(name, value);
      }
    }

    /*
     * (non-Javadoc)
     * @see javax.net.ssl.SSLSession#removeValue(java.lang.String)
     */
    @Override
    public void removeValue(final String name) {
      if (getSession() == null) {
        throw new IllegalStateException("Not callable at this state");
      } else {
        getSession().removeValue(name);
      }
    }

  }

  /**
   * The Enum StateMachine.
   * 
   * @author Sébastien Aupetit
   */
  enum StateMachine {

    /** The INITIAL. */
    INITIAL,
    /** The CONNECTING. */
    CONNECTING,
    /** The CONNECTED. */
    CONNECTED,
    /** The SSL. */
    SSL
  }

  /** The engine. */
  private SSLEngine engine;

  /** The certificate manager. */
  private final CertificateManager certificateManager;

  /** The enabled cipher suites. */
  private String[] enabledCipherSuites;

  /** The enabled protocols. */
  private String[] enabledProtocols;;

  /** The need client auth. */
  private boolean needClientAuth = false;

  /** The enable session creation. */
  private boolean enableSessionCreation = true;

  /** The use client mode. */
  private boolean useClientMode = false;

  /** The want client auth. */
  private boolean wantClientAuth = false;

  /** The state machine. */
  private StateMachine stateMachine = StateMachine.INITIAL;

  /** The certificate target. */
  private CertificateTarget certificateTarget;

  /**
   * Instantiates a new proxy SSL engine.
   * 
   * @param certificateManager the certificate manager
   */
  public ProxySSLEngine(final CertificateManager certificateManager) {
    this.certificateManager = certificateManager;
  }

  /*
   * (non-Javadoc)
   * @see javax.net.ssl.SSLEngine#beginHandshake()
   */
  @Override
  public void beginHandshake() throws SSLException {
    if (engine != null) {
      engine.beginHandshake();
    }
  }

  /*
   * (non-Javadoc)
   * @see javax.net.ssl.SSLEngine#closeInbound()
   */
  @Override
  public void closeInbound() throws SSLException {
    if (engine != null) {
      try {
        engine.closeInbound();
      } catch (final SSLException e) {
        // ignore exception
      }
    }
  }

  /*
   * (non-Javadoc)
   * @see javax.net.ssl.SSLEngine#closeOutbound()
   */
  @Override
  public void closeOutbound() {
    if (engine != null) {
      engine.closeOutbound();
    }
  }

  /* ------------------------------------------------------------ */
  /**
   * Creates the ssl context.
   * 
   * @return the sSL context
   * @throws Exception the exception
   */
  private SSLContext createSSLContext() throws Exception {
    final KeyManager[] keyManagers = getKeyManagers();
    final TrustManager[] trustManagers = getTrustManagers();
    final SSLContext context = SSLContext.getInstance("TLS");
    context.init(keyManagers, trustManagers, null);
    return context;
  }

  /* ------------------------------------------------------------ */
  /**
   * Creates the ssl engine.
   * 
   * @return the SSL engine
   * @throws Exception the exception
   */
  private synchronized SSLEngine createSSLEngine() throws Exception {
    final SSLEngine engine = createSSLContext().createSSLEngine();
    engine.setUseClientMode(false);
    if (enabledCipherSuites != null) {
      engine.setEnabledCipherSuites(enabledCipherSuites);
    }
    if (enabledProtocols != null) {
      engine.setEnabledProtocols(enabledProtocols);
    }
    engine.setNeedClientAuth(needClientAuth);
    engine.setEnableSessionCreation(enableSessionCreation);
    engine.setUseClientMode(useClientMode);
    engine.setWantClientAuth(wantClientAuth);
    return engine;
  }

  /*
   * (non-Javadoc)
   * @see javax.net.ssl.SSLEngine#getDelegatedTask()
   */
  @Override
  public Runnable getDelegatedTask() {
    if (engine == null) {
      return null;
    } else {
      return engine.getDelegatedTask();
    }
  }

  /*
   * (non-Javadoc)
   * @see javax.net.ssl.SSLEngine#getEnabledCipherSuites()
   */
  @Override
  public String[] getEnabledCipherSuites() {
    if (engine == null) {
      return enabledCipherSuites;
    } else {
      return engine.getEnabledCipherSuites();
    }
  }

  /*
   * (non-Javadoc)
   * @see javax.net.ssl.SSLEngine#getEnabledProtocols()
   */
  @Override
  public String[] getEnabledProtocols() {
    if (engine == null) {
      return enabledProtocols;
    } else {
      return engine.getEnabledProtocols();
    }
  }

  /*
   * (non-Javadoc)
   * @see javax.net.ssl.SSLEngine#getEnableSessionCreation()
   */
  @Override
  public boolean getEnableSessionCreation() {
    if (engine == null) {
      return enableSessionCreation;
    } else {
      return engine.getEnableSessionCreation();
    }
  }

  /*
   * (non-Javadoc)
   * @see javax.net.ssl.SSLEngine#getHandshakeStatus()
   */
  @Override
  public HandshakeStatus getHandshakeStatus() {

    if (engine != null) {
      return engine.getHandshakeStatus();
    } else {
      return HandshakeStatus.NOT_HANDSHAKING;
    }
  }

  /* ------------------------------------------------------------ */
  /**
   * Gets the key managers.
   * 
   * @return the key managers
   * @throws Exception the exception
   */
  private KeyManager[] getKeyManagers() throws Exception {
    final KeyStore keyStore = certificateManager.getKeyStoreForTarget(certificateTarget);
    final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
    keyManagerFactory.init(keyStore, CertificatePropertyHolder.pemPassword.get().toCharArray());
    return keyManagerFactory.getKeyManagers();
  }

  /*
   * (non-Javadoc)
   * @see javax.net.ssl.SSLEngine#getNeedClientAuth()
   */
  @Override
  public boolean getNeedClientAuth() {
    if (engine == null) {
      throw new UnsupportedOperationException();
    } else {
      return engine.getNeedClientAuth();
    }
  }

  /*
   * (non-Javadoc)
   * @see javax.net.ssl.SSLEngine#getSession()
   */
  @Override
  public SSLSession getSession() {
    return new ProxySSLSession();
  }

  /*
   * (non-Javadoc)
   * @see javax.net.ssl.SSLEngine#getSupportedCipherSuites()
   */
  @Override
  public String[] getSupportedCipherSuites() {
    if (engine == null) {
      throw new UnsupportedOperationException();
    } else {
      return engine.getSupportedCipherSuites();
    }
  }

  /*
   * (non-Javadoc)
   * @see javax.net.ssl.SSLEngine#getSupportedProtocols()
   */
  @Override
  public String[] getSupportedProtocols() {
    if (engine == null) {
      throw new UnsupportedOperationException();
    } else {
      return engine.getSupportedProtocols();
    }
  }

  /**
   * Gets the trust managers.
   * 
   * @return the trust managers
   * @throws Exception the exception
   */
  private TrustManager[] getTrustManagers() throws Exception {
    final KeyStore trustStore = certificateManager.getKeyStoreForTarget(certificateTarget);
    final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
    trustManagerFactory.init(trustStore);
    return trustManagerFactory.getTrustManagers();
  }

  /*
   * (non-Javadoc)
   * @see javax.net.ssl.SSLEngine#getUseClientMode()
   */
  @Override
  public boolean getUseClientMode() {
    if (engine == null) {
      return useClientMode;
    } else {
      return engine.getUseClientMode();
    }
  }

  /*
   * (non-Javadoc)
   * @see javax.net.ssl.SSLEngine#getWantClientAuth()
   */
  @Override
  public boolean getWantClientAuth() {
    if (engine == null) {
      return wantClientAuth;
    } else {
      return engine.getWantClientAuth();
    }
  }

  /**
   * Checks if is connected.
   * 
   * @return true, if is connected
   */
  public synchronized boolean isConnected() {
    return stateMachine == StateMachine.CONNECTED;
  }

  /*
   * (non-Javadoc)
   * @see javax.net.ssl.SSLEngine#isInboundDone()
   */
  @Override
  public boolean isInboundDone() {
    if (engine == null) {
      return false;
    } else {
      return engine.isInboundDone();
    }
  }

  /*
   * (non-Javadoc)
   * @see javax.net.ssl.SSLEngine#isOutboundDone()
   */
  @Override
  public boolean isOutboundDone() {
    if (engine == null) {
      return true;
    } else {
      return engine.isOutboundDone();
    }
  }

  /*
   * (non-Javadoc)
   * @see javax.net.ssl.SSLEngine#setEnabledCipherSuites(java.lang.String[])
   */
  @Override
  public void setEnabledCipherSuites(final String[] cipherSuites) {
    this.enabledCipherSuites = cipherSuites;
  }

  /*
   * (non-Javadoc)
   * @see javax.net.ssl.SSLEngine#setEnabledProtocols(java.lang.String[])
   */
  @Override
  public void setEnabledProtocols(final String[] protocols) {
    this.enabledProtocols = protocols;
  }

  /*
   * (non-Javadoc)
   * @see javax.net.ssl.SSLEngine#setEnableSessionCreation(boolean)
   */
  @Override
  public void setEnableSessionCreation(final boolean sessionCreation) {
    this.enableSessionCreation = sessionCreation;
  }

  /**
   * Sets the host.
   * 
   * @param certificateTarget the new host
   * @throws Exception the exception
   */
  public void setHost(final CertificateTarget certificateTarget) throws Exception {
    this.certificateTarget = certificateTarget;
    engine = createSSLEngine();
  }

  /*
   * (non-Javadoc)
   * @see javax.net.ssl.SSLEngine#setNeedClientAuth(boolean)
   */
  @Override
  public void setNeedClientAuth(final boolean needClientAuth) {
    this.needClientAuth = needClientAuth;
  }

  /*
   * (non-Javadoc)
   * @see javax.net.ssl.SSLEngine#setUseClientMode(boolean)
   */
  @Override
  public void setUseClientMode(final boolean useClientMode) {
    this.useClientMode = useClientMode;
  }

  /*
   * (non-Javadoc)
   * @see javax.net.ssl.SSLEngine#setWantClientAuth(boolean)
   */
  @Override
  public void setWantClientAuth(final boolean wantClientAuth) {
    this.wantClientAuth = wantClientAuth;
  }

  /*
   * (non-Javadoc)
   * @see javax.net.ssl.SSLEngine#unwrap(java.nio.ByteBuffer,
   * java.nio.ByteBuffer[], int, int)
   */
  @Override
  public synchronized SSLEngineResult unwrap(final ByteBuffer src, final ByteBuffer[] dests,
      final int offset, final int length) throws SSLException {
    if (engine == null) {
      System.err.println(src);
    }
    return engine.unwrap(src, dests, offset, length);
  }

  /**
   * Upgrade to ssl.
   */
  public synchronized void upgradeToSSL() {
    stateMachine = StateMachine.SSL;
  }

  /*
   * (non-Javadoc)
   * @see javax.net.ssl.SSLEngine#wrap(java.nio.ByteBuffer[], int, int,
   * java.nio.ByteBuffer)
   */
  @Override
  public synchronized SSLEngineResult wrap(final ByteBuffer[] srcs, final int offset,
      final int length, final ByteBuffer dst) throws SSLException {
    return engine.wrap(srcs, offset, length, dst);

  }
}
