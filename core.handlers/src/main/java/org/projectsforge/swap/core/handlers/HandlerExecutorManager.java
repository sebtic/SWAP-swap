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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The HandlerExecutorManager manages the execution of a handler.
 * 
 * @param <T> the generic type
 */
class HandlerExecutorManager<T> {

  /** The handler class. */
  private final Class<?> handlerClass;

  /** The handler instance. */
  private final T handlerInstance;

  /** The terminated. */
  private volatile boolean terminated = false;

  /** The exception. */
  private Exception exception;

  /** The lock. */
  private final ReentrantLock lock = new ReentrantLock();

  /** The state changed. */
  private final Condition stateChanged = lock.newCondition();

  /** The executor. */
  private final HandlerExecutor<T> executor;

  /** The context. */
  private final HandlerContext<T> context;

  /**
   * Instantiates a new handler executor.
   * 
   * @param context the context
   * @param handlerClass the handler class
   * @param handlerInstance the handler instance
   * @param executor the executor
   */
  HandlerExecutorManager(final HandlerContext<T> context, final Class<?> handlerClass, final T handlerInstance,
      final HandlerExecutor<T> executor) {
    this.context = context;
    this.handlerClass = handlerClass;
    this.handlerInstance = handlerInstance;
    this.executor = executor;
  }

  /**
   * Execute the handler.
   */
  void execute() {
    final long startTime = System.currentTimeMillis();
    try {
      executor.execute(context, handlerClass, handlerInstance);
      try {
        lock.lock();
        terminated = true;
        stateChanged.signalAll();
      } finally {
        lock.unlock();
      }
    } catch (final Exception e) {
      try {
        lock.lock();
        exception = e;
        context.getLogger().warn("An exception occurred while executing " + handlerClass.getCanonicalName(), e);
        stateChanged.signalAll();
      } finally {
        lock.unlock();
      }
    }
    final long endTime = System.currentTimeMillis();
    context.getLogger().info("{} executed in {}ms", handlerClass.getCanonicalName(), endTime - startTime);
  }

  /**
   * Gets the exception.
   * 
   * @return the exception
   */
  public Exception getException() {
    return exception;
  }

  /**
   * Gets the handler class.
   * 
   * @return the handler class
   */
  public Class<?> getHandlerClass() {
    return handlerClass;
  }

  /**
   * Checks if the handler is terminated.
   * 
   * @return true, if is terminated
   */
  public boolean isTerminated() {
    return terminated;
  }

  public void shutdown() throws Exception {
    executor.shutdown(context, handlerClass, handlerInstance);
  }

  @Override
  public String toString() {
    return handlerClass.getName();
  }

  /**
   * Wait for an handler to terminate or to throw an exception.
   * 
   * @return true, if the handler terminated without throwing an exception
   * @throws InterruptedException the interrupted exception
   * @throws HandlerException the handler exception
   */
  public boolean waitFor() throws InterruptedException, HandlerException {
    try {
      lock.lock();
      while (!terminated && exception == null) {
        stateChanged.await();
      }
      if (exception != null) {
        throw new HandlerException(exception);
      }
    } finally {
      lock.unlock();
    }
    return true;
  }

  /**
   * Wait for an handler to terminate within the given time limit or to throw an
   * exception..
   * 
   * @param timeout the timeout
   * @param timeUnit the time unit
   * @return true, if the handler terminated within the time limit and without
   *         throwing an exception
   * @throws InterruptedException the interrupted exception
   * @throws HandlerException the handler exception
   */
  public boolean waitFor(final long timeout, final TimeUnit timeUnit) throws InterruptedException, HandlerException {
    try {
      lock.lock();
      if (!terminated && exception == null) {
        if (!stateChanged.await(timeout, timeUnit)) {
          return false;
        } else {
          if (exception != null) {
            throw new HandlerException(exception);
          }
          return terminated;
        }
      }
      return true;
    } finally {
      lock.unlock();
    }
  }
}
