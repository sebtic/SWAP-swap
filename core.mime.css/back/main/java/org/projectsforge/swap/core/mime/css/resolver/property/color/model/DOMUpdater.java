/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr>
 * 
 * This file is part of SWAP.
 * 
 * SWAP is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * SWAP is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with SWAP. If not, see <http://www.gnu.org/licenses/>.
 * 
 * \$Id$
 */
package org.projectsforge.swap.core.mime.css.resolver.color.model;

import java.util.ArrayList;
import java.util.List;
import org.projectsforge.swap.core.mime.css.resolver.color.model.ColorRule.ColorOrigin;
import org.projectsforge.swap.core.mime.css.resolver.ruleset.CssRule;
import org.projectsforge.swap.core.mime.css.resolver.ruleset.CssRule.Origin;
import org.projectsforge.swap.core.mime.html.nodes.Document;
import org.projectsforge.swap.core.mime.html.nodes.Node;
import org.projectsforge.swap.core.mime.html.nodes.Text;
import org.projectsforge.swap.core.mime.html.nodes.elements.AbstractElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.Attributes;
import org.projectsforge.swap.core.mime.html.nodes.elements.HEADElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.LINKElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.STYLEElement;

/**
 * The Class DOMUpdater which automatize DOM updating subject to color
 * dictionary changes.
 */
public class DOMUpdater {

  /**
   * Update the document.
   * 
   * @param rootNode
   *          the root node
   * @param colorDictionary
   *          the color dictionary
   */
  public void update(final Document rootNode, final ColorDictionary colorDictionary) {
    // Write modified style to the document final
    final StringBuilder stylesheet = new StringBuilder();
    for (final ColorDictionary.Entry entry : colorDictionary.getEntries()) {
      for (final ColorRule rule : entry.getRules()) {
        if (rule.getColorOrigin() == ColorOrigin.FOREGROUND) {
          final CssRule cssRule = rule.getRule();
          if (cssRule.getOrigin() == Origin.HTMLTAG || cssRule.getOrigin() == Origin.AUTHOR_STYLE) {
            final AbstractElement element = cssRule.getElement();
            String style = element.getAttributes().get(Attributes.STYLE);
            if (style == null) {
              style = "";
            }
            style += ";color:" + entry.getColor();
            element.getAttributes().put(Attributes.STYLE, style);
          } else {
            stylesheet.append(cssRule.getSelector());
            stylesheet.append("{ color:").append(entry.getColor()).append("; }\n");
          }
        } else {
          final CssRule cssRule = rule.getRule();
          if (cssRule.getOrigin() == Origin.HTMLTAG || cssRule.getOrigin() == Origin.AUTHOR_STYLE) {
            final AbstractElement element = cssRule.getElement();
            String style = element.getAttributes().get(Attributes.STYLE);
            if (style == null) {
              style = "";
            }
            style += ";background-color:" + entry.getColor();
            element.getAttributes().put(Attributes.STYLE, style);
          } else {
            stylesheet.append(cssRule.getSelector());
            stylesheet.append("{ background-color:").append(entry.getColor()).append("; }\n");
          }
        }
      }
    }
    final STYLEElement styleElement = new STYLEElement();
    styleElement.getChildren().add(new Text(stylesheet.toString()));
    styleElement.getAttributes().put(Attributes.TYPE, "text/css");
    final List<HEADElement> heads = new ArrayList<HEADElement>();
    rootNode.getChildren(HEADElement.class, heads);
    final HEADElement htmlElement = heads.get(0);
    Node last = null;
    // add style tag after the last style information <link> or <style>
    // in
    // case some javascript needs stylesheet to be defined
    for (final Node element : htmlElement.getChildren()) {
      if (element instanceof LINKElement || element instanceof STYLEElement) {
        last = element;
      }
    }
    htmlElement.addAfter(last, styleElement);

  }
}
