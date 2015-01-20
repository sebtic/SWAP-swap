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
package org.projectsforge.swap.handlers.html;

import org.projectsforge.swap.core.handlers.HandlerContext;
import org.projectsforge.swap.core.http.Response;
import org.projectsforge.swap.handlers.mime.StatisticsCollector;

/**
 * The Interface HtmlTransformation for HTML document transformation and
 * filtering.
 * 
 * @author Sébastien Aupetit
 */
public interface HtmlTransformation {

  /**
   * Transform.
   * 
   * @param context the context
   * @param statisticsCollector the object allowing to collect data across all
   *          the request processing
   * @param response the response
   * @return true, if the transformation can continue
   * @throws Exception the exception
   */
  boolean transform(HandlerContext<HtmlTransformation> context,
      StatisticsCollector statisticsCollector, Response response) throws Exception;

}
