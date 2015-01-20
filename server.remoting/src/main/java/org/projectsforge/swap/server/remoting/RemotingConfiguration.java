/**
 * Copyright 2012 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr>
 * 
 * This software is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software. If not, see <http://www.gnu.org/licenses/>.
 * 
 * $Id$
 */
package org.projectsforge.swap.server.remoting;

import java.util.List;
import org.projectsforge.swap.core.webui.WebUIConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.remoting.support.DefaultRemoteInvocationExecutor;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;

/**
 * The Class RemotingConfiguration.
 * 
 * @author Sébastien Aupetit
 */
public class RemotingConfiguration extends WebUIConfiguration {

  /* (non-Javadoc)
   * @see org.projectsforge.swap.core.webui.WebUIConfiguration#configureHandlerExceptionResolvers(java.util.List)
   */
  @Override
  protected void configureHandlerExceptionResolvers(
      final List<HandlerExceptionResolver> exceptionResolvers) {
    addDefaultHandlerExceptionResolvers(exceptionResolvers);
    for (final HandlerExceptionResolver resolver : exceptionResolvers) {
      if (resolver instanceof AbstractHandlerExceptionResolver) {
        ((AbstractHandlerExceptionResolver) resolver).setWarnLogCategory("server.remoting");
      }
    }
  }

  /**
   * Default remote invocation executor.
   * 
   * @return the default remote invocation executor
   */
  @Bean
  DefaultRemoteInvocationExecutor defaultRemoteInvocationExecutor() {
    return new DefaultRemoteInvocationExecutor();
  }

  /**
   * Remoting exporter url handler mapping.
   * 
   * @return the remoting exporter url handler mapping
   */
  @Bean
  public RemotingExporterUrlHandlerMapping remotingExporterUrlHandlerMapping() {
    final RemotingExporterUrlHandlerMapping remotingExporterUrlHandlerMapping = new RemotingExporterUrlHandlerMapping();
    remotingExporterUrlHandlerMapping.setOrder(1);
    remotingExporterUrlHandlerMapping.setDetectHandlersInAncestorContexts(true);
    // remotingExporterUrlHandlerMapping.setAlwaysUseFullPath(true);
    remotingExporterUrlHandlerMapping.setInterceptors(getInterceptors());
    return remotingExporterUrlHandlerMapping;
  }

}
