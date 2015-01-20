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
package org.projectsforge.swap.server.remoting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.projectsforge.swap.core.remoting.RemoteInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.remoting.support.DefaultRemoteInvocationExecutor;
import org.springframework.remoting.support.RemoteInvocationExecutor;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

/**
 * A specialized UrlhandlerMapping class which detects bean annotated with.
 * {@RemoteInterface} and exports the defined services.
 * 
 * @author Sébastien Aupetit
 */
public class RemotingExporterUrlHandlerMapping extends AbstractUrlHandlerMapping {

  /** The logger. */
  private final Logger logger = LoggerFactory.getLogger("org.projectsforge.swap.server.remoting");

  /** The remote invocation executor. */
  private final RemoteInvocationExecutor remoteInvocationExecutor = new DefaultRemoteInvocationExecutor();

  /** The detect handlers in ancestor contexts. */
  private boolean detectHandlersInAncestorContexts = false;

  /**
   * Collect interfaces.
   * 
   * @param interfaces the interfaces
   * @param clazz the clazz
   */
  private void collectInterfaces(final Set<Class<?>> interfaces, final Class<?> clazz) {
    if (clazz == null || clazz == Object.class) {
      return;
    }

    collectInterfaces(interfaces, clazz.getSuperclass());
    for (final Class<?> interfaceClass : clazz.getInterfaces()) {
      collectInterfaces(interfaces, interfaceClass);
      interfaces.add(interfaceClass);
    }
  }

  /**
   * Register all handlers found in the current ApplicationContext.
   * <p>
   * The actual URL determination for a handler is up to the concrete
   * 
   * @throws BeansException the beans exception
   *           {@link #determineUrlsForHandler(String)} implementation. A bean
   *           for which no such URLs could be determined is simply not
   *           considered a handler.
   * @see #determineUrlsForHandler(String)
   */
  protected void detectHandlers() throws BeansException {
    // Get the root context
    final AbstractApplicationContext wac = (AbstractApplicationContext) WebApplicationContextUtils
        .getWebApplicationContext(this.getServletContext());

    // get all beans
    final String[] beanNames = (this.detectHandlersInAncestorContexts ? BeanFactoryUtils
        .beanNamesForTypeIncludingAncestors(getApplicationContext(), Object.class)
        : getApplicationContext().getBeanNamesForType(Object.class));

    final Set<Class<?>> interfaces = new HashSet<>();

    for (final String beanName : beanNames) {
      if (getApplicationContext().findAnnotationOnBean(beanName, RemoteInterface.class) != null) {
        final Object bean = getApplicationContext().getBean(beanName);

        interfaces.clear();
        collectInterfaces(interfaces, bean.getClass());

        // For each interfaces of the bean annotated with @RemoteInterface, we
        // exports the service
        for (final Class<?> interfaceClass : interfaces) {
          if (interfaceClass.getAnnotation(RemoteInterface.class) != null) {
            final String name = "/remoting/" + interfaceClass.getName();
            HttpInvokerServiceExporter exporter = new HttpInvokerServiceExporter();
            exporter.setService(bean);
            exporter.setServiceInterface(interfaceClass);
            exporter.setRemoteInvocationExecutor(remoteInvocationExecutor);

            wac.getAutowireCapableBeanFactory().autowireBean(exporter);
            exporter = (HttpInvokerServiceExporter) wac.getAutowireCapableBeanFactory()
                .initializeBean(exporter, name);
            wac.getBeanFactory().registerSingleton(name, exporter);

            // register mapping
            registerHandler(determineUrlsForHandler(name), name);

            logger.info("Bean {} exported onto {}", beanName, name);
          }
        }
      }
    }

  }

  /**
   * Determine the URLs for the given handler bean.
   * 
   * @param beanName the name of the candidate bean
   * @return the URLs determined for the bean, or <code>null</code> or an empty
   *         array if none
   */
  protected String[] determineUrlsForHandler(final String beanName) {
    final List<String> urls = new ArrayList<String>();
    if (beanName.startsWith("/")) {
      urls.add(beanName);
    }
    final String[] aliases = getApplicationContext().getAliases(beanName);
    for (final String alias : aliases) {
      if (alias.startsWith("/")) {
        urls.add(alias);
      }
    }
    return StringUtils.toStringArray(urls);
  }

  /**
   * Calls the {@link #detectHandlers()} method in addition to the superclass's
   * initialization.
   * 
   * @throws ApplicationContextException the application context exception
   */
  @Override
  public void initApplicationContext() throws ApplicationContextException {
    super.initApplicationContext();
    detectHandlers();
  }

  /**
   * Set whether to detect handler beans in ancestor ApplicationContexts.
   * <p>
   * Default is "false": Only handler beans in the current ApplicationContext
   * will be detected, i.e. only in the context that this HandlerMapping itself
   * is defined in (typically the current DispatcherServlet's context).
   * <p>
   * Switch this flag on to detect handler beans in ancestor contexts (typically
   * the Spring root WebApplicationContext) as well.
   * 
   * @param detectHandlersInAncestorContexts the new detect handlers in ancestor
   *          contexts
   */
  public void setDetectHandlersInAncestorContexts(final boolean detectHandlersInAncestorContexts) {
    this.detectHandlersInAncestorContexts = detectHandlersInAncestorContexts;
  }

}
