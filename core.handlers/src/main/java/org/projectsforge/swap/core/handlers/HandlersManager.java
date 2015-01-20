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
package org.projectsforge.swap.core.handlers;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.projectsforge.swap.core.environment.Environment;
import org.projectsforge.utils.annotations.AnnotationScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The HandlersManager manage the discovery and instantiation of handlers.
 */
@Component
public class HandlersManager {

  /** The logger. */
  private final Logger logger = LoggerFactory.getLogger(HandlersManager.class);

  /** The handlers. */
  private final Map<String, Class<?>> handlers = new TreeMap<String, Class<?>>();

  /** The handlers by interfaces. */
  private final Map<Class<?>, Set<Class<?>>> handlersByInterfaces = new HashMap<Class<?>, Set<Class<?>>>();

  /** The singletons. */
  private final Map<Class<?>, Object> singletons = new HashMap<Class<?>, Object>();

  /** The disabled handlers. */
  private final Set<String> disabledHandlers = new HashSet<String>();

  /** The annotation scanner. */
  @Autowired
  private AnnotationScanner annotationScanner;

  /** The environment. */
  @Autowired
  private Environment environment;

  @PreDestroy
  private void destroy() {
    for (final Object singleton : singletons.values()) {
      if (singleton != null) {
        for (final Method method : singleton.getClass().getMethods()) {
          if (method.getAnnotation(PreDestroy.class) != null && method.getParameterTypes().length == 0) {
            try {
              method.invoke(singleton, new Object[] {});
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
              logger.warn("An error occurred while calling " + method, e);
            }
          }
        }
      }
    }
  }

  /**
   * Disable handler.
   * 
   * @param handler the handler
   */
  public synchronized void disableHandler(final String handler) {
    if (disabledHandlers.add(handler)) {
      String result = "";
      boolean first = true;
      for (final String c : disabledHandlers) {
        if (first) {
          result = c;
          first = false;
        } else {
          result = result + ";" + c;
        }
      }
      HandlersPropertyHolder.disabledHandlers.set(result);
      environment.saveConfigurationProperties();
      handlersByInterfaces.clear();
    }
  }

  /**
   * Enable handler.
   * 
   * @param handler the handler
   */
  public synchronized void enableHandler(final String handler) {
    if (disabledHandlers.remove(handler)) {
      String result = "";
      boolean first = true;
      for (final String c : disabledHandlers) {
        if (first) {
          result = c;
          first = false;
        } else {
          result = result + ";" + c;
        }
      }
      HandlersPropertyHolder.disabledHandlers.set(result);
      environment.saveConfigurationProperties();
      handlersByInterfaces.clear();
    }
    handlersByInterfaces.clear();
  }

  /**
   * Gets the handlers by interface.
   * 
   * @param interfaceClass the interface class
   * @return the handlers by interface
   */
  public synchronized Set<Class<?>> getHandlersByInterface(final Class<?> interfaceClass) {
    Set<Class<?>> result = handlersByInterfaces.get(interfaceClass);
    if (result == null) {
      result = new HashSet<Class<?>>();

      for (final Entry<String, Class<?>> handler : handlers.entrySet()) {
        final Handler annotation = handler.getValue().getAnnotation(Handler.class);
        if (annotation != null && interfaceClass.isAssignableFrom(handler.getValue())) {
          if (!disabledHandlers.contains(handler.getKey())) {
            result.add(handler.getValue());
          }
        }
      }

      handlersByInterfaces.put(interfaceClass, result);
    }
    return result;
  }

  /**
   * Gets the instance.
   * 
   * @param <T> the generic type
   * @param handler the handler
   * @return the instance
   */
  @SuppressWarnings("unchecked")
  public synchronized <T> T getInstance(final Class<T> handler) {
    T result = (T) singletons.get(handler);
    if (result == null) {
      try {
        final Constructor<T> defaultConstructor = handler.getDeclaredConstructor();
        defaultConstructor.setAccessible(true); // set visibility to public
        result = defaultConstructor.newInstance();
      } catch (final InstantiationException | IllegalAccessException | SecurityException | NoSuchMethodException
          | IllegalArgumentException | InvocationTargetException e) {
        logger.error("Can not create instance", e);
        throw new IllegalArgumentException(e);
      }
      result = environment.autowireBean(result);
      final Handler annotation = handler.getAnnotation(Handler.class);
      if (annotation.singleton()) {
        logger.info("Singleton handler for {} created", handler.getCanonicalName());
        singletons.put(handler, result);
      }
    }
    return result;
  }

  /**
   * Initializes the component.
   */
  @PostConstruct
  public void init() {
    final Map<String, Class<?>> classes = annotationScanner.getClasses(AnnotationScanner.INCLUDE_PLAIN_CLASSES, false,
        new Class<?>[] { Handler.class });
    for (final Entry<String, Class<?>> entry : classes.entrySet()) {
      handlers.put(entry.getKey(), entry.getValue());
    }

    final StringTokenizer tokenizer = new StringTokenizer(HandlersPropertyHolder.disabledHandlers.get(), ";");
    while (tokenizer.hasMoreTokens()) {
      final String name = tokenizer.nextToken().trim();
      if (!name.isEmpty() && classes.containsKey(name)) {
        disabledHandlers.add(name);
      }
    }

    if (logger.isInfoEnabled()) {
      for (final String c : handlers.keySet()) {
        logger.info("Detected handler {} (enabled:{})", c, !disabledHandlers.contains(c));
      }
    }
  }
}
