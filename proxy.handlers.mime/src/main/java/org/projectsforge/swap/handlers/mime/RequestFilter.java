/**
 * Copyright 2012 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr> This
 * software is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This software is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this software. If not, see
 * <http://www.gnu.org/licenses/>. $Id$
 */
package org.projectsforge.swap.handlers.mime;

import org.projectsforge.swap.core.handlers.HandlerContext;
import org.projectsforge.swap.core.http.Request;

/**
 * The Interface RequestFilter. It is used to block a request by returning false
 * or to rewrite the request using the {@link #OUTPUT_REQUEST} resource.
 * 
 * @author Sébastien Aupetit
 */
public interface RequestFilter {

  /** The Constant OUTPUT_REQUEST. */
  public static final String OUTPUT_REQUEST = "requestfilter.outputRequest";

  /**
   * Filter the request.
   * 
   * @param context the context
   * @param statisticsCollector the object allowing to collect data across all
   *          the request processing
   * @param request the request
   * @return true, if the processing must continue, false otherwise
   */
  boolean filter(HandlerContext<RequestFilter> context, StatisticsCollector statisticsCollector,
      Request request);

}
