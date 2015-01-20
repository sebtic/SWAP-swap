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
 * <http://www.gnu.org/licenses/>. $Id: ConfigProxyController.java 98 2011-11-24
 * 12:10:32Z sebtic $
 */
package org.projectsforge.swap.proxy.webui.proxy;

import org.projectsforge.swap.core.environment.Environment;
import org.projectsforge.swap.proxy.proxy.ProxyPropertyHolder;
import org.projectsforge.swap.proxy.webui.configuration.ConfigurationComponent;
import org.projectsforge.swap.proxy.webui.configuration.ConfigurationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * The web controller managing the configuration of the proxy server.
 * 
 * @author Sébastien Aupetit
 */
@Component
@Controller
@RequestMapping(ConfigProxyController.URL)
public class ConfigProxyController extends ConfigurationComponent {

  /** The environment. */
  @Autowired
  private Environment environment;

  /** The Constant URL. */
  public static final String URL = ConfigurationController.URL
      + "/org.projectsforge.swap.proxy.proxy";

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.web.config.ConfigurableDescriptor#getDescription
   * ()
   */
  @Override
  public String getDescription() {
    return "Configure the proxy component";
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.web.config.ConfigurableDescriptor#getName()
   */
  @Override
  public String getName() {
    return "Proxy";
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.web.config.ConfigurableDescriptor#getPriority()
   */
  @Override
  public int getPriority() {
    return Integer.MAX_VALUE;
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.web.config.ConfigurableDescriptor#getUrl()
   */
  @Override
  public String getUrl() {
    return ConfigProxyController.URL;
  }

  /**
   * The GET handler.
   * 
   * @return the model and view
   */
  @RequestMapping(value = "/", method = RequestMethod.GET)
  public ModelAndView handleGet() {
    final ModelAndView mav = new ModelAndView("org.projectsforge.swap.proxy.proxy/get");
    mav.addObject("minThreads", ProxyPropertyHolder.httpMinThread.get());
    mav.addObject("maxThreads", ProxyPropertyHolder.httpMaxThread.get());
    mav.addObject("rootline", getRootline());
    return mav;
  }

  /**
   * The POST handler.
   * 
   * @param minThreads the min threads
   * @param maxThreads the max threads
   * @return the model and view
   */
  @RequestMapping(value = "/", method = RequestMethod.POST)
  public ModelAndView handlePost(@RequestParam final int minThreads,
      @RequestParam final int maxThreads) {
    final ModelAndView mav = new ModelAndView("org.projectsforge.swap.proxy.proxy/get");

    ProxyPropertyHolder.httpMinThread.set(minThreads);
    ProxyPropertyHolder.httpMaxThread.set(maxThreads);
    environment.saveConfigurationProperties();

    mav.addObject("minThreads", ProxyPropertyHolder.httpMinThread.get());
    mav.addObject("maxThreads", ProxyPropertyHolder.httpMaxThread.get());
    mav.addObject("changed", true);
    mav.addObject("rootline", getRootline());
    return mav;
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.webui.AbstractMVCComponent#isActive()
   */
  @Override
  public boolean isActive() {
    return true;
  }
}
