/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr>
 * 
 * This file is part of SHS.
 * 
 * SWAP is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * SWAP P is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with SHS. If not, see <http://www.gnu.org/licenses/>.
 * 
 * $Id$
 */
package org.projectsforge.swap.core.webui.velocity;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;
import org.apache.velocity.tools.ToolContext;
import org.apache.velocity.tools.ToolManager;
import org.springframework.web.servlet.view.velocity.VelocityView;

/**
 * The class which replace VelocityView in order to allow the use of velocity
 * tools.
 */
public class VelocityToolsView extends VelocityView {

  /** The tool context. */
  private static ToolContext toolContext = VelocityToolsView.initVelocityToolContext();

  /**
   * Inits the velocity tool context.
   * 
   * @return the tool context
   */
  private static ToolContext initVelocityToolContext() {
    if (VelocityToolsView.toolContext == null) {
      final ToolManager velocityToolManager = new ToolManager();
      velocityToolManager.configure("servlets/velocity/velocity-tools.xml");
      VelocityToolsView.toolContext = velocityToolManager.createContext();
    }
    return VelocityToolsView.toolContext;
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.web.servlet.view.velocity.VelocityView#
   * createVelocityContext(java.util.Map, javax.servlet.http.HttpServletRequest,
   * javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected Context createVelocityContext(final Map<String, Object> model,
      final HttpServletRequest request, final HttpServletResponse response) {
    final VelocityContext context = new VelocityContext(VelocityToolsView.toolContext);
    if (model != null) {
      for (final Map.Entry<String, Object> entry : model.entrySet()) {
        context.put(entry.getKey(), entry.getValue());
      }
    }
    return context;
  }
}
