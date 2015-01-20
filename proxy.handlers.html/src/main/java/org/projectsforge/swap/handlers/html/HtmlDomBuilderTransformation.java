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

import java.io.InputStream;
import java.util.ArrayList;
import org.projectsforge.swap.core.environment.Environment;
import org.projectsforge.swap.core.handlers.Handler;
import org.projectsforge.swap.core.handlers.HandlerContext;
import org.projectsforge.swap.core.handlers.Resource;
import org.projectsforge.swap.core.http.Response;
import org.projectsforge.swap.core.mime.html.nodes.Document;
import org.projectsforge.swap.core.mime.html.nodes.Node;
import org.projectsforge.swap.core.mime.html.nodes.elements.ElementFactory;
import org.projectsforge.swap.core.mime.html.nodes.elements.HEADElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.HTMLElement;
import org.projectsforge.swap.core.mime.html.parser.HtmlParser;
import org.projectsforge.swap.handlers.mime.StatisticsCollector;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A HTML transformation which handle DOM parsing after a document encoding has
 * been detected.
 * 
 * @author Sébastien Aupetit
 */
@Handler(singleton = true)
class HtmlDomBuilderTransformation implements HtmlTransformation {

  /** The environment. */
  @Autowired
  private Environment environment;

  @Autowired
  private ElementFactory elementFactory;

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
    final Resource<String> encoding = context
        .waitForResource(HtmlEncodingDetectorTransformation.HTML_ENCODING);

    final HtmlParser htmlParser = environment.autowireBean(new HtmlParser());
    final Document rootNode;
    try (final InputStream in = response.getContent().getInputStream()) {
      rootNode = htmlParser.parse(encoding.get(), in);
    }

    // merge head tags
    final HTMLElement html = rootNode.getFirstChild(HTMLElement.class);
    HEADElement head = html.getFirstChild(HEADElement.class);
    if (head == null) {
      head = elementFactory.newElement(HEADElement.class);
      html.addChildAtStart(head);
    }

    HEADElement next = head.getNext(HEADElement.class);
    while (next != null) {
      next.detachFromParent();
      for (final Node node : new ArrayList<>(next.getChildrenCollection())) {
        node.detachFromParent();
        head.addChildAtEnd(node);
      }

      next = head.getNext(HEADElement.class);
    }

    context.addResource(new Resource<Document>(HtmlDomTransformation.HTML_DOM, rootNode));

    return true;
  }

}
