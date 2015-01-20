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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The context class which manage resources access and the synchronization and
 * execution of tasks.
 * 
 * @param <T> the generic type
 * @author Sébastien Aupetit
 */
public class HandlerContext<T> implements AutoCloseable {

  /** The logger. */
  private final Logger logger;

  /** The handlers manager (shared by contexts). */
  @Autowired
  private HandlersManager handlersManager;

  /** The resources (shared by contexts). */
  @SuppressWarnings("rawtypes")
  private final Map<String, Resource> resources;

  /** The resources lock (shared by contexts). */
  private final ReentrantLock resourcesLock;

  /** The condition 'resources has changed' (shared by contexts). */
  private final Condition resourcesChanged;

  /** The handler base class (specific to a context). */
  private final Class<T> handlerBaseClass;

  /** The handlerExecutor (specific to a context). */
  private final HandlerExecutor<T> handlerExecutor;

  /** The handler informations (specific to a context). */
  @SuppressWarnings("rawtypes")
  private final Map<Class<?>, HandlerExecutorManager> handlerInfos = new ConcurrentHashMap<Class<?>, HandlerExecutorManager>();

  /** The filter. */
  private final HandlerFilter handlerFilter;

  /**
   * The Constructor.
   * 
   * @param parent the parent
   * @param handlerBaseClass the handler base class
   * @param handlerExecutor the handlerExecutor
   * @param handlerFilter the handler filter
   */
  @SuppressWarnings("unchecked")
  public HandlerContext(@SuppressWarnings("rawtypes") final HandlerContext parent, final Class<T> handlerBaseClass,
      final HandlerExecutor<T> handlerExecutor, final HandlerFilter handlerFilter) {
    this.resources = parent.resources;
    this.resourcesLock = parent.resourcesLock;
    this.resourcesChanged = parent.resourcesChanged;
    this.handlerBaseClass = handlerBaseClass;
    this.handlerExecutor = handlerExecutor;
    this.logger = parent.logger;
    this.handlerFilter = handlerFilter;
  }

  /**
   * The Constructor.
   * 
   * @param logger the logger
   * @param handlerBaseClass the handler base class
   * @param handlerExecutor the handlerExecutor
   * @param handlerFilter the handler filter
   */
  @SuppressWarnings("rawtypes")
  public HandlerContext(final Logger logger, final Class<T> handlerBaseClass, final HandlerExecutor<T> handlerExecutor,
      final HandlerFilter handlerFilter) {
    this.logger = logger;
    resources = new HashMap<String, Resource>();
    resourcesLock = new ReentrantLock();
    resourcesChanged = resourcesLock.newCondition();
    this.handlerBaseClass = handlerBaseClass;
    this.handlerExecutor = handlerExecutor;
    this.handlerFilter = handlerFilter;
  }

  /**
   * Adds the given resource to the context and signal threads waiting for
   * resources.
   * 
   * @param <TData> the generic type
   * @param resource the resource
   */
  public <TData> void addResource(final Resource<TData> resource) {
    resourcesLock.lock();
    try {
      if (resources.containsKey(resource.getName())) {
        throw new IllegalArgumentException("Resource already defined");
      } else {
        resources.put(resource.getName(), resource);
        resourcesChanged.signalAll();
      }
    } finally {
      resourcesLock.unlock();
    }
  }

  /**
   * Adds the given resource to the context if missing, and signal threads
   * waiting for resources.
   * 
   * @param <TData> the generic type
   * @param resource the resource
   */
  public <TData> void addResourceIfMissing(final Resource<TData> resource) {
    resourcesLock.lock();
    try {
      if (!resources.containsKey(resource.getName())) {
        resources.put(resource.getName(), resource);
        resourcesChanged.signalAll();
      }
    } finally {
      resourcesLock.unlock();
    }
  }

  @Override
  public void close() {
    for (final HandlerExecutorManager<?> hi : handlerInfos.values()) {
      try {
        hi.shutdown();
      } catch (final Exception ex) {
        logger.warn("An exception occured while closing HandlerContext", ex);
      }
    }
  }

