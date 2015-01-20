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
 * <http://www.gnu.org/licenses/>. $Id:
 * BackgroundColorPropertyResolverProcessor.java 13 2010-03-09 14:26:43Z sebtic
 * $
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
 * The {@link Processor} that resolves background colors.
 * 
 * @author Sébastien Aupetit
 */
public class BackgroundColorProcessor implements Processor {

  class Result {
    public SRGBColor color;

    public CssRule rule;

    public Result(final SRGBColor color, final CssRule rule) {
      this.color = color;
      this.rule = rule;
    }
  }

  private Result analyseBackgroundProperty(final CssRule rule, final LexicalUnit value) {
    if (value == null) {
      return null;
    }

    if (value.getLexicalUnitType() == LexicalUnit.SAC_RGBCOLOR) {
      final LexicalUnit params = value.getParameters();
      // r,g,b
      final int r = params.getIntegerValue();
      // on saute le premier nombre et la virgule
      final int g = params.getNextLexicalUnit().getNextLexicalUnit().getIntegerValue();
      // on saute le premier nombre, la virgule, le deuxième nombre et la
      // virgule
      final int b = params.getNextLexicalUnit().getNextLexicalUnit().getNextLexicalUnit()
          .getNextLexicalUnit().getIntegerValue();
      return new Result(new SRGBColor(r, g, b), rule);
    }

    if (value.getLexicalUnitType() == LexicalUnit.SAC_INHERIT) {
      return new Result(null, rule);
    }

    if (value.getLexicalUnitType() == LexicalUnit.SAC_IDENT) {
      if ("transparent".equalsIgnoreCase(value.getStringValue())) {
        // TODO : actually 'transparent' is handled as inherit. Doing more
        // advanced computing would lead to far for infrequent cases
        return new Result(null, rule);
      } else {
        final SRGBColor color = SRGBColorRegistry.getColorByName(new ICaseString(value
            .getStringValue()));
        if (color != null) {
          return new Result(color, rule);
        } else {
          analyseBackgroundProperty(rule, value.getNextLexicalUnit());
        }
      }
    }

    return analyseBackgroundProperty(rule, value.getNextLexicalUnit());
  }

  @Override
  public void process(final Document document, final ArrayList<AbstractElement> elements,
      final ArrayList<State> states, final ArrayList<Properties> properties,
      final CssRuleSet ruleSet) {
    final Properties prop = properties.get(properties.size() - 1);

    SRGBColor color = null;
    CssRule rule = null;

    // apply rules
    final List<CssRule> rules = ruleSet.getRules();
    for (int i = rules.size() - 1; i >= 0; --i) {
      if (rules.get(i).getRule().getName().equals(CssProperties.BACKGROUND)) {
        // par définition le premier paramètre doit être la couleur si
        // spécifié
        final LexicalUnit value = rules.get(i).getRule().getValue();
        final Result result = analyseBackgroundProperty(rules.get(i), value);
        if (result != null) {
          rule = result.rule;
          color = result.color;
        }
      } else if (rules.get(i).getRule().getName().equals(CssProperties.BACKGROUND_COLOR)) {
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
          if ("transparent".equalsIgnoreCase(value.getStringValue())) {
            // TODO : actually 'transparent' is handled as inherit. Doing more
            // advanced computing would lead to far for infrequent cases
            color = null;
            rule = rules.get(i);
            break;
          } else {
            color = SRGBColorRegistry.getColorByName(new ICaseString(value.getStringValue()));
            if (color != null) {
              rule = rules.get(i);
              break;
            }
          }
        }
      }
    }

    if (color == null) {
      final Properties parentProp = properties.get(properties.size() - 2);

      color = parentProp.get(Properties.CSS_BACKGROUNDCOLOR, SRGBColor.class);
      rule = parentProp.get(Properties.CSS_RULE_BACKGROUNDCOLOR, CssRule.class);
    }

    prop.put(Properties.CSS_RULE_BACKGROUNDCOLOR, rule);
    prop.put(Properties.CSS_BACKGROUNDCOLOR, color);
  }

}
