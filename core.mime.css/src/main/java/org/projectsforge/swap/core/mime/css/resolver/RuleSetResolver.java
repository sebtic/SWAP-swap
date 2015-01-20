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
 * <http://www.gnu.org/licenses/>. $Id: RuleSetResolver.java 125 2012-04-04
 * 14:59:59Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.resolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import org.projectsforge.swap.core.mime.css.CssProperties;
import org.projectsforge.swap.core.mime.css.nodes.Media;
import org.projectsforge.swap.core.mime.css.nodes.Rule;
import org.projectsforge.swap.core.mime.css.nodes.SelectorRulesPair;
import org.projectsforge.swap.core.mime.css.nodes.Stylesheet;
import org.projectsforge.swap.core.mime.css.parser.CSSParsingException;
import org.projectsforge.swap.core.mime.css.parser.CssParser;
import org.projectsforge.swap.core.mime.css.parser.selectors.CssDummySelector;
import org.projectsforge.swap.core.mime.css.parser.selectors.CssElementSelector;
import org.projectsforge.swap.core.mime.css.parser.selectors.CssSelector;
import org.projectsforge.swap.core.mime.css.resolver.CssRule.Origin;
import org.projectsforge.swap.core.mime.html.nodes.elements.AbstractElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.Attributes;
import org.projectsforge.swap.core.mime.html.nodes.elements.BODYElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.FONTElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.HRElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.IFRAMEElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.LIElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.OLElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.ULElement;
import org.projectsforge.utils.icasestring.ICaseString;
import org.projectsforge.utils.tasksexecutor.RecursiveTaskExecutorException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.css.sac.LexicalUnit;

/**
 * The class used to compute the applicable rule set to an element taking care
 * of recording origins for properly applying ordering of rules according to
 * standards.
 * 
 * @author Sébastien Aupetit
 */
class RuleSetResolver {

  /**
   * Extract element selector.
   * 
   * @param selector the selector
   * @return the i case string
   */
  private static ICaseString extractElementSelector(final CssSelector selector) {
    if (selector == null) {
      return null;
    } else if (selector instanceof CssElementSelector) {
      return ((CssElementSelector) selector).getName();
    } else {
      return extractElementSelector((CssSelector) selector.getMainSelector());
    }
  }

  /**
   * Trim.
   * 
   * @param value the value
   * @return the string
   */
  private static String trim(final String value) {
    return (value == null) ? "" : value.trim();
  }

  /** The logger. */
  private final Logger logger = org.slf4j.LoggerFactory.getLogger(RuleSetResolver.class);

  /** The author stylesheet. */
  private final List<Stylesheet> authorStylesheets;

  /** The user agent stylesheet. */
  private final Stylesheet userAgentStylesheet;

  /** The user stylesheet. */
  private final Stylesheet userStylesheet;

  /** The media. */
  private final ICaseString media;

  /** The matcher visitor. */
  @Autowired
  private CssMatcherTester cssMatcherTester;

  /** Map to cache pairs based on the Media instance and tag name. */
  private final Map<Stylesheet, Map<ICaseString, SelectorRulesPair[]>> filteredStyles = new IdentityHashMap<>();

  private static final SelectorRulesPair[] EMPTY_SELECTOR_RULES_PAIR_ARRAY = new SelectorRulesPair[0];

  /**
   * Instantiates a new rule set resolver.
   * 
   * @param authorStylesheets the author stylesheet
   * @param userAgentStylesheet the user agent stylesheet
   * @param userStylesheet the user stylesheet
   * @param media the media
   */
  public RuleSetResolver(final List<Stylesheet> authorStylesheets,
      final Stylesheet userAgentStylesheet, final Stylesheet userStylesheet, final ICaseString media) {
    this.authorStylesheets = authorStylesheets;
    this.userAgentStylesheet = userAgentStylesheet;
    this.userStylesheet = userStylesheet;
    this.media = media;
  }

  /**
   * Gets the rule set.
   * 
   * @param resolverState the resolver state
   * @return the rule set
   * @throws RecursiveTaskExecutorException
   */
  public CssRuleSet getRuleSet(final ResolverState resolverState)
      throws RecursiveTaskExecutorException {
    final CssRuleSet ruleSet = new CssRuleSet();
    handleUserAgentRuleSet(ruleSet, resolverState);
    handleUserRuleSet(ruleSet, resolverState);
    handleAuthorRuleSet(ruleSet, resolverState);
    handleHtmlTagStyleProperty(ruleSet, resolverState);
    handleHtmlTagProperties(ruleSet, resolverState);
    return ruleSet;
  }

