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
package org.projectsforge.swap.core.mime.css.resolver.font;

import org.projectsforge.swap.core.mime.css.CssProperties;
import org.projectsforge.swap.core.mime.css.property.font.Font;
import org.projectsforge.swap.core.mime.css.resolver.CssRule;
import org.projectsforge.swap.core.mime.css.resolver.ResolverState;
import org.projectsforge.swap.core.mime.css.resolver.RuleSetProcessor;
import org.projectsforge.utils.icasestring.ICaseString;

/**
 * The Class FontRuleSetProcessor.
 */
public class FontRuleSetProcessor implements RuleSetProcessor {

  /** The Constant CSS_FONT. */
  public static final ICaseString CSS_FONT = new ICaseString("css:font");

  @Override
  public void process(final ResolverState resolverState, final CssRule[] rules) {

    // NB: <i>, <b>, <u>, <em> are defined by style in default stylesheet so no
    // need for special handling of it here

    final Font font = new Font();

    Font parentFont;
    if (resolverState.getParentResolverState() != null) {
      parentFont = resolverState.getParentResolverState().getElementProperties()
          .get(CSS_FONT, Font.class);
      if (parentFont == null) {
        parentFont = new Font();
      }
    } else {
      parentFont = new Font();
    }

    for (final CssRule rule : rules) {
      if (rule.getRule().getName().equals(CssProperties.LINE_HEIGHT)) {
        font.parseLineHeight(rule.getRule().getValue(), parentFont);
      } else if (rule.getRule().getName().equals(CssProperties.FONT)) {
        font.parseFont(rule.getRule().getValue(), parentFont);
      } else if (rule.getRule().getName().equals(CssProperties.FONT_FAMILY)) {
        font.parseFamily(rule.getRule().getValue(), parentFont);
      } else if (rule.getRule().getName().equals(CssProperties.FONT_SIZE)) {
        font.parseSize(rule.getRule().getValue(), parentFont);
      } else if (rule.getRule().getName().equals(CssProperties.FONT_STYLE)) {
        font.parseStyle(rule.getRule().getValue(), parentFont);
      } else if (rule.getRule().getName().equals(CssProperties.FONT_VARIANT)) {
        font.parseVariant(rule.getRule().getValue(), parentFont);
      } else if (rule.getRule().getName().equals(CssProperties.FONT_WEIGHT)) {
        font.parseWeight(rule.getRule().getValue(), parentFont);
      }
    }

    resolverState.getElementProperties().put(CSS_FONT, font);
  }

}
