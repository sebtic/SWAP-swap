/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr>
 * 
 * This file is part of SWAP.
 * 
 * SWAP is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * SWAP is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with SWAP. If not, see <http://www.gnu.org/licenses/>.
 * 
 * $Id$
 */
package org.projectsforge.swap.core.environment.impl;

import org.projectsforge.swap.core.environment.StarterMonitor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.core.Ordered;

/**
 * A bean processor used to monitor bean instantiations and notify a
 * MonitorStarter instance.
 * 
 * @author Sébastien Aupetit
 */
public class StarterMonitorBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter
    implements Ordered {

  /** The starter monitor. */
  private final StarterMonitor starterMonitor;

  /**
   * Instantiates a new starter monitor bean post processor.
   * 
   * @param starterMonitor
   *          the starter monitor
   */
  public StarterMonitorBeanPostProcessor(final StarterMonitor starterMonitor) {
    this.starterMonitor = starterMonitor;
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.core.Ordered#getOrder()
   */
  @Override
  public int getOrder() {
    return Ordered.HIGHEST_PRECEDENCE;
  }

  /*
   * (non-Javadoc)
   * @seeorg.springframework.beans.factory.config.
   * InstantiationAwareBeanPostProcessorAdapter
   * #postProcessAfterInitialization(java.lang.Object, java.lang.String)
   */
  @Override
  public Object postProcessAfterInitialization(final Object bean, final String beanName)
      throws BeansException {
    starterMonitor.updateStatus("Bean created: " + beanName);
    return super.postProcessAfterInitialization(bean, beanName);
  }
}
