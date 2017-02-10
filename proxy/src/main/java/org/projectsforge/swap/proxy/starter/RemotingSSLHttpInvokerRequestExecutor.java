package org.projectsforge.swap.proxy.starter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.springframework.remoting.httpinvoker.HttpComponentsHttpInvokerRequestExecutor;
import org.springframework.remoting.httpinvoker.HttpInvokerRequestExecutor;

/**
 * Special HTTP Invoker that setup a custom SSL configuration
 * for both side authentication
 * 
 * @author Vincent Rouillé
 */
public class RemotingSSLHttpInvokerRequestExecutor extends
    HttpComponentsHttpInvokerRequestExecutor implements
    HttpInvokerRequestExecutor {

  /** The logger */
  private static final Log logger = LogFactory.getLog(RemotingSSLHttpInvokerRequestExecutor.class);
  
  /**
   * Constructor that register the custom swaphttps scheme and its related ssl configuration
   * 
   * @param remotingKeyStore
   */
  RemotingSSLHttpInvokerRequestExecutor(RemotingClientKeyStore remotingKeyStore)
  {
    super();
    try {
      SslContextFactory sslContextFactory = new SslContextFactory();
  
      // Setup the key/trust stores
      sslContextFactory.setKeyStorePath(remotingKeyStore.getKeyStorePath());
      sslContextFactory.setKeyStorePassword(remotingKeyStore.getKeyStorePassword());
      sslContextFactory.setTrustStore(remotingKeyStore.getTrustStorePath());
      sslContextFactory.setTrustStorePassword(remotingKeyStore.getTrustStorePassword());
      sslContextFactory.setCertAlias(remotingKeyStore.getKeyAlias());
      
      // Exclude unsafe protocols / ciphers
      sslContextFactory.setExcludeProtocols("SSLv2Hello", "SSLv3"); //< Those protocols aren't safe at all...
      sslContextFactory.setExcludeCipherSuites(
              "SSL_RSA_WITH_DES_CBC_SHA",
              "SSL_DHE_RSA_WITH_DES_CBC_SHA",
              "SSL_DHE_DSS_WITH_DES_CBC_SHA",
              "SSL_RSA_EXPORT_WITH_RC4_40_MD5",
              "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA",
              "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA",
              "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA");
      sslContextFactory.start();
      
      // Create the socket factory with the previously set settings
      SSLSocketFactory socketFactory = new SSLSocketFactory(sslContextFactory.getSslContext());
      
      // Register the new custom scheme
      // We could also override the previously defined https scheme
      // Put this provide a way to only change the scheme to go back to the standard https
      SchemeRegistry schemeRegistry = this.getHttpClient().getConnectionManager().getSchemeRegistry();
      schemeRegistry.register(new Scheme("swaphttps", 9443, socketFactory));
    } catch(Exception e) {
      logger.error("Unable to initialize remoting SSL", e);
    }
  }

}
