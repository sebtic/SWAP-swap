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
import org.projectsforge.swap.core.http.Response;

/**
 * The Interface ResponseFilter. It is used to filter the response. By returning
 * false, the response is not transformed by subsequent {@link MimeHandler}. It
 * can also be used to do any preprocessing like URL logging.
 * 
 * @author Sébastien Aupetit
 */
public interface ResponseFilter {

  /**
   * Handle the response.
   * 
   * @param context the context
   * @param statisticsCollector the object allowing to collect data across all
   *          the request processing
   * @param response the response
   * @return true, if the handling can continue, false otherwise
   * @throws Exception the exception
   */
  boolean filter(HandlerContext<ResponseFilter> context, StatisticsCollector statisticsCollector,
      Response response) throws Exception;
}
