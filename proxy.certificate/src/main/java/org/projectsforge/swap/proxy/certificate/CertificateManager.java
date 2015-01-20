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
 * <http://www.gnu.org/licenses/>. $Id: CertificateManager.java 98 2011-11-24
 * 12:10:32Z sebtic $
 */
package org.projectsforge.swap.proxy.certificate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.projectsforge.swap.core.environment.Environment;
import org.projectsforge.swap.core.environment.impl.EnvironmentPropertyHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The class that manages a registry of emitted trusted and untrusted
 * certificates for HTTPS proxying.
 * 
 * @author Sébastien Aupetit
 */
public class CertificateManager {

  /** The shared secure random. */
  @Autowired
  private SharedSecureRandom sharedSecureRandom;

  /** The environment. */
  @Autowired
  private Environment environment;

  /** The logger. */
  private final Logger logger = LoggerFactory.getLogger(CertificateManager.class);

  /** The trusted CA manager. */
  @Autowired
  private TrustedCertificationAgencyManager trustedCAM;

  /** The untrusted CA manager. */
  @Autowired
  private UntrustedCertificationAgencyManager untrustedCAM;

  /** The cached key store. */
  private final Map<CertificateTarget, KeyStore> cachedKeyStore = new HashMap<CertificateTarget, KeyStore>();

  /** The keystore. */
  private KeyStore trustKeystore;

  private File pemDirectory;

  /**
   * Gets the key store associated with this common name (host name).
   * 
   * @param certificateTarget the certificate target
   * @return the key store
   * @throws CertificateManagerException the certificate manager exception
   */
  public synchronized KeyStore getKeyStoreForTarget(final CertificateTarget certificateTarget)
      throws CertificateManagerException {
    KeyStore keyStore = cachedKeyStore.get(certificateTarget);
    if (keyStore == null) { // not in cache => try loading
      keyStore = loadKeyStore(certificateTarget);
      if (keyStore != null) { // loaded => check validity
        try {
          final X509Certificate cert = (X509Certificate) keyStore.getCertificate("proxiedhost");
          cert.checkValidity();
        } catch (final CertificateExpiredException | CertificateNotYetValidException
            | KeyStoreException e) {
          keyStore = null;
        }
      }

      if (keyStore == null) {
        keyStore = registerHost(certificateTarget);
        saveKeyStore(certificateTarget, keyStore);
      }
    }
    return keyStore;

  }

  /**
   * Gets the trusted CA manager.
   * 
   * @return the trusted CA manager
   */
  public CertificationAgencyManager getTrustedCAM() {
    return trustedCAM;
  }

  /**
   * Gets the untrusted CA manager.
   * 
   * @return the untrusted CA manager
   */
  public CertificationAgencyManager getUntrustedCAM() {
    return untrustedCAM;
  }

  /**
   * Inits the registry by loading the keystore which contain the trusted
   * certificate chain. Those certificates are used to validate remote
   * certificate chains.
   * 
   * @throws NoSuchAlgorithmException the no such algorithm exception
   * @throws CertificateException the certificate exception
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws KeyStoreException the key store exception
   */
  @PostConstruct
  public void init() throws NoSuchAlgorithmException, CertificateException, IOException,
      KeyStoreException {
    trustKeystore = KeyStore.getInstance("JKS");
    try (final FileInputStream fis = new FileInputStream(new File(
        EnvironmentPropertyHolder.configurationDirectory.get(),
        CertificatePropertyHolder.trustKeystoreName.get()))) {
      trustKeystore.load(fis, CertificatePropertyHolder.trustKeystorePassword.get().toCharArray());
    }

    pemDirectory = new File(EnvironmentPropertyHolder.configurationDirectory.get(),
        CertificatePropertyHolder.pemDirectory.get());
    pemDirectory.mkdirs();
  }

  /**
   * Checks if the chain is trustable.
   * 
   * @param chain the chain
   * @return true, if is trustable
   */
  private boolean isTrustable(final X509Certificate[] chain) {
    try {
      final Map<String, X509Certificate> trustedCerts = new HashMap<String, X509Certificate>();
      final Map<String, X509Certificate> allCerts = new HashMap<String, X509Certificate>();

      final Enumeration<String> aliases = trustKeystore.aliases();
      while (aliases.hasMoreElements()) {
        final X509Certificate cert = (X509Certificate) trustKeystore.getCertificate(aliases
            .nextElement());
        if (cert != null) {
          trustedCerts.put(cert.getSubjectDN().getName(), cert);
        }
      }

      allCerts.putAll(trustedCerts);
      for (final X509Certificate cert : chain) {
        allCerts.put(cert.getSubjectDN().getName(), cert);
      }

      X509Certificate cur = chain[0];
      while (true) {
        cur.checkValidity();
        if (trustedCerts.containsKey(cur.getIssuerDN().getName())) {
          return true;
        } else {
          if (cur.getSubjectDN().equals(cur.getIssuerDN())) {
            return false;
          } else {
            final X509Certificate parent = allCerts.get(cur.getIssuerDN().getName());
            if (parent == null) {
              return false;
            }
            cur.verify(parent.getPublicKey());
            cur = parent;
          }
        }
      }
    } catch (final Exception e) {
      return false;
    }
  }

