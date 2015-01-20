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
 * The {@link RuleSetProcessor} that resolves border styling. Warning:
 * foreground colors must be computed before for proper interpretation.
 * 
 * @author Sébastien Aupetit
 */
public class BorderColorRuleSetProcessor implements RuleSetProcessor {

  public static final ICaseString CSS_BORDER_TOP_COLOR = new ICaseString("css:border-top-color");

  public static final ICaseString CSS_BORDER_LEFT_COLOR = new ICaseString("css:border-left-color");

  public static final ICaseString CSS_BORDER_BOTTOM_COLOR = new ICaseString(
      "css:border-bottom-color");

  public static final ICaseString CSS_BORDER_RIGHT_COLOR = new ICaseString("css:border-right-color");

  public static final ICaseString CSS_BORDER_TOP_STYLE = new ICaseString("css:border-top-style");

  public static final ICaseString CSS_BORDER_LEFT_STYLE = new ICaseString("css:border-left-style");

  public static final ICaseString CSS_BORDER_BOTTOM_STYLE = new ICaseString(
      "css:border-bottom-style");

  public static final ICaseString CSS_BORDER_RIGHT_STYLE = new ICaseString("css:border-right-style");

  public static final ICaseString CSS_BORDER_TOP_WIDTH = new ICaseString("css:border-top-width");

  public static final ICaseString CSS_BORDER_LEFT_WIDTH = new ICaseString("css:border-left-width");

  public static final ICaseString CSS_BORDER_BOTTOM_WIDTH = new ICaseString(
      "css:border-bottom-width");

  public static final ICaseString CSS_BORDER_RIGHT_WIDTH = new ICaseString("css:border-right-width");

  private void copyFirstToSecondIfNotDefined(final ColorAndRule first, final ColorAndRule second) {
    if (first != null && first.rule != null && second.rule == null) {
      second.rule = first.rule;
      second.color = first.color;
    }
  }

  private void copyFirstToSecondIfNotDefined(final StyleAndRule first, final StyleAndRule second) {
    if (first != null && first.rule != null && second.rule == null) {
      second.rule = first.rule;
      second.style = first.style;
    }
  }

  private void copyFirstToSecondIfNotDefined(final WidthAndRule first, final WidthAndRule second) {
    if (first != null && first.rule != null && second.rule == null) {
      second.rule = first.rule;
      second.width = first.width;
    }
  }

