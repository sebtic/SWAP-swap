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
 * <http://www.gnu.org/licenses/>. $Id: ConfigurationController.java 98
 * 2011-11-24 12:10:32Z sebtic $
 */
package org.projectsforge.swap.proxy.webui.configuration;

import org.projectsforge.swap.proxy.webui.WelcomeComponent;
import org.projectsforge.swap.proxy.webui.WelcomeController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * The web controller used to manage configuration controllers.
 */
@Controller
public class ConfigurationController extends WelcomeComponent {

  /** The Constant URL. */
  public static final String URL = WelcomeController.URL + "config";

  @Override
  public String getDescription() {
    return "Allows to configure the various components of the proxy.";
  }

  @Override
  public String getName() {
    return "Configuration";
  }

  @Override
  public int getPriority() {
    return Integer.MAX_VALUE;
  }

  @Override
  public String getUrl() {
    return ConfigurationController.URL;
  }

  /**
   * Config.
   * 
   * @return the model and view
   */
  @RequestMapping(ConfigurationController.URL)
  public ModelAndView handle() {
    if (!isActive()) {
      return getInactiveMAV();
    }
    final ModelAndView mav = new ModelAndView("config");
    mav.addObject("components", getComponents());
    mav.addObject("rootline", getRootline());
    return mav;
  }

  @Override
  public boolean isActive() {
    return true;
  }

  /**
   * Sets the components.
   * 
   * @param components the new components
   */
  @Autowired(required = false)
  public void setComponents(final ConfigurationComponent[] components) {
    super.setComponents(components);
  }

}