  /**
   * Gets the {@link SelectorRulesPair}s from the given stylesheet for the given
   * html tag element. Caching is used to speedup things on next calls.
   * 
   * @param htmlElement the name of the html tag
   * @param stylesheet the stylesheet
   * @return the list of {@link SelectorRulesPair}
   */
  private SelectorRulesPair[] getSelectorRulesPairs(final ICaseString htmlElement,
      final Stylesheet stylesheet) {
    Map<ICaseString, SelectorRulesPair[]> tagList = filteredStyles.get(stylesheet);
    if (tagList == null) {
      tagList = new HashMap<>();
      filteredStyles.put(stylesheet, tagList);
    }

    SelectorRulesPair[] result = tagList.get(htmlElement);
    if (result == null) {
      final ArrayList<Object> temp = new ArrayList<>();

      for (final SelectorRulesPair pair : stylesheet.getMedia(Media.ALL).getSelectorRulesPairs()) {
        final ICaseString elementSelector = extractElementSelector(pair.getSelector());
        if (elementSelector == null || htmlElement.equals(elementSelector)) {
          temp.add(pair);
        }
      }

      if (!Media.ALL.equals(media)) {
        for (final SelectorRulesPair pair : stylesheet.getMedia(media).getSelectorRulesPairs()) {
          final ICaseString elementSelector = extractElementSelector(pair.getSelector());
          if (elementSelector == null || htmlElement.equals(elementSelector)) {
            temp.add(pair);
          }
        }
      }

      result = temp.toArray(EMPTY_SELECTOR_RULES_PAIR_ARRAY);
      tagList.put(htmlElement, result);
    }

    return result;
  }

  /**
   * Compute author rule set.
   * 
   * @param ruleSet the rule set
   * @param resolverState the resolver state
   * @throws RecursiveTaskExecutorException
   */
  private void handleAuthorRuleSet(final CssRuleSet ruleSet, final ResolverState resolverState)
      throws RecursiveTaskExecutorException {
    for (final Stylesheet stylesheet : authorStylesheets) {
      for (final SelectorRulesPair pair : getSelectorRulesPairs(resolverState.getElement()
          .getTagName(), stylesheet)) {
        if (cssMatcherTester.match(pair.getSelector(), resolverState)) {
          for (final Rule rule : pair.getRules()) {
            final CssRule cssRule = new CssRule(null, pair.getSelector(), rule,
                CssRule.Origin.AUTHOR);
            if (rule.isImportant()) {
              ruleSet.addAuthorImportantRule(cssRule);
            } else {
              ruleSet.addAuthorNormalRule(cssRule);
            }
          }
        }
      }
    }
  }

