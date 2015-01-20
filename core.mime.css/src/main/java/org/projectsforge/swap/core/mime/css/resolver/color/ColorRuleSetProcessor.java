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
 * <http://www.gnu.org/licenses/>. $Id: ColorRuleSetProcessor.java 91 2011-07-18
 * 16:28:31Z sebtic $
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
 * The {@link RuleSetProcessor} that resolves foreground colors.
 * 
 * @author Sébastien Aupetit
 */
public class ColorRuleSetProcessor implements RuleSetProcessor {

  /** The Constant CSS_COLOR. */
  public static final ICaseString CSS_FOREGROUND_COLOR = new ICaseString("css:foreground-color");

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

    ColorAndRule foreground = new ColorAndRule();

    for (int i = rules.length - 1; i >= 0; --i) {
      final ICaseString ruleName = rules[i].getRule().getName();
      if (ruleName.equals(CssProperties.COLOR)) {
        final LexicalUnit lu = rules[i].getRule().getValue();
        foreground.extract(rules[i], resolverState.getElement(), lu);
        if (foreground.rule != null) {
          break;
        }
      }
    }

    if (foreground.color == null) { // inherited by default
      final Properties parentProp = resolverState.getParentResolverState().getElementProperties();
      foreground = parentProp.get(CSS_FOREGROUND_COLOR, ColorAndRule.class);
    }
    prop.put(CSS_FOREGROUND_COLOR, foreground);
  }

}