  /**
   * Execute the handlers associated to the context.
   * 
   * @return true if not error occurred
   */
  public boolean execute() {
    boolean anExceptionOccurred = false;

    // Build handler list for the phase
    handlerInfos.clear();
    for (final Class<?> c : handlersManager.getHandlersByInterface(handlerBaseClass)) {
      if (handlerFilter == null || handlerFilter.accept(c)) {
        @SuppressWarnings("unchecked")
        final HandlerExecutorManager<T> hi = new HandlerExecutorManager<T>(this, c, (T) handlersManager.getInstance(c),
            handlerExecutor);
        handlerInfos.put(c, hi);
      }
    }
    final Set<Thread> threads = new HashSet<Thread>();

    // create threads
    for (final HandlerExecutorManager<?> hi : handlerInfos.values()) {
      final Thread thread = new Thread() {
        @Override
        public void run() {
          try {
            hi.execute();
          } finally {
            // free locks on resources owned by this threads
            resourcesLock.lock();
            try {
              for (final Resource<?> resource : resources.values()) {
                resource.readWriteLock.unlockAllPendingLocks(this);
              }
            } finally {
              resourcesLock.unlock();
            }
          }
          if (hi.getException() != null) {
            // on error, we interrupt all threads to remove never ending locking
            for (final Thread t : threads) {
              t.interrupt();
            }
          }
        }
      };
      thread.setName(logger.getName() + " " + hi);
      threads.add(thread);
    }

    // start all threads of the phase
    for (final Thread thread : threads) {
      thread.start();
    }

    // join threads
    boolean allDone = false;
    while (!allDone) {
      try {
        for (final Thread thread : threads) {
          thread.join();
        }
        allDone = true;
      } catch (final InterruptedException e) {
        for (final Thread thread : threads) {
          thread.interrupt();
        }
      }
    }

    // handle error

    for (final HandlerExecutorManager<?> hi : handlerInfos.values()) {
      if (hi.getException() != null) {
        anExceptionOccurred = true;
        if (logger.isWarnEnabled()) {
          logger.warn("Execution of " + hi.getHandlerClass().getName() + " thrown an exception", hi.getException());
        }
      }
    }

    // check for active lock on resource and log an error for active lock
    // from dead threads
    resourcesLock.lock();
    try {
      for (final Resource<?> resource : resources.values()) {
        resource.readWriteLock.unlockAllPendingLocks(threads);
      }
    } finally {
      resourcesLock.unlock();
    }

    return !anExceptionOccurred;
  }

  /**
   * Gets the logger.
   * 
   * @return the logger
   */
  public Logger getLogger() {
    return logger;
  }

  /**
   * Gets the resource.
   * 
   * @param <TData> the generic type
   * @param name the name
   * @param dataClass the data class
   * @return the resource
   */
  @SuppressWarnings("unchecked")
  public <TData> Resource<TData> getResource(final String name, final Class<TData> dataClass) {
    resourcesLock.lock();
    try {
      return resources.get(name);
    } finally {
      resourcesLock.unlock();
    }
  }

  /**
   * Removes the resource.
   * 
   * @param <TData> the generic type
   * @param name the name
   */
  public <TData> void removeResource(final String name) {
    resourcesLock.lock();
    try {
      if (!resources.containsKey(name)) {
        throw new IllegalArgumentException("Resource already defined");
      } else {
        resources.remove(name);
        resourcesChanged.signalAll();
      }
    } finally {
      resourcesLock.unlock();
    }
  }

  /**
   * Wait for handler.
   * 
   * @param clazz the clazz
   * @return true, if wait for handler
   * @throws InterruptedException the interrupted exception
   * @throws HandlerException the handler exception
   */
  public boolean waitForHandler(final Class<?> clazz) throws InterruptedException, HandlerException {
    @SuppressWarnings("rawtypes")
    final HandlerExecutorManager hi = handlerInfos.get(clazz);
    if (hi == null) {
      return false;
    } else {
      hi.waitFor();
      return true;
    }
  }

  /**
   * Wait for handler.
   * 
   * @param clazz the clazz
   * @param timeout the timeout
   * @param timeUnit the time unit
   * @return true, if wait for handler
   * @throws InterruptedException the interrupted exception
   * @throws HandlerException the handler exception
   */
  public boolean waitForHandler(final Class<?> clazz, final long timeout, final TimeUnit timeUnit)
      throws InterruptedException, HandlerException {
    @SuppressWarnings("rawtypes")
    final HandlerExecutorManager hi = handlerInfos.get(clazz);
    if (hi == null) {
      return false;
    } else {
      return hi.waitFor(timeout, timeUnit);
    }
  }

  /**
   * Wait for handlers.
   * 
   * @param clazz the clazz
   * @throws InterruptedException the interrupted exception
   * @throws HandlerException the handler exception
   */
  public void waitForHandlers(final Class<?> clazz) throws InterruptedException, HandlerException {
    for (@SuppressWarnings("rawtypes")
    final Entry<Class<?>, HandlerExecutorManager> entry : handlerInfos.entrySet()) {
      if (clazz.isAssignableFrom(entry.getKey())) {
        entry.getValue().waitFor();
      }
    }
  }

  /**
   * Wait for resource.
   * 
   * @param <TData> the generic type
   * @param name the name
   * @return the resource< t data>
   * @throws InterruptedException the interrupted exception
   */
  @SuppressWarnings("unchecked")
  public <TData> Resource<TData> waitForResource(final String name) throws InterruptedException {
    resourcesLock.lock();
    try {
      while (!resources.containsKey(name)) {
        resourcesChanged.await();
      }
      return resources.get(name);
    } finally {
      resourcesLock.unlock();
    }
  }

  /**
   * Wait for resource.
   * 
   * @param <TData> the generic type
   * @param name the name
   * @param timeout the timeout
   * @param timeUnit the time unit
   * @return the resource< t data>
   * @throws InterruptedException the interrupted exception
   */
  @SuppressWarnings("unchecked")
  public <TData> Resource<TData> waitForResource(final String name, final long timeout, final TimeUnit timeUnit)
      throws InterruptedException {
    resourcesLock.lock();
    try {
      if (!resources.containsKey(name)) {
        resourcesChanged.await(timeout, timeUnit);
      }
      return resources.get(name);
    } finally {
      resourcesLock.unlock();
    }
  }

}
