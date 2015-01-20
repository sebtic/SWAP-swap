/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr> This file
 * is part of SHS. SWAP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version. SWAP P is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with SHS. If not, see
 * <http://www.gnu.org/licenses/>. $Id: ConfigCacheManagerController.java 125
 * 2012-04-04 14:59:59Z sebtic $
 */
package org.projectsforge.swap.util.http.webui;

import org.projectsforge.swap.core.environment.Environment;
import org.projectsforge.swap.core.environment.impl.TemporaryStreamPropertyHolder;
import org.projectsforge.swap.core.http.CacheManagerPropertyHolder;
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
 * The web controller which manage cache manager configuration.
 * 
 * @author Sébastien Aupetit
 */
@Component
@Controller
@RequestMapping(ConfigCacheManagerController.URL)
public class ConfigCacheManagerController extends ConfigurationComponent {

  /** The environment. */
  @Autowired
  private Environment environment;

  /** The Constant URL. */
  public static final String URL = ConfigurationController.URL
      + "/org.projectsforge.swap.core.http.cache";

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.web.config.ConfigurableDescriptor#getDescription
   * ()
   */
  @Override
  public String getDescription() {
    return "Configure the cache manager";
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.web.config.ConfigurableDescriptor#getName()
   */
  @Override
  public String getName() {
    return "Cache management";
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.web.config.ConfigurableDescriptor#getPriority()
   */
  @Override
  public int getPriority() {
    return Integer.MAX_VALUE - 1;
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.web.config.ConfigurableDescriptor#getUrl()
   */
  @Override
  public String getUrl() {
    return ConfigCacheManagerController.URL;
  }

  /**
   * The GET handler.
   * 
   * @return the model and view
   */
  @RequestMapping(value = "/", method = RequestMethod.GET)
  public ModelAndView handleGet() {
    final ModelAndView mav = new ModelAndView("org.projectsforge.swap.core.http.cache/config");
    mav.addObject("maxSizeOfInMemoryStoredContent",
        TemporaryStreamPropertyHolder.inMemoryMaxSize.get());
    mav.addObject("maxAgeBeforeForcedRefresh",
        CacheManagerPropertyHolder.maxAgeBeforeForcedRefresh.get());
    mav.addObject("maxAgeForForcedExpiration",
        CacheManagerPropertyHolder.maxAgeForForcedExpiration.get());
    mav.addObject("rootline", getRootline());
    return mav;
  }

  /**
   * The POST handler.
   * 
   * @param maxSizeOfInMemoryStoredContent the max size of in memory stored
   *          content
   * @param maxAgeBeforeForcedRefresh the max age before forced refresh
   * @param maxAgeForForcedExpiration the max age for forced expiration
   * @param cleanerThreadSleepTime the cleaner thread sleep time
   * @return the model and view
   */
  @RequestMapping(value = "/", method = RequestMethod.POST)
  public ModelAndView handlePost(@RequestParam final int maxSizeOfInMemoryStoredContent,
      @RequestParam final int maxAgeBeforeForcedRefresh,
      @RequestParam final int maxAgeForForcedExpiration) {
    final ModelAndView mav = new ModelAndView("org.projectsforge.swap.core.http.cache/config");

    TemporaryStreamPropertyHolder.inMemoryMaxSize.set(maxSizeOfInMemoryStoredContent);
    CacheManagerPropertyHolder.maxAgeBeforeForcedRefresh.set(maxAgeBeforeForcedRefresh);
    CacheManagerPropertyHolder.maxAgeForForcedExpiration.set(maxAgeForForcedExpiration);
    environment.saveConfigurationProperties();

    mav.addObject("maxSizeOfInMemoryStoredContent",
        TemporaryStreamPropertyHolder.inMemoryMaxSize.get());
    mav.addObject("maxAgeBeforeForcedRefresh",
        CacheManagerPropertyHolder.maxAgeBeforeForcedRefresh.get());
    mav.addObject("maxAgeForForcedExpiration",
        CacheManagerPropertyHolder.maxAgeForForcedExpiration.get());
    mav.addObject("rootline", getRootline());
    mav.addObject("changed", true);

    mav.addObject("rootline", getRootline());
    return mav;
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.web.mvc.AbstractMVCComponent#isActive()
   */
  @Override
  public boolean isActive() {
    return true;
  }
}
