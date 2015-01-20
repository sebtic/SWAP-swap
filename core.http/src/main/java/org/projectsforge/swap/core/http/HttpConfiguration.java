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
import org.projectsforge.swap.core.environment.Environment;
import org.projectsforge.utils.temporarystreams.TemporaryStreamsFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * The Class HttpConfiguration.
 * 
 * @author Sébastien Aupetit
 */
@Configuration
public class HttpConfiguration {

  /** The environment. */
  @Autowired
  Environment environment;

  /**
   * Servlet http client.
   * 
   * @return the servlet http client
   * @throws KeyManagementException the key management exception
   * @throws UnrecoverableKeyException the unrecoverable key exception
   * @throws NoSuchAlgorithmException the no such algorithm exception
   * @throws KeyStoreException the key store exception
   */
  @Bean
  ServletHttpClient servletHttpClient() throws KeyManagementException, UnrecoverableKeyException,
      NoSuchAlgorithmException, KeyStoreException {
    // TODO do better confirguration
    return new ServletHttpClient();
  }

  /**
   * Temporary streams factory.
   * 
   * @return the temporary streams factory
   */
  @Bean
  TemporaryStreamsFactory temporaryStreamsFactory() {
    return new TemporaryStreamsFactory();
  }

}
