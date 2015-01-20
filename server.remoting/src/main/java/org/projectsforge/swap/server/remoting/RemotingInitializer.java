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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.springframework.core.annotation.Order;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * The Class RemotingInitializer.
 * 
 * @author Sébastien Aupetit
 */
@Order(0)
public class RemotingInitializer implements WebApplicationInitializer {

  /* (non-Javadoc)
   * @see org.springframework.web.WebApplicationInitializer#onStartup(javax.servlet.ServletContext)
   */
  @Override
  public void onStartup(final ServletContext servletContext) throws ServletException {
    final AnnotationConfigWebApplicationContext acwac = new AnnotationConfigWebApplicationContext();
    acwac.setServletContext(servletContext);
    acwac.register(RemotingConfiguration.class);

    final ServletRegistration.Dynamic dispatcher = servletContext.addServlet("remoting",
        new DispatcherServlet(acwac));
    dispatcher.setLoadOnStartup(1);
    dispatcher.addMapping("/");
    dispatcher.setInitParameter("gzip", "true");
  }

}