  /**
   * Load key store.
   * 
   * @param certificateTarget the certificate target
   * @return the key store
   */
  private KeyStore loadKeyStore(final CertificateTarget certificateTarget) {
    KeyStore keyStore = cachedKeyStore.get(certificateTarget);
    if (keyStore == null) {
      try {
        try (final FileInputStream fis = new FileInputStream(new File(pemDirectory,
            certificateTarget.toString() + ".keystore"))) {
          keyStore = KeyStore.getInstance("JKS");
          keyStore.load(fis, CertificatePropertyHolder.pemPassword.get().toCharArray());
        }
        cachedKeyStore.put(certificateTarget, keyStore);
      } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
        return null;
      }
    }
    return keyStore;
  }

  /**
   * Save key store.
   * 
   * @param certificateTarget the certificate target
   * @param keyStore the key store
   */
  private void saveKeyStore(final CertificateTarget certificateTarget, final KeyStore keyStore) {
    try {
      try (final FileOutputStream fos = new FileOutputStream(new File(pemDirectory,
          certificateTarget.toString() + ".keystore"))) {
        keyStore.store(fos, CertificatePropertyHolder.pemPassword.get().toCharArray());
      }
    } catch (IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException e) {
      logger.debug("Keystore not saved on disk", e);
    }
    cachedKeyStore.put(certificateTarget, keyStore);

  }

  /**
   * Register the client certificate chain.
   * 
   * @param certificateTarget the certificate target
   * @param chain the chain
   * @return the key store
   * @throws CertificateManagerException the certificate manager exception
   */
  private KeyStore registerClientCertificate(final CertificateTarget certificateTarget,
      final X509Certificate[] chain) throws CertificateManagerException {
    if (isTrustable(chain)) {
      logger.debug("Creating a proxied certificate for trusted CN {}", certificateTarget);
      final KeyStore ketStore = trustedCAM.createCertifitate(certificateTarget, CertificatePropertyHolder.pemPassword.get(),
          "Trusted proxied certificate via Smart Web Accessibility Proxy");
      saveKeyStore(certificateTarget, ketStore);
      return ketStore;
    } else {
      logger.debug("Creating a proxied certificate for untrusted CN {}", certificateTarget);
      final KeyStore ketStore = untrustedCAM.createCertifitate(certificateTarget, CertificatePropertyHolder.pemPassword.get(),
          "Untrusted proxied certificate via Smart Web Accessibility Proxy");
      saveKeyStore(certificateTarget, ketStore);
      return ketStore;
    }
  };

  /**
   * Register the certificate of a host. A new proxied certificate is issued.
   * 
   * @param certificateTarget the certificate target
   * @return the key store containing the new certificate
   * @throws CertificateManagerException the certificate manager exception
   */
  private KeyStore registerHost(final CertificateTarget certificateTarget)
      throws CertificateManagerException {
    logger.info("Registering SSL certificates for host {}", certificateTarget);

    final CertificateRecorder certificateRecorder = new CertificateRecorder();
    try {
      final SSLSocketFactory socketFactory = new SSLSocketFactory(certificateRecorder,
          SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
      final Scheme sch = new Scheme("https", 443, socketFactory);
      final HttpHead request = new HttpHead("https://" + certificateTarget.getHostname() + ":"
          + certificateTarget.getPort());

      final HttpClient httpClient = new DefaultHttpClient();
      httpClient.getConnectionManager().getSchemeRegistry().register(sch);
      final HttpResponse response = httpClient.execute(request);
      final HttpEntity entity = response.getEntity();
      EntityUtils.consume(entity);
    } catch (IOException | KeyManagementException | UnrecoverableKeyException
        | NoSuchAlgorithmException | KeyStoreException e) {
      throw new CertificateManagerException("Can not connect to a valid SSL host "
          + certificateTarget, e);
    }

    if (certificateRecorder.chain != null) {
      final KeyStore store = registerClientCertificate(certificateTarget, certificateRecorder.chain);
      logger.debug("Registering SSL certificates for host {} done", certificateTarget);
      return store;
    } else {
      throw new CertificateManagerException("Can not connect to a valid SSL host "
          + certificateTarget);
    }
  }

}
