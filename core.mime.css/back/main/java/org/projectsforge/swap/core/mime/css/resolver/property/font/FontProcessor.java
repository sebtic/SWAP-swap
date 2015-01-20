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
 * $Id$
 */
package org.projectsforge.swap.core.mime.css.resolver.font;

import java.util.ArrayList;
import java.util.List;
import org.projectsforge.swap.core.mime.css.CssProperties;
import org.projectsforge.swap.core.mime.css.resolver.Properties;
import org.projectsforge.swap.core.mime.css.resolver.State;
import org.projectsforge.swap.core.mime.css.resolver.PropertyResolver.Processor;
import org.projectsforge.swap.core.mime.css.resolver.ruleset.CssRule;
import org.projectsforge.swap.core.mime.css.resolver.ruleset.CssRuleSet;
import org.projectsforge.swap.core.mime.html.nodes.Document;
import org.projectsforge.swap.core.mime.html.nodes.elements.AbstractElement;

/**
 * The Class FontProcessor.
 */
public class FontProcessor implements Processor {

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.mime.css.resolver.PropertyResolver.Processor
   * #process(org.projectsforge.swap.core.mime.html.nodes.Document, java.util.Deque,
   * java.util.Deque, java.util.Deque,
   * org.projectsforge.swap.core.mime.css.resolver.ruleset.CssRuleSet)
   */
  @Override
  public void process(final Document document, final ArrayList<AbstractElement> elements,
      final ArrayList<State> states, final ArrayList<Properties> properties,
      final CssRuleSet ruleSet) {

    // NB: <i>, <b>, <u>, <em> are defined by style in default stylesheet so no
    // need for special handling of it here

    final Font font = new Font();

    final Properties prop = properties.get(properties.size() - 1);
    Font parentFont = (properties.size() >= 2) ? (Font) properties.get(properties.size() - 2).get(
        Properties.CSS_FONT) : null;
    if (parentFont == null) {
      parentFont = new Font();
      if (properties.size() >= 2) {
        properties.get(properties.size() - 2).put(Properties.CSS_FONT, parentFont);
      }
    }

    final List<CssRule> rules = ruleSet.getRules();
    for (int i = 0; i < rules.size(); ++i) {
      if (rules.get(i).getRule().getName().equals(CssProperties.LINE_HEIGHT)) {
        font.parseLineHeight(rules.get(i).getRule().getValue(), parentFont);
      } else if (rules.get(i).getRule().getName().equals(CssProperties.FONT)) {
        font.parseFont(rules.get(i).getRule().getValue(), parentFont);
      } else if (rules.get(i).getRule().getName().equals(CssProperties.FONT_FAMILY)) {
        font.parseFamily(rules.get(i).getRule().getValue(), parentFont);
      } else if (rules.get(i).getRule().getName().equals(CssProperties.FONT_SIZE)) {
        font.parseSize(rules.get(i).getRule().getValue(), parentFont);
      } else if (rules.get(i).getRule().getName().equals(CssProperties.FONT_STYLE)) {
        font.parseStyle(rules.get(i).getRule().getValue(), parentFont);
      } else if (rules.get(i).getRule().getName().equals(CssProperties.FONT_VARIANT)) {
        font.parseVariant(rules.get(i).getRule().getValue(), parentFont);
      } else if (rules.get(i).getRule().getName().equals(CssProperties.FONT_WEIGHT)) {
        font.parseWeight(rules.get(i).getRule().getValue(), parentFont);
      }
    }

    prop.put(Properties.CSS_FONT, font);
  }

}
