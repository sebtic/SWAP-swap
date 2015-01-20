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
import org.projectsforge.swap.core.handlers.Resource;
import org.projectsforge.swap.core.http.Response;
import org.projectsforge.swap.core.mime.html.nodes.Document;
import org.projectsforge.swap.handlers.mime.StatisticsCollector;

/**
 * The base class of HTML DOM based transformation.
 * 
 * @author Sébastien Aupetit
 */
public abstract class HtmlDomTransformation implements HtmlTransformation {

  /** The resource name of the DOM of the document. */
  public static final String HTML_DOM = "html.dom";

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.handlers.html.HtmlTransformation#transform(org.
   * projectsforge.swap.core.handlers.HandlerContext,
   * org.projectsforge.swap.core.http.Response,
   * org.projectsforge.swap.core.timelimitedoperations.ProbeManager)
   */
  @Override
  public boolean transform(final HandlerContext<HtmlTransformation> context,
      final StatisticsCollector statisticsCollector, final Response response) throws Exception {
    final Resource<Document> document = context.waitForResource(HtmlDomTransformation.HTML_DOM);
    statisticsCollector.startTimer(StatisticsCollector.PROXY_HANDLER_HTML_AFTERDOM_TIMER_KEY
        + getClass().getCanonicalName());
    try {
      return transform(context, statisticsCollector, response, document);
    } finally {
      statisticsCollector.stopTimer(StatisticsCollector.PROXY_HANDLER_HTML_AFTERDOM_TIMER_KEY
          + getClass().getCanonicalName());
    }
  }

  /**
   * Transform the document.
   * 
   * @param context the context
   * @param statisticsCollector the object allowing to collect data across all
   *          the request processing
   * @param response the response
   * @param document the document
   * @return true, if the processing must continue
   * @throws Exception the exception
   */
  public abstract boolean transform(HandlerContext<HtmlTransformation> context,
      StatisticsCollector statisticsCollector, Response response, Resource<Document> document)
      throws Exception;

}
