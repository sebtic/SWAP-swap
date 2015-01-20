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
 * <http://www.gnu.org/licenses/>. $Id: ColorProcessor.java 91 2011-07-18
 * 16:28:31Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.resolver.color;

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
import org.projectsforge.utils.icasestring.ICaseString;
import org.w3c.css.sac.LexicalUnit;

/**
 * The {@link Processor} that resolves foreground colors.
 * 
 * @author Sébastien Aupetit
 */
public class ColorProcessor implements Processor {

  @Override
  public void process(final Document document, final ArrayList<AbstractElement> elements,
      final ArrayList<State> states, final ArrayList<Properties> properties,
      final CssRuleSet ruleSet) {
    final Properties prop = properties.get(properties.size() - 1);

    SRGBColor color = null;
    CssRule rule = null;

    final List<CssRule> rules = ruleSet.getRules();
    for (int i = rules.size() - 1; i >= 0; --i) {
      if (rules.get(i).getRule().getName().equals(CssProperties.COLOR)) {
        final LexicalUnit value = rules.get(i).getRule().getValue();
        if (value.getLexicalUnitType() == LexicalUnit.SAC_RGBCOLOR) {
          final LexicalUnit params = value.getParameters();
          // r,g,b
          final int r = params.getIntegerValue();
          final int g = params.getNextLexicalUnit().getNextLexicalUnit().getIntegerValue();
          final int b = params.getNextLexicalUnit().getNextLexicalUnit().getNextLexicalUnit()
              .getNextLexicalUnit().getIntegerValue();
          color = new SRGBColor(r, g, b);
          rule = rules.get(i);
          break;
        } else if (value.getLexicalUnitType() == LexicalUnit.SAC_INHERIT) {
          color = null;
          rule = rules.get(i);
          break;
        } else if (value.getLexicalUnitType() == LexicalUnit.SAC_IDENT) {
          color = SRGBColorRegistry.getColorByName(new ICaseString(value.getStringValue()));
          if (color != null) {
            rule = rules.get(i);
            break;
          }
        }
      }
    }

    if (color == null) {
      final Properties parentProp = properties.get(properties.size() - 2);

      color = parentProp.get(Properties.CSS_COLOR, SRGBColor.class);
      rule = parentProp.get(Properties.CSS_RULE_COLOR, CssRule.class);
    }

    prop.put(Properties.CSS_RULE_COLOR, rule);
    prop.put(Properties.CSS_COLOR, color);

  }

}
