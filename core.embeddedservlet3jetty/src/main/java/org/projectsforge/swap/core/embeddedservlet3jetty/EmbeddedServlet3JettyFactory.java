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
package org.projectsforge.swap.core.embeddedservlet3jetty;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.annotations.AnnotationParser;
import org.eclipse.jetty.annotations.ClassNameResolver;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.DiscoveredAnnotation;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.h2.server.web.WebServlet;
import org.projectsforge.swap.core.environment.Environment;
import org.projectsforge.swap.core.environment.impl.EnvironmentPropertyHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * A factory class for the set up of an Embedded servlet 3 jetty server.
 */
@Component
public class EmbeddedServlet3JettyFactory {

  /** The environment. */
  @Autowired
  Environment environment;

  /** The database path. */
  @Autowired
  @Value("${database.path}")
  private String databasePath;

  /**
   * Instantiates a new embedded servlet3 jetty factory.
   */
  private EmbeddedServlet3JettyFactory() {
  }

  /**
   * H2console servlet context handler.
   * 
   * @param virtualHost the virtual host
   * @return the servlet context handler
   * @throws FileNotFoundException the file not found exception
   */
  public ServletContextHandler h2consoleServletContextHandler(final String virtualHost) throws FileNotFoundException {
    final ServletContextHandler h2consoleServletContextHandler = new ServletContextHandler();
    if (virtualHost != null) {
      h2consoleServletContextHandler.setVirtualHosts(new String[] { virtualHost });
    }
    final ServletHolder h2consoleholder = new ServletHolder("h2 console", new WebServlet());
    h2consoleholder.setInitParameter("properties", EnvironmentPropertyHolder.configurationDirectory.get()
        .getAbsolutePath());
    h2consoleServletContextHandler.addServlet(h2consoleholder, "/h2console/*");

    try (final PrintWriter h2properties = new PrintWriter(new FileOutputStream(new File(
        EnvironmentPropertyHolder.configurationDirectory.get(), ".h2.server.properties")))) {
      h2properties.println("0=SWAP H2 (Embedded)|org.h2.Driver|jdbc\\:h2\\:"
          + EnvironmentPropertyHolder.configurationDirectory.get() + File.separator + databasePath + "|user");
    }

    return h2consoleServletContextHandler;
  }

  /**
   * New server.
   * 
   * @param handlers the handlers
   * @return the server
   */
  public Server newServer(final Handler[] handlers) {
    final Server server = new Server();

    final ContextHandlerCollection contexts = new ContextHandlerCollection();
    contexts.setHandlers(handlers);
    server.setHandler(contexts);

    for (final Handler handler : handlers) {
      if (handler instanceof ContextHandler) {
        ((ContextHandler) handler).getServletContext().setAttribute(
            WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, environment.getContext());
      }
    }

    return server;
  }

  /**
   * New web app context.
   * 
   * @param virtualHost the virtual host
   * @param webApplicationsInitializerClass the web applications initializer
   *          class
   * @return the web app context
   */
  public WebAppContext newWebAppContext(final String virtualHost, final Class<?>... webApplicationsInitializerClass) {
    final WebAppContext webAppContext = new WebAppContext();
    webAppContext.setConfigurations(new org.eclipse.jetty.webapp.Configuration[] { new AnnotationConfiguration() {
      @Override
      public void parseContainerPath(final WebAppContext context, final AnnotationParser parser) throws Exception {
        // clear any previously discovered annotations
        clearAnnotationList(parser.getAnnotationHandlers());

        final ClassNameResolver classNameResolver = new ClassNameResolver() {
          @Override
          public boolean isExcluded(final String name) {
            if (context.isSystemClass(name)) {
              return false;
            }
            if (context.isServerClass(name)) {
              return true;
            }

            return false;
          }

          @Override
          public boolean shouldOverride(final String name) {
            // looking at system classpath
            if (context.isParentLoaderPriority()) {
              return true;
            }
            return false;
          }
        };

        /*
         * final Set<URI> uris = environment.getPathRepository().getPaths(); for
         * (final URI uri : uris) { final Resource r =
         * Resource.newResource(uri); // parser.parse(Resource.newResource(uri),
         * classNameResolver); // } }
         */

        // Use class annotated with @SpringWebApplicationInitializer
        for (final Class<?> clazz : webApplicationsInitializerClass) {
          parser.parse(clazz, classNameResolver, true);
        }

        // gather together all annotations discovered
        final List<DiscoveredAnnotation> annotations = new ArrayList<DiscoveredAnnotation>();
        gatherAnnotations(annotations, parser.getAnnotationHandlers());

        context.getMetaData().addDiscoveredAnnotations(annotations);
      }

    }, new WebXmlConfiguration(), new MetaInfConfiguration(), new FragmentConfiguration(), new EnvConfiguration(),
        new PlusConfiguration(), new JettyWebXmlConfiguration()

    });
    if (virtualHost != null) {
      webAppContext.setVirtualHosts(new String[] { virtualHost });
    }
    return webAppContext;
  }
}