  private void inheritAndSet(final ResolverState resolverState, ColorAndRule color,
      StyleAndRule style, WidthAndRule width, final ICaseString colorId, final ICaseString styleId,
      final ICaseString widthId) {
    final Properties prop = resolverState.getElementProperties();
    final Properties parentProp = resolverState.getParentResolverState().getElementProperties();
    if (color.color == null && color.rule != null) { // inherit
      color = parentProp.get(colorId, ColorAndRule.class);
    }
    if (width.width == null && width.rule != null) { // inherit
      width = parentProp.get(widthId, WidthAndRule.class);
    }
    if (style.style == null && style.rule != null) { // inherit
      style = parentProp.get(styleId, StyleAndRule.class);
    }
    if (color.rule == null && (width.rule != null || style.rule != null)) {
      // default to foreground color if one of the two other properties is
      // defined and the color is not defined
      copyFirstToSecondIfNotDefined(
          prop.get(ColorRuleSetProcessor.CSS_FOREGROUND_COLOR, ColorAndRule.class), color);
    }
    prop.put(colorId, color);
    prop.put(widthId, width);
    prop.put(styleId, style);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.mime.css.resolver.RuleSetProcessor#process(
   * org.projectsforge.swap.core.mime.css.resolver.ResolverState,
   * java.util.List)
   */
  @Override
  public void process(final ResolverState resolverState, final CssRule[] rules) {

    final ColorAndRule topColor = new ColorAndRule();
    final ColorAndRule leftColor = new ColorAndRule();
    final ColorAndRule bottomColor = new ColorAndRule();
    final ColorAndRule rightColor = new ColorAndRule();
    final StyleAndRule topStyle = new StyleAndRule();
    final StyleAndRule leftStyle = new StyleAndRule();
    final StyleAndRule bottomStyle = new StyleAndRule();
    final StyleAndRule rightStyle = new StyleAndRule();
    final WidthAndRule topWidth = new WidthAndRule();
    final WidthAndRule leftWidth = new WidthAndRule();
    final WidthAndRule bottomWidth = new WidthAndRule();
    final WidthAndRule rightWidth = new WidthAndRule();

    // FAIRE 4 analyseurs : 1 final par coté ce final sera plus simple

    // http://www.w3.org/TR/CSS2/box.html#propdef-border-color

    // apply rules
    for (int i = rules.length - 1; i >= 0; --i) {
      final ICaseString ruleName = rules[i].getRule().getName();

      if (ruleName.equals(CssProperties.BORDER)) {
        final ColorAndRule car = new ColorAndRule();
        final StyleAndRule sar = new StyleAndRule();
        final WidthAndRule war = new WidthAndRule();
        LexicalUnit lu = rules[i].getRule().getValue();
        while (lu != null) {
          car.extract(rules[i], resolverState.getElement(), lu);
          sar.extract(rules[i], resolverState.getElement(), lu);
          war.extract(rules[i], resolverState.getElement(), lu);
          lu = lu.getNextLexicalUnit();
        }
        copyFirstToSecondIfNotDefined(car, topColor);
        copyFirstToSecondIfNotDefined(car, leftColor);
        copyFirstToSecondIfNotDefined(car, bottomColor);
        copyFirstToSecondIfNotDefined(car, rightColor);
        copyFirstToSecondIfNotDefined(sar, topStyle);
        copyFirstToSecondIfNotDefined(sar, leftStyle);
        copyFirstToSecondIfNotDefined(sar, bottomStyle);
        copyFirstToSecondIfNotDefined(sar, rightStyle);
        copyFirstToSecondIfNotDefined(war, topWidth);
        copyFirstToSecondIfNotDefined(war, leftWidth);
        copyFirstToSecondIfNotDefined(war, bottomWidth);
        copyFirstToSecondIfNotDefined(war, rightWidth);
      } else if (topColor.rule != null && ruleName.equals(CssProperties.BORDER_TOP)) {
        final ColorAndRule car = new ColorAndRule();
        final StyleAndRule sar = new StyleAndRule();
        final WidthAndRule war = new WidthAndRule();
        LexicalUnit lu = rules[i].getRule().getValue();
        while (lu != null) {
          car.extract(rules[i], resolverState.getElement(), lu);
          sar.extract(rules[i], resolverState.getElement(), lu);
          war.extract(rules[i], resolverState.getElement(), lu);
          lu = lu.getNextLexicalUnit();
        }
        copyFirstToSecondIfNotDefined(car, topColor);
        copyFirstToSecondIfNotDefined(sar, topStyle);
        copyFirstToSecondIfNotDefined(war, topWidth);
      } else if (leftColor.rule != null && ruleName.equals(CssProperties.BORDER_LEFT)) {
        final ColorAndRule car = new ColorAndRule();
        final StyleAndRule sar = new StyleAndRule();
        final WidthAndRule war = new WidthAndRule();
        LexicalUnit lu = rules[i].getRule().getValue();
        while (lu != null) {
          car.extract(rules[i], resolverState.getElement(), lu);
          sar.extract(rules[i], resolverState.getElement(), lu);
          war.extract(rules[i], resolverState.getElement(), lu);
          lu = lu.getNextLexicalUnit();
        }
        copyFirstToSecondIfNotDefined(car, leftColor);
        copyFirstToSecondIfNotDefined(sar, leftStyle);
        copyFirstToSecondIfNotDefined(war, leftWidth);
      } else if (bottomColor.rule != null && ruleName.equals(CssProperties.BORDER_BOTTOM)) {
        final ColorAndRule car = new ColorAndRule();
        final StyleAndRule sar = new StyleAndRule();
        final WidthAndRule war = new WidthAndRule();
        LexicalUnit lu = rules[i].getRule().getValue();
        while (lu != null) {
          car.extract(rules[i], resolverState.getElement(), lu);
          sar.extract(rules[i], resolverState.getElement(), lu);
          war.extract(rules[i], resolverState.getElement(), lu);
          lu = lu.getNextLexicalUnit();
        }
        copyFirstToSecondIfNotDefined(car, bottomColor);
        copyFirstToSecondIfNotDefined(sar, bottomStyle);
        copyFirstToSecondIfNotDefined(war, bottomWidth);
      } else if (rightColor.rule != null && ruleName.equals(CssProperties.BORDER_RIGHT)) {
        final ColorAndRule car = new ColorAndRule();
        final StyleAndRule sar = new StyleAndRule();
        final WidthAndRule war = new WidthAndRule();
        LexicalUnit lu = rules[i].getRule().getValue();
        while (lu != null) {
          car.extract(rules[i], resolverState.getElement(), lu);
          sar.extract(rules[i], resolverState.getElement(), lu);
          war.extract(rules[i], resolverState.getElement(), lu);
          lu = lu.getNextLexicalUnit();
        }
        copyFirstToSecondIfNotDefined(car, rightColor);
        copyFirstToSecondIfNotDefined(sar, rightStyle);
        copyFirstToSecondIfNotDefined(war, rightWidth);
      } else if (ruleName.equals(CssProperties.BORDER_WIDTH)) {
        final LexicalUnit lu = rules[i].getRule().getValue();
        final WidthAndRule wars[] = { new WidthAndRule(), new WidthAndRule(), new WidthAndRule(),
            new WidthAndRule() };
        int count = 0;
        while (lu != null && count < 4) {
          wars[count].extract(rules[i], resolverState.getElement(), lu);
          if (wars[count].rule != null) {
            count++;
          } else {
            break;
          }
        }

        switch (count) {
          case 1:
            copyFirstToSecondIfNotDefined(wars[0], topWidth);
            copyFirstToSecondIfNotDefined(wars[0], leftWidth);
            copyFirstToSecondIfNotDefined(wars[0], bottomWidth);
            copyFirstToSecondIfNotDefined(wars[0], rightWidth);
            break;
          case 2:
            copyFirstToSecondIfNotDefined(wars[0], topWidth);
            copyFirstToSecondIfNotDefined(wars[0], bottomWidth);
            copyFirstToSecondIfNotDefined(wars[1], leftWidth);
            copyFirstToSecondIfNotDefined(wars[1], rightWidth);
            break;
          case 3:
            copyFirstToSecondIfNotDefined(wars[0], topWidth);
            copyFirstToSecondIfNotDefined(wars[1], leftWidth);
            copyFirstToSecondIfNotDefined(wars[1], rightWidth);
            copyFirstToSecondIfNotDefined(wars[2], bottomWidth);
            break;
          case 4:
            copyFirstToSecondIfNotDefined(wars[0], topWidth);
            copyFirstToSecondIfNotDefined(wars[1], rightWidth);
            copyFirstToSecondIfNotDefined(wars[2], bottomWidth);
            copyFirstToSecondIfNotDefined(wars[3], leftWidth);
            break;
        }
      } else if (ruleName.equals(CssProperties.BORDER_STYLE)) {
        final LexicalUnit lu = rules[i].getRule().getValue();
        final StyleAndRule[] sars = { new StyleAndRule(), new StyleAndRule(), new StyleAndRule(),
            new StyleAndRule() };
        int count = 0;
        while (lu != null && count < 4) {
          sars[count].extract(rules[i], resolverState.getElement(), lu);
          if (sars[count].rule != null) {
            count++;
          } else {
            break;
          }
        }

        switch (count) {
          case 1:
            copyFirstToSecondIfNotDefined(sars[0], topStyle);
            copyFirstToSecondIfNotDefined(sars[0], leftStyle);
            copyFirstToSecondIfNotDefined(sars[0], bottomStyle);
            copyFirstToSecondIfNotDefined(sars[0], rightStyle);
            break;
          case 2:
            copyFirstToSecondIfNotDefined(sars[0], topStyle);
            copyFirstToSecondIfNotDefined(sars[0], bottomStyle);
            copyFirstToSecondIfNotDefined(sars[1], leftStyle);
            copyFirstToSecondIfNotDefined(sars[1], rightStyle);
            break;
          case 3:
            copyFirstToSecondIfNotDefined(sars[0], topStyle);
            copyFirstToSecondIfNotDefined(sars[1], leftStyle);
            copyFirstToSecondIfNotDefined(sars[1], rightStyle);
            copyFirstToSecondIfNotDefined(sars[2], bottomStyle);
            break;
          case 4:
            copyFirstToSecondIfNotDefined(sars[0], topStyle);
            copyFirstToSecondIfNotDefined(sars[1], rightStyle);
            copyFirstToSecondIfNotDefined(sars[2], bottomStyle);
            copyFirstToSecondIfNotDefined(sars[3], leftStyle);
            break;
        }
      } else if (ruleName.equals(CssProperties.BORDER_COLOR)) {
        final LexicalUnit lu = rules[i].getRule().getValue();
        final ColorAndRule[] cars = { new ColorAndRule(), new ColorAndRule(), new ColorAndRule(),
            new ColorAndRule() };
        int count = 0;
        while (lu != null && count < 4) {
          cars[count].extract(rules[i], resolverState.getElement(), lu);
          if (cars[count].rule != null) {
            count++;
          } else {
            break;
          }
        }

        switch (count) {
          case 1:
            copyFirstToSecondIfNotDefined(cars[0], topColor);
            copyFirstToSecondIfNotDefined(cars[0], leftColor);
            copyFirstToSecondIfNotDefined(cars[0], bottomColor);
            copyFirstToSecondIfNotDefined(cars[0], rightColor);
            break;
          case 2:
            copyFirstToSecondIfNotDefined(cars[0], topColor);
            copyFirstToSecondIfNotDefined(cars[0], bottomColor);
            copyFirstToSecondIfNotDefined(cars[1], leftColor);
            copyFirstToSecondIfNotDefined(cars[1], rightColor);
            break;
          case 3:
            copyFirstToSecondIfNotDefined(cars[0], topColor);
            copyFirstToSecondIfNotDefined(cars[1], leftColor);
            copyFirstToSecondIfNotDefined(cars[1], rightColor);
            copyFirstToSecondIfNotDefined(cars[2], bottomColor);
            break;
          case 4:
            copyFirstToSecondIfNotDefined(cars[0], topColor);
            copyFirstToSecondIfNotDefined(cars[1], rightColor);
            copyFirstToSecondIfNotDefined(cars[2], bottomColor);
            copyFirstToSecondIfNotDefined(cars[3], leftColor);
            break;
        }

      } else if (topStyle.rule != null && ruleName.equals(CssProperties.BORDER_TOP_STYLE)) {
        topStyle.extract(rules[i], resolverState.getElement(), rules[i].getRule().getValue());
      } else if (leftStyle.rule != null && ruleName.equals(CssProperties.BORDER_LEFT_STYLE)) {
        leftStyle.extract(rules[i], resolverState.getElement(), rules[i].getRule().getValue());
      } else if (bottomStyle.rule != null && ruleName.equals(CssProperties.BORDER_BOTTOM_STYLE)) {
        bottomStyle.extract(rules[i], resolverState.getElement(), rules[i].getRule().getValue());
      } else if (rightStyle.rule != null && ruleName.equals(CssProperties.BORDER_RIGHT_STYLE)) {
        rightStyle.extract(rules[i], resolverState.getElement(), rules[i].getRule().getValue());
      } else if (bottomColor.rule != null && ruleName.equals(CssProperties.BORDER_BOTTOM_COLOR)) {
        bottomColor.extract(rules[i], resolverState.getElement(), rules[i].getRule().getValue());
      } else if (leftColor.rule != null && ruleName.equals(CssProperties.BORDER_LEFT_COLOR)) {
        leftColor.extract(rules[i], resolverState.getElement(), rules[i].getRule().getValue());
      } else if (rightColor.rule != null && ruleName.equals(CssProperties.BORDER_RIGHT_COLOR)) {
        rightColor.extract(rules[i], resolverState.getElement(), rules[i].getRule().getValue());
      } else if (topColor.rule != null && ruleName.equals(CssProperties.BORDER_TOP_COLOR)) {
        topColor.extract(rules[i], resolverState.getElement(), rules[i].getRule().getValue());
      } else if (topWidth.rule != null && ruleName.equals(CssProperties.BORDER_TOP_WIDTH)) {
        topWidth.extract(rules[i], resolverState.getElement(), rules[i].getRule().getValue());
      } else if (leftWidth.rule != null && ruleName.equals(CssProperties.BORDER_LEFT_WIDTH)) {
        leftWidth.extract(rules[i], resolverState.getElement(), rules[i].getRule().getValue());
      } else if (bottomWidth.rule != null && ruleName.equals(CssProperties.BORDER_BOTTOM_WIDTH)) {
        bottomWidth.extract(rules[i], resolverState.getElement(), rules[i].getRule().getValue());
      } else if (rightWidth.rule != null && ruleName.equals(CssProperties.BORDER_RIGHT_WIDTH)) {
        rightWidth.extract(rules[i], resolverState.getElement(), rules[i].getRule().getValue());
      }
    }

    inheritAndSet(resolverState, topColor, topStyle, topWidth, CSS_BORDER_TOP_COLOR,
        CSS_BORDER_TOP_STYLE, CSS_BORDER_TOP_WIDTH);
    inheritAndSet(resolverState, leftColor, leftStyle, leftWidth, CSS_BORDER_LEFT_COLOR,
        CSS_BORDER_LEFT_STYLE, CSS_BORDER_LEFT_WIDTH);
    inheritAndSet(resolverState, bottomColor, bottomStyle, bottomWidth, CSS_BORDER_BOTTOM_COLOR,
        CSS_BORDER_BOTTOM_STYLE, CSS_BORDER_BOTTOM_WIDTH);
    inheritAndSet(resolverState, rightColor, rightStyle, rightWidth, CSS_BORDER_RIGHT_COLOR,
        CSS_BORDER_RIGHT_STYLE, CSS_BORDER_RIGHT_WIDTH);

  }
}
