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
package org.projectsforge.swap.proxy.starter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.projectsforge.swap.core.environment.impl.EnvironmentImpl;
import org.projectsforge.swap.core.environment.provider.InMemoryXmlContextProvider;
import org.projectsforge.swap.core.remoting.RemoteInterface;
import org.projectsforge.utils.annotations.AnnotationScanner;
import org.xml.sax.InputSource;

/**
 * The proxy {@link EnvironmentImpl}.
 *
 * @author Sébastien Aupetit
 */
public class ProxyEnvironment extends EnvironmentImpl {

  /**
   * Instantiates a new proxy environment.
   *
   * @param name
   *          the name
   */
  public ProxyEnvironment(final String name) {
    super(name);
    final InMemoryXmlContextProvider provider = getInMemoryXmlContextProvider();
    setInMemoryXmlContextProvider(environment -> {
      final List<InputSource> result = new ArrayList<>();
      result.addAll(provider.getInputSources(environment));
      result.add(detectRemoteService());
      return result;
    });
  }

  /**
   * Detect remote service.
   *
   * @return the input source
   */
  private InputSource detectRemoteService() {
    final AnnotationScanner annotationScanner = getContext().getBean(AnnotationScanner.class);

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (final PrintWriter out = new PrintWriter(baos)) {

      out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      out.println("<beans xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");

      if (getContext().containsBean("remoting.httpInvokerRequestExecutor")) {
        for (final String className : annotationScanner
            .getClasses(AnnotationScanner.INCLUDE_INTERFACES, true, new Class<?>[] { RemoteInterface.class })
            .keySet()) {
          out.println("<bean id=\"/remoting/" + className
              + "\" class=\"org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean\">");

          // We setup a custom scheme handled by the httpInvokerRequestExecutor
          // for both side authentication
          out.println(
              "  <property name=\"serviceUrl\" value=\"swaphttps://${remoting.server.name}:${remoting.server.port}/remoting/"
                  + className + "\"/>");
          out.println("  <property name=\"serviceInterface\" value =\"" + className + "\"/>");
          out.println("  <property name=\"httpInvokerRequestExecutor\" ref=\"remoting.httpInvokerRequestExecutor\"/>");
          out.println("</bean>");
        }
      }
      out.println("</beans>");
    }
    return new InputSource(new ByteArrayInputStream(baos.toByteArray()));
  }
}