  /**
   * Compute tag rule set.
   * 
   * @param ruleSet the rule set
   * @param resolverState the resolver state
   */
  private void handleHtmlTagProperties(final CssRuleSet ruleSet, final ResolverState resolverState) {
    final AbstractElement node = resolverState.getElement();
    final String heightAttribute = trim(node.getAttribute(Attributes.HEIGHT));
    final String widthAttribute = trim(node.getAttribute(Attributes.WIDTH));
    final String hspaceAttribute = trim(node.getAttribute(Attributes.HSPACE));
    final String vspaceAttribute = trim(node.getAttribute(Attributes.VSPACE));
    final String colorAttribute = trim(node.getAttribute(Attributes.COLOR));
    final String bgcolorAttribute = trim(node.getAttribute(Attributes.BGCOLOR));
    final String backgroundAttribute = trim(node.getAttribute(Attributes.BACKGROUND));
    final String sizeAttribute = trim(node.getAttribute(Attributes.SIZE));
    final String faceAttribute = trim(node.getAttribute(Attributes.FACE));
    final String alignAttribute = trim(node.getAttribute(Attributes.ALIGN));
    final String valignAttribute = trim(node.getAttribute(Attributes.VALIGN));
    final String nowrapAttribute = trim(node.getAttribute(Attributes.NOWRAP));
    final String clearAttribute = trim(node.getAttribute(Attributes.CLEAR));
    final String marginheightAttribute = trim(node.getAttribute(Attributes.MARGINHEIGHT));
    final String marginwidthAttribute = trim(node.getAttribute(Attributes.MARGINWIDTH));
    final String borderAttribute = trim(node.getAttribute(Attributes.BORDER));
    final String textAttribute = trim(node.getAttribute(Attributes.TEXT));
    final String typeAttribute = trim(node.getAttribute(Attributes.TYPE));

    if (!heightAttribute.isEmpty()) {
      try {
        ruleSet.addHtmlTagPropertyRule(new CssRule(node, CssDummySelector.getInstance(), new Rule(
            CssProperties.HEIGHT, CssParser.parsePropertyValue(heightAttribute), false),
            Origin.HTMLTAG_PROPERTY));
      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value", e);
      }
    }

    if (!widthAttribute.isEmpty()) {
      try {
        ruleSet.addHtmlTagPropertyRule(new CssRule(node, CssDummySelector.getInstance(), new Rule(
            CssProperties.WIDTH, CssParser.parsePropertyValue(widthAttribute), false),
            Origin.HTMLTAG_PROPERTY));
      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value " + widthAttribute + " for " + node, e);
      }
    }

    if (!hspaceAttribute.isEmpty()) {
      try {
        final LexicalUnit value = CssParser.parsePropertyValue(hspaceAttribute);
        ruleSet.addHtmlTagPropertyRule(new CssRule(node, CssDummySelector.getInstance(), new Rule(
            CssProperties.MARGIN_LEFT, value, false), Origin.HTMLTAG_PROPERTY));
        ruleSet.addHtmlTagPropertyRule(new CssRule(node, CssDummySelector.getInstance(), new Rule(
            CssProperties.MARGIN_RIGHT, value, false), Origin.HTMLTAG_PROPERTY));
      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value", e);
      }
    }

    if (!vspaceAttribute.isEmpty()) {
      try {
        final LexicalUnit value = CssParser.parsePropertyValue(vspaceAttribute);

        ruleSet.addHtmlTagPropertyRule(new CssRule(node, CssDummySelector.getInstance(), new Rule(
            CssProperties.MARGIN_TOP, value, false), Origin.HTMLTAG_PROPERTY));
        ruleSet.addHtmlTagPropertyRule(new CssRule(node, CssDummySelector.getInstance(), new Rule(
            CssProperties.MARGIN_BOTTOM, value, false), Origin.HTMLTAG_PROPERTY));
      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value", e);
      }
    }

    if (!colorAttribute.isEmpty()) {
      try {
        ruleSet.addHtmlTagPropertyRule(new CssRule(node, CssDummySelector.getInstance(), new Rule(
            CssProperties.COLOR, CssParser.parsePropertyValue(colorAttribute), false),
            Origin.HTMLTAG_PROPERTY));
      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value", e);
      }
    }

    if (!bgcolorAttribute.isEmpty()) {
      try {
        ruleSet.addHtmlTagPropertyRule(new CssRule(node, CssDummySelector.getInstance(), new Rule(
            CssProperties.BACKGROUND_COLOR, CssParser.parsePropertyValue(bgcolorAttribute), false),
            Origin.HTMLTAG_PROPERTY));
      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value", e);
      }
    }

    if (!backgroundAttribute.isEmpty()) {
      try {
        // TODO il y a un problème ici
        ruleSet.addHtmlTagPropertyRule(new CssRule(node, CssDummySelector.getInstance(), new Rule(
            CssProperties.BACKGROUND_IMAGE, CssParser.parsePropertyValue("url("
                + backgroundAttribute + ")"), false), Origin.HTMLTAG_PROPERTY));
      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value", e);
      }
    }

    if (!sizeAttribute.isEmpty()) {
      try {
        if (node instanceof FONTElement) {
          ruleSet
              .addHtmlTagPropertyRule(new CssRule(node, CssDummySelector.getInstance(), new Rule(
                  CssProperties.FONT_SIZE, CssParser.parsePropertyValue(sizeAttribute), false),
                  Origin.HTMLTAG_PROPERTY));
        } else if (node instanceof HRElement) {
          ruleSet.addHtmlTagPropertyRule(new CssRule(node, CssDummySelector.getInstance(),
              new Rule(CssProperties.HEIGHT, CssParser.parsePropertyValue(sizeAttribute), false),
              Origin.HTMLTAG_PROPERTY));
        }
      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value", e);
      }
    }

    if (!faceAttribute.isEmpty()) {
      try {
        ruleSet.addHtmlTagPropertyRule(new CssRule(node, CssDummySelector.getInstance(), new Rule(
            CssProperties.FONT_FAMILY, CssParser.parsePropertyValue(faceAttribute), false),
            Origin.HTMLTAG_PROPERTY));
      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value", e);
      }
    }

    if (!alignAttribute.isEmpty()) {
      try {
        ruleSet.addHtmlTagPropertyRule(new CssRule(node, CssDummySelector.getInstance(), new Rule(
            CssProperties.TEXT_ALIGN, CssParser.parsePropertyValue(alignAttribute), false),
            Origin.HTMLTAG_PROPERTY));
      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value", e);
      }
    }

    if (!valignAttribute.isEmpty()) {
      try {
        ruleSet.addHtmlTagPropertyRule(new CssRule(node, CssDummySelector.getInstance(), new Rule(
            CssProperties.VERTICAL_ALIGN, CssParser.parsePropertyValue(valignAttribute), false),
            Origin.HTMLTAG_PROPERTY));
      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value", e);
      }
    }

    if (!nowrapAttribute.isEmpty()) {
      try {
        ruleSet.addHtmlTagPropertyRule(new CssRule(node, CssDummySelector.getInstance(), new Rule(
            CssProperties.WHITE_SPACE, CssParser.parsePropertyValue("nowrap"), false),
            Origin.HTMLTAG_PROPERTY));
      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value", e);
      }
    }

    if (!clearAttribute.isEmpty()) {
      try {
        if (clearAttribute.equalsIgnoreCase("all")) {
          ruleSet.addHtmlTagPropertyRule(new CssRule(node, CssDummySelector.getInstance(),
              new Rule(CssProperties.CLEAR, CssParser.parsePropertyValue("both"), false),
              Origin.HTMLTAG_PROPERTY));
        } else {
          ruleSet.addHtmlTagPropertyRule(new CssRule(node, CssDummySelector.getInstance(),
              new Rule(CssProperties.CLEAR, CssParser.parsePropertyValue(clearAttribute), false),
              Origin.HTMLTAG_PROPERTY));
        }

      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value", e);
      }
    }

    if (!marginheightAttribute.isEmpty()) {
      if (node instanceof IFRAMEElement) {
        try {
          final LexicalUnit value = CssParser.parsePropertyValue(marginheightAttribute);

          ruleSet.addHtmlTagPropertyRule(new CssRule(node, CssDummySelector.getInstance(),
              new Rule(CssProperties.MARGIN_TOP, value, false), Origin.HTMLTAG_PROPERTY));
          ruleSet.addHtmlTagPropertyRule(new CssRule(node, CssDummySelector.getInstance(),
              new Rule(CssProperties.MARGIN_BOTTOM, value, false), Origin.HTMLTAG_PROPERTY));
        } catch (final CSSParsingException e) {
          logger.debug("Error parsing value", e);
        }
      }
    }

    if (!marginwidthAttribute.isEmpty()) {
      if (node instanceof IFRAMEElement) {
        try {
          final LexicalUnit value = CssParser.parsePropertyValue(marginwidthAttribute);

          ruleSet.addHtmlTagPropertyRule(new CssRule(node, CssDummySelector.getInstance(),
              new Rule(CssProperties.MARGIN_LEFT, value, false), Origin.HTMLTAG_PROPERTY));
          ruleSet.addHtmlTagPropertyRule(new CssRule(node, CssDummySelector.getInstance(),
              new Rule(CssProperties.MARGIN_RIGHT, value, false), Origin.HTMLTAG_PROPERTY));
        } catch (final CSSParsingException e) {
          logger.debug("Error parsing value", e);
        }
      }
    }

    if (!borderAttribute.isEmpty()) {
      try {
        ruleSet.addHtmlTagPropertyRule(new CssRule(node, CssDummySelector.getInstance(), new Rule(
            CssProperties.BORDER_WIDTH, CssParser.parsePropertyValue(borderAttribute), false),
            Origin.HTMLTAG_PROPERTY));
      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value", e);
      }
    }

    if (node instanceof BODYElement) {
      if (!textAttribute.isEmpty()) {
        try {
          ruleSet.addHtmlTagPropertyRule(new CssRule(node, CssDummySelector.getInstance(),
              new Rule(CssProperties.COLOR, CssParser.parsePropertyValue(textAttribute), false),
              Origin.HTMLTAG_PROPERTY));
        } catch (final CSSParsingException e) {
          logger.debug("Error parsing value", e);
        }
      }
    }

    if (!typeAttribute.isEmpty()) {
      try {
        if (node instanceof ULElement || node instanceof OLElement || node instanceof LIElement) {
          LexicalUnit value;
          if (typeAttribute.equals("1")) {
            value = CssParser.parsePropertyValue("decimal");
          } else if (typeAttribute.equals("a")) {
            value = CssParser.parsePropertyValue("lower-alpha");
          } else if (typeAttribute.equals("A")) {
            value = CssParser.parsePropertyValue("upper-alpha");
          } else if (typeAttribute.equals("i")) {
            value = CssParser.parsePropertyValue("lower-roman");
          } else if (typeAttribute.equals("I")) {
            value = CssParser.parsePropertyValue("upper-roman");
          } else {
            value = CssParser.parsePropertyValue(typeAttribute);
          }
          ruleSet.addHtmlTagPropertyRule(new CssRule(node, CssDummySelector.getInstance(),
              new Rule(CssProperties.LIST_STYLE_TYPE, value, false), Origin.HTMLTAG_PROPERTY));

        }
      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value", e);
      }
    }
  }

