/**
 * Copyright 2012 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr>
 *
 * This software is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software. If not, see <http://www.gnu.org/licenses/>.
 *
 * $Id$
 */
package org.projectsforge.swap.proxy.starter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerRequestExecutor;

/**
 * The Class RemotingConfiguration.
 *
 * @author Sébastien Aupetit
 */
@Configuration
class RemotingConfiguration {

  /** The client remoting key store to use for the invokerRequestexecutor */
  @Autowired
  RemotingClientKeyStore remotingKeyStore;

  /**
   * Invoker requestexecutor.
   *
   * @return the http invoker request executor
   * @throws Exception
   */
  @Bean(name = "remoting.httpInvokerRequestExecutor")
  HttpInvokerRequestExecutor invokerRequestexecutor() {
    return new RemotingSSLHttpInvokerRequestExecutor(remotingKeyStore);
  }

}
