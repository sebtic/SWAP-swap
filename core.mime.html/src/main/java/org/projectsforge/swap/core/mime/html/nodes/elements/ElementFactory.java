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
 * <http://www.gnu.org/licenses/>. $Id: ElementFactory.java 84 2011-06-09
 * 10:09:03Z sebtic $
 */
package org.projectsforge.swap.core.mime.html.nodes.elements;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import javax.annotation.PostConstruct;
import org.projectsforge.swap.core.environment.Environment;
import org.projectsforge.utils.annotations.AnnotationScanner;
import org.projectsforge.utils.icasestring.ICaseString;
import org.projectsforge.utils.icasestring.ICaseStringKeyCollections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The HTML tag factory.
 */
@Component
public class ElementFactory {

  /** The Constant logger. */
  private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
      .getLogger(ElementFactory.class);

  /** The Constant registry. */
  private final Map<ICaseString, Class<? extends AbstractElement>> registry = ICaseStringKeyCollections
      .caseInsensitiveMap(new HashMap<ICaseString, Class<? extends AbstractElement>>());

  /** The environment. */
  @Autowired
  private Environment environment;

  /** The annotation scanner. */
  @Autowired
  private AnnotationScanner annotationScanner;

  /**
   * Inits the.
   */
  @PostConstruct
  void init() {
    final Map<String, Class<?>> classes = annotationScanner.getClasses(
        AnnotationScanner.INCLUDE_PLAIN_CLASSES, false, new Class<?>[] { HtmlTag.class });
    for (final Class<?> clazz : classes.values()) {
      try {
        final Field nameField = clazz.getDeclaredField("TAGNAME");
        final ICaseString tagName = ICaseString.class.cast(nameField.get(null));
        final Class<? extends AbstractElement> tagClass = clazz.asSubclass(AbstractElement.class);

        for (final Constructor<?> c : tagClass.getConstructors()) {
          if ((c.getModifiers() & Modifier.PUBLIC) != 0) {
            logger.warn("Public constructor found for HTML tag class {}", tagClass);
            throw new IllegalStateException("Public constructor found for HTML tag class "
                + tagClass);
          }
        }

        if (registry.put(tagName, tagClass) != null) {
          logger.warn("Duplicate class for HTML tag {}", tagName);
          throw new IllegalStateException("Duplicate class for HTML tag " + tagName);
        }
      } catch (final Exception e) {
        logger.error("An error occurred on class " + clazz.getName(), e);
        throw new IllegalStateException("An error occurred on class " + clazz.getName(), e);
      }
    }
    if (logger.isInfoEnabled()) {
      logger.info("Detected HTML tags: {}", new TreeSet<>(registry.keySet()));
    }
  }

  /**
   * New element.
   * 
   * @param <T> the generic type
   * @param clazz the clazz
   * @return the t
   */
  public <T extends AbstractElement> T newElement(final Class<T> clazz) {
    for (final Constructor<?> c : clazz.getConstructors()) {
      c.setAccessible(true);
    }
    try {
      return environment.autowireBean(clazz.newInstance());
    } catch (InstantiationException | IllegalAccessException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /**
   * New element.
   * 
   * @param name the name
   * @return the abstract element
   */
  public AbstractElement newElement(final ICaseString name) {
    final Class<? extends AbstractElement> theClass = registry.get(name);
    try {
      if (theClass == null) {
        logger.warn("Unknown tag found ({}). Using default element class", name);
        return environment.autowireBean(new DefaultElement(name));
      } else {
        for (final Constructor<?> c : theClass.getConstructors()) {
          c.setAccessible(true);
        }
        return environment.autowireBean(theClass.newInstance());
      }
    } catch (final Exception e) {
      logger.error("ElementFactory fails to instantiate HTML element", e);
      throw new IllegalStateException("Can not instantiate element of class " + name, e);
    }
  }
}
