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

import org.projectsforge.swap.core.mime.css.CssProperties;
import org.projectsforge.swap.core.mime.css.resolver.CssRule;
import org.projectsforge.swap.core.mime.css.resolver.Properties;
import org.projectsforge.swap.core.mime.css.resolver.ResolverState;
import org.projectsforge.swap.core.mime.css.resolver.RuleSetProcessor;
import org.projectsforge.utils.icasestring.ICaseString;
import org.w3c.css.sac.LexicalUnit;

/**
 * The {@link RuleSetProcessor} that resolves background colors.
 * 
 * @author Sébastien Aupetit
 */
public class BackgroundColorRuleSetProcessor implements RuleSetProcessor {

  /** The Constant CSS_BACKGROUNDCOLOR. */
  public static final ICaseString CSS_BACKGROUND_COLOR = new ICaseString("css:background-color");

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.mime.css.resolver.RuleSetProcessor#process(
   * org.projectsforge.swap.core.mime.css.resolver.ResolverState,
   * java.util.List)
   */
  @Override
  public void process(final ResolverState resolverState, final CssRule[] rules) {
    final Properties prop = resolverState.getElementProperties();

    ColorAndRule background = new ColorAndRule();

    for (int i = rules.length - 1; i >= 0; --i) {
      final ICaseString ruleName = rules[i].getRule().getName();
      if (ruleName.equals(CssProperties.BACKGROUND)) {
        LexicalUnit lu = rules[i].getRule().getValue();
        while (lu != null) {
          background.extract(rules[i], resolverState.getElement(), lu);
          if (background.rule != null) {
            break;
          }
          lu = lu.getNextLexicalUnit();
        }
        if (background.rule != null) {
          break;
        }
      } else if (ruleName.equals(CssProperties.BACKGROUND_COLOR)) {
        final LexicalUnit lu = rules[i].getRule().getValue();
        background.extract(rules[i], resolverState.getElement(), lu);
        if (background.rule != null) {
          break;
        }
      }
    }

    if (background.color == null && background.rule != null) { // inherit
      final Properties parentProp = resolverState.getParentResolverState().getElementProperties();
      background = parentProp.get(CSS_BACKGROUND_COLOR, ColorAndRule.class);
    }
    prop.put(CSS_BACKGROUND_COLOR, background);
  }

}
