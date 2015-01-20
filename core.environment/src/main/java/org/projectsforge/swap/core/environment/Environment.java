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
package org.projectsforge.swap.core.environment;

import org.projectsforge.utils.path.PathRepository;
import org.slf4j.Logger;
import org.springframework.web.context.support.GenericWebApplicationContext;

/**
 * The execution environment.
 * 
 * @author Sébastien Aupetit
 */
public interface Environment {

  /**
   * Autowire bean.
   * 
   * @param <T> the generic type
   * @param instance the instance to autowire
   * @return the the autowired instance to use
   */
  <T> T autowireBean(final T instance);

  /**
   * Gets the allow user interaction.
   * 
   * @return the allow user interaction
   */
  boolean getAllowUserInteraction();

  /**
   * Gets the context.
   * 
   * @return the context
   */
  GenericWebApplicationContext getContext();

  /**
   * Gets the logger.
   * 
   * @return the logger
   */
  Logger getLogger();

  /**
   * Gets the name.
   * 
   * @return the name
   */
  String getName();

  /**
   * Gets the path repository.
   * 
   * @return the path repository
   */
  PathRepository getPathRepository();

  /**
   * Gets the starter monitor.
   * 
   * @return the starter monitor
   */
  StarterMonitor getStarterMonitor();

  /**
   * Save configuration properties.
   */
  void saveConfigurationProperties();

  /**
   * Stop.
   */
  void stop();

}
