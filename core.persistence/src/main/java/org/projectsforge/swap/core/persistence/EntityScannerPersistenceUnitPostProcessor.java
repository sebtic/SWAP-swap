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
 * <http://www.gnu.org/licenses/>. $Id:
 * EntityScannerPersistenceUnitPostProcessor.java 9 2010-03-08 22:04:08Z sebtic
 * $
 */
package org.projectsforge.swap.core.persistence;

import org.projectsforge.swap.core.environment.StarterMonitor;
import org.projectsforge.utils.annotations.AnnotationScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;
import org.springframework.stereotype.Component;

/**
 * A spring PersistenceUnitPostProcessor used to detect hibernate annotations.
 * 
 * @author Sébastien Aupetit
 */
@Component
public class EntityScannerPersistenceUnitPostProcessor implements PersistenceUnitPostProcessor {

  private final Logger logger = LoggerFactory
      .getLogger(EntityScannerPersistenceUnitPostProcessor.class);

  /** The annotation scanner. */
  @Autowired
  private AnnotationScanner annotationScanner;

  /** The starter monitor. */
  @Autowired
  StarterMonitor starterMonitor;

  /** The hibernate annotations class to detect. */
  @SuppressWarnings("deprecation")
  public static final Class<?>[] HIBERNATE_ANNOTATIONS = new Class<?>[] {
      javax.persistence.Entity.class, javax.persistence.Embeddable.class,
      javax.persistence.MappedSuperclass.class, org.hibernate.annotations.Entity.class };

  /*
   * (non-Javadoc)
   * @see
   * org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor
   * #postProcessPersistenceUnitInfo
   * (org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo)
   */
  @Override
  public void postProcessPersistenceUnitInfo(final MutablePersistenceUnitInfo pui) {
    starterMonitor.nextStep("Registering annotated hibernate classes");

    for (final String name : annotationScanner.getClasses(AnnotationScanner.INCLUDE_PLAIN_CLASSES,
        false, EntityScannerPersistenceUnitPostProcessor.HIBERNATE_ANNOTATIONS).keySet()) {
      starterMonitor.updateStatus("Registering annotated hibernate class: " + name);
      logger.info("Detected hibernate class {}", name);
      pui.addManagedClassName(name);
    }
  }
}
