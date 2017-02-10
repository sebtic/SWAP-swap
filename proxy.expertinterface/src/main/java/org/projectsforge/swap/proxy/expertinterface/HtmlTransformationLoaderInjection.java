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
package org.projectsforge.swap.proxy.expertinterface;

import org.projectsforge.swap.core.environment.Environment;
import org.projectsforge.swap.core.handlers.Handler;
import org.projectsforge.swap.core.handlers.HandlerContext;
import org.projectsforge.swap.core.handlers.Resource;
import org.projectsforge.swap.core.http.Response;
import org.projectsforge.swap.core.mime.html.nodes.Document;
import org.projectsforge.swap.core.mime.html.nodes.elements.Attributes;
import org.projectsforge.swap.core.mime.html.nodes.elements.ElementFactory;
import org.projectsforge.swap.core.mime.html.nodes.elements.HEADElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.SCRIPTElement;
import org.projectsforge.swap.handlers.html.HtmlDomTransformation;
import org.projectsforge.swap.handlers.html.HtmlTransformation;
import org.projectsforge.swap.handlers.mime.StatisticsCollector;
import org.projectsforge.swap.proxy.webui.WebUIPropertyHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Injects into the Loader into the DOM
 * 
 * @author Vincent Rouillé
 */
@Handler(singleton = true)
public class HtmlTransformationLoaderInjection extends HtmlDomTransformation {

  private static final String TEXT_JAVASCRIPT = "text/javascript";

  /** The logger. */
  private final Logger logger = LoggerFactory
      .getLogger(HtmlTransformationLoaderInjection.class);

  /** The environment. */
  @Autowired
  private Environment environment;

  /** The element factory */
  @Autowired
  private ElementFactory elementFactory;

  @Override
  public boolean transform(HandlerContext<HtmlTransformation> context,
      StatisticsCollector statisticsCollector, Response response,
      Resource<Document> document) throws Exception {
    Document dom = document.get();

    // Insert communication script
    {
      HEADElement domHead = dom.getFirstChildrenToLeaves(HEADElement.class);
      if (domHead != null) {
        // Create script element
        {
          SCRIPTElement domScript = elementFactory
              .newElement(SCRIPTElement.class);
          domScript.setAttribute(Attributes.TYPE, TEXT_JAVASCRIPT);
          domScript.setAttribute(Attributes.SRC, "http://"
              + WebUIPropertyHolder.hostname.get() + "/" + ExpertInterfaceWeb.PACKAGE
              + "/core/Loader.js");
          domHead.addChildAtStart(domScript);
        }
        logger.trace("Script Element added to DOMHeadElement");
      } else {
        logger.warn("Unable to find DOMHeadElement in document");
      }
    }

    return true;
  }
}
