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

/**
 * The Interface HandlerExecutor used to delegate the execution of a handler.
 * 
 * @param <T> the generic type
 */
public interface HandlerExecutor<T> {

  /**
   * Execute.
   * 
   * @param context the context
   * @param handlerClass the handler class
   * @param handler the handler
   * @throws Exception the exception
   */
  void execute(HandlerContext<T> context, Class<?> handlerClass, T handler) throws Exception;

  /**
   * Shutdown.
   */
  void shutdown(HandlerContext<T> context, Class<?> handlerClass, T handler) throws Exception;
}
