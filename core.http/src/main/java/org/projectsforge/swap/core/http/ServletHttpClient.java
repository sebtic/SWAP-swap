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
package org.projectsforge.swap.core.http;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.RedirectStrategy;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.RequestClientConnControl;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.SystemDefaultHttpClient;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;

/**
 * The Class ServletHttpClient.
 * 
 * @author Sébastien Aupetit
 */
public class ServletHttpClient extends SystemDefaultHttpClient {

  /**
   * Instantiates a new servlet http client.
   * 
   * @throws KeyManagementException the key management exception
   * @throws UnrecoverableKeyException the unrecoverable key exception
   * @throws NoSuchAlgorithmException the no such algorithm exception
   * @throws KeyStoreException the key store exception
   */
  public ServletHttpClient() throws KeyManagementException, UnrecoverableKeyException,
      NoSuchAlgorithmException, KeyStoreException {
    super();
    setRedirectStrategy(new RedirectStrategy() {

      @Override
      public HttpUriRequest getRedirect(final HttpRequest request, final HttpResponse response,
          final HttpContext context) throws ProtocolException {
        return null;
      }

      @Override
      public boolean isRedirected(final HttpRequest request, final HttpResponse response,
          final HttpContext context) throws ProtocolException {
        return false;
      }
    });
    final SSLSocketFactory socketFactory = new SSLSocketFactory(new TrustStrategy() {
      @Override
      public boolean isTrusted(final X509Certificate[] chain, final String authType)
          throws CertificateException {
        return true;
      }
    }, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
    final Scheme sch = new Scheme("https", 443, socketFactory);
    getConnectionManager().getSchemeRegistry().register(sch);
  }

  /*
   * (non-Javadoc)
   * @see org.apache.http.impl.client.DefaultHttpClient#createHttpProcessor()
   */
  @Override
  protected BasicHttpProcessor createHttpProcessor() {
    final BasicHttpProcessor httpproc = new BasicHttpProcessor();
    httpproc.addInterceptor(new RequestTargetHost());
    httpproc.addInterceptor(new RequestClientConnControl());
    httpproc.addInterceptor(new RequestUserAgent());
    return httpproc;
  }
}
