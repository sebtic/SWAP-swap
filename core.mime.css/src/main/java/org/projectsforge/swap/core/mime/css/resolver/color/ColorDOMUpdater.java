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
 * <http://www.gnu.org/licenses/>. \$Id$
 */
package org.projectsforge.swap.core.mime.css.resolver.color;

import org.projectsforge.swap.core.mime.css.property.color.SRGBColor;
import org.projectsforge.swap.core.mime.css.property.color.dictionary.ColorDictionary;
import org.projectsforge.swap.core.mime.css.property.color.dictionary.ColorDictionaryEntry;
import org.projectsforge.swap.core.mime.css.property.color.dictionary.ColorRule;
import org.projectsforge.swap.core.mime.css.property.color.dictionary.ColorRule.ColorOrigin;
import org.projectsforge.swap.core.mime.css.resolver.CssRule;
import org.projectsforge.swap.core.mime.css.resolver.CssRule.Origin;
import org.projectsforge.swap.core.mime.html.nodes.Document;
import org.projectsforge.swap.core.mime.html.nodes.Text;
import org.projectsforge.swap.core.mime.html.nodes.elements.AbstractElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.Attributes;
import org.projectsforge.swap.core.mime.html.nodes.elements.ElementFactory;
import org.projectsforge.swap.core.mime.html.nodes.elements.HEADElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.HTMLElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.STYLEElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The Class ColorDOMUpdater which automate DOM updating subject to color
 * dictionary changes.
 * 
 * @author Sébastien Aupetit
 */
@Component
public class ColorDOMUpdater {

  /** The element factory. */
  @Autowired
  private ElementFactory elementFactory;

  /**
   * Update the document.
   * 
   * @param <TColor> the generic type
   * @param document the root node
   * @param colorDictionary the color dictionary
   */
  public <TColor extends SRGBColor> void update(final Document document,
      final ColorDictionary<TColor> colorDictionary) {
    // Write modified style to the document final
    final StringBuilder stylesheet = new StringBuilder();
    for (final ColorDictionaryEntry<TColor> entry : colorDictionary.getEntries()) {
      for (final ColorRule rule : entry.getRules()) {
        if (rule.getColorOrigin() == ColorOrigin.FOREGROUND) {
          final CssRule cssRule = rule.getRule();
          if (cssRule.getOrigin() == Origin.HTMLTAG_PROPERTY
              || cssRule.getOrigin() == Origin.HTMLTAG_STYLE_PROPERTY) {
            final AbstractElement element = cssRule.getElement();
            String style = element.getAttribute(Attributes.STYLE);
            if (style == null) {
              style = "";
            }
            style += ";color:" + entry.getColor();
            element.setAttribute(Attributes.STYLE, style);
          } else {
            stylesheet.append(cssRule.getSelector());
            stylesheet.append("{ color:").append(entry.getColor()).append("; }\n");
          }
        } else {
          final CssRule cssRule = rule.getRule();
          if (cssRule.getOrigin() == Origin.HTMLTAG_PROPERTY
              || cssRule.getOrigin() == Origin.HTMLTAG_STYLE_PROPERTY) {
            final AbstractElement element = cssRule.getElement();
            String style = element.getAttribute(Attributes.STYLE);
            if (style == null) {
              style = "";
            }
            style += ";background-color:" + entry.getColor();
            element.setAttribute(Attributes.STYLE, style);
          } else {
            stylesheet.append(cssRule.getSelector());
            stylesheet.append("{ background-color:").append(entry.getColor()).append("; }\n");
          }
        }
      }
    }
    if (stylesheet.length() != 0) {
      final STYLEElement styleElement = elementFactory.newElement(STYLEElement.class);
      styleElement.addChildAtEnd(new Text(stylesheet.toString()));
      styleElement.setAttribute(Attributes.TYPE, "text/css");
      HEADElement head = document.getFirstChildrenToLeaves(HEADElement.class);
      if (head == null) {
        head = elementFactory.newElement(HEADElement.class);
        HTMLElement html = document.getFirstChildrenToLeaves(HTMLElement.class);
        if (html == null) {
          html = elementFactory.newElement(HTMLElement.class);
          document.addChildAtEnd(html);
        }
        html.addChildAtEnd(head);
      }
      head.addChildAtEnd(styleElement);
    }
  }
}