  /**
   * Compute style rule set.
   * 
   * @param ruleSet the rule set
   * @param resolverState the resolver state
   */
  private void handleHtmlTagStyleProperty(final CssRuleSet ruleSet,
      final ResolverState resolverState) {
    final String style = resolverState.getElement().getAttribute(Attributes.STYLE);
    if (style != null) {
      List<Rule> styleTagRules;
      try {
        styleTagRules = CssParser.parseStyleAttribute(style);
        for (final Rule rule : styleTagRules) {
          final CssRule cssRule = new CssRule(resolverState.getElement(),
              CssDummySelector.getInstance(), rule, CssRule.Origin.HTMLTAG_STYLE_PROPERTY);
          if (rule.isImportant()) {
            ruleSet.addAuthorImportantRule(cssRule);
          } else {
            ruleSet.addAuthorNormalRule(cssRule);
          }
        }
      } catch (final RuntimeException | CSSParsingException e) {
        logger.warn("Can not parse style property", e);
      }
    }
  }

  /**
   * Compute user agent rule set.
   * 
   * @param ruleSet the rule set
   * @param resolverState the resolver state
   * @throws RecursiveTaskExecutorException
   */
  private void handleUserAgentRuleSet(final CssRuleSet ruleSet, final ResolverState resolverState)
      throws RecursiveTaskExecutorException {
    for (final SelectorRulesPair pair : getSelectorRulesPairs(resolverState.getElement()
        .getTagName(), userAgentStylesheet)) {
      if (cssMatcherTester.match(pair.getSelector(), resolverState)) {
        for (final Rule rule : pair.getRules()) {
          ruleSet.addUserAgentRule(new CssRule(null, pair.getSelector(), rule,
              CssRule.Origin.USER_AGENT));
        }
      }
    }
  }

  /**
   * Compute user rule set.
   * 
   * @param ruleSet the rule set
   * @param resolverState the resolver state
   * @throws RecursiveTaskExecutorException
   */
  private void handleUserRuleSet(final CssRuleSet ruleSet, final ResolverState resolverState)
      throws RecursiveTaskExecutorException {
    for (final SelectorRulesPair pair : getSelectorRulesPairs(resolverState.getElement()
        .getTagName(), userStylesheet)) {
      if (cssMatcherTester.match(pair.getSelector(), resolverState)) {
        for (final Rule rule : pair.getRules()) {
          if (rule.isImportant()) {
            ruleSet.addUserImportantRule(new CssRule(null, pair.getSelector(), rule,
                CssRule.Origin.USER));
          } else {
            ruleSet.addUserNormalRule(new CssRule(null, pair.getSelector(), rule,
                CssRule.Origin.USER));
          }
        }
      }
    }
  }

}
