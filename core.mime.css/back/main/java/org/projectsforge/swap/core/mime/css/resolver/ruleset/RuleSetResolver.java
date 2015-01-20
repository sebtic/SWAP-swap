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
package org.projectsforge.swap.core.mime.css.resolver.ruleset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.projectsforge.swap.core.mime.css.CssProperties;
import org.projectsforge.swap.core.mime.css.nodes.CssElement;
import org.projectsforge.swap.core.mime.css.nodes.Media;
import org.projectsforge.swap.core.mime.css.nodes.Rule;
import org.projectsforge.swap.core.mime.css.nodes.SelectorRulesPair;
import org.projectsforge.swap.core.mime.css.nodes.Stylesheet;
import org.projectsforge.swap.core.mime.css.parser.CSSParsingException;
import org.projectsforge.swap.core.mime.css.parser.CssParser;
import org.projectsforge.swap.core.mime.css.parser.selectors.CssDummySelector;
import org.projectsforge.swap.core.mime.css.parser.selectors.CssSelector;
import org.projectsforge.swap.core.mime.html.nodes.Document;
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
import org.slf4j.Logger;
import org.w3c.css.sac.LexicalUnit;

/**
 * The class used to compute the applicable rule set to an element taking care
 * of recording origins for properly applying ordering of rules according to
 * standards.
 * 
 * @author Sébastien Aupetit
 */
public class RuleSetResolver {

  /** The logger. */
  private final Logger logger = org.slf4j.LoggerFactory.getLogger(RuleSetResolver.class);

  /** The author stylesheet. */
  private final List<Stylesheet> authorStylesheet;

  /** The user agent stylesheet. */
  private final Stylesheet userAgentStylesheet;

  /** The user stylesheet. */
  private final Stylesheet userStylesheet;

  /** The media. */
  private final ICaseString media;

  /** The document. */
  private final Document document;

  /**
   * Instantiates a new rule set resolver.
   * 
   * @param document the document
   * @param authorStylesheet the author stylesheet
   * @param userAgentStylesheet the user agent stylesheet
   * @param userStylesheet the user stylesheet
   * @param media the media
   */
  public RuleSetResolver(final Document document, final List<Stylesheet> authorStylesheet,
      final Stylesheet userAgentStylesheet, final Stylesheet userStylesheet, final ICaseString media) {
    this.document = document;
    this.authorStylesheet = authorStylesheet;
    this.userAgentStylesheet = userAgentStylesheet;
    this.userStylesheet = userStylesheet;
    this.media = media;
  }

  /**
   * Compute author rule set.
   * 
   * @param ruleSet the rule set
   * @param node the node
   * @param pseudoElements the pseudo elements
   */
  private void computeAuthorRuleSet(final CssRuleSet ruleSet, final AbstractElement node,
      final Set<ICaseString> pseudoElements) {
    for (final Stylesheet css : authorStylesheet) {
      for (final SelectorRulesPair pair : getMatchingRules(css, node, pseudoElements)) {
        for (final Rule rule : pair.getRules()) {
          if (rule.isImportant()) {
            ruleSet.addAuthorImportantRule(new CssRule(pair.getSelector(), rule,
                CssRule.Origin.AUTHOR));
          } else {
            ruleSet
                .addAuthorNormalRule(new CssRule(pair.getSelector(), rule, CssRule.Origin.AUTHOR));
          }
        }
      }
    }
  }

  /**
   * Compute style rule set.
   * 
   * @param ruleSet the rule set
   * @param node the node
   * @param pseudoElements the pseudo elements
   */
  private void computeStyleRuleSet(final CssRuleSet ruleSet, final AbstractElement node,
      final Set<ICaseString> pseudoElements) {
    final String style = node.getAttributes().get(Attributes.STYLE);
    if (style != null) {
      List<Rule> styleTagRules;
      try {
        styleTagRules = CssParser.parseStyleTag(style);
        final CssSelector selector = new CssDummySelector();
        for (final Rule rule : styleTagRules) {
          if (rule.isImportant()) {
            ruleSet
                .addAuthorImportantRule(new CssRule(selector, rule, CssRule.Origin.AUTHOR_STYLE));
          } else {
            ruleSet.addAuthorNormalRule(new CssRule(selector, rule, CssRule.Origin.AUTHOR_STYLE));
          }
        }
      } catch (final RuntimeException | CSSParsingException | IOException e) {
        logger.warn("Can not parse style property", e);
      }
    }
  }

  /**
   * Compute tag rule set.
   * 
   * @param ruleSet the rule set
   * @param node the node
   * @param pseudoElements the pseudo elements
   */
  private void computeTagRuleSet(final CssRuleSet ruleSet, final AbstractElement node,
      final Set<ICaseString> pseudoElements) {
    final String heightAttribute = node.getAttributes().get(Attributes.HEIGHT);
    final String widthAttribute = node.getAttributes().get(Attributes.WIDTH);
    final String hspaceAttribute = node.getAttributes().get(Attributes.HSPACE);
    final String vspaceAttribute = node.getAttributes().get(Attributes.VSPACE);
    final String colorAttribute = node.getAttributes().get(Attributes.COLOR);
    final String bgcolorAttribute = node.getAttributes().get(Attributes.BGCOLOR);
    final String backgroundAttribute = node.getAttributes().get(Attributes.BACKGROUND);
    final String sizeAttribute = node.getAttributes().get(Attributes.SIZE);
    final String faceAttribute = node.getAttributes().get(Attributes.FACE);
    final String alignAttribute = node.getAttributes().get(Attributes.ALIGN);
    final String valignAttribute = node.getAttributes().get(Attributes.VALIGN);
    final String nowrapAttribute = node.getAttributes().get(Attributes.NOWRAP);
    final String clearAttribute = node.getAttributes().get(Attributes.CLEAR);
    final String marginheightAttribute = node.getAttributes().get(Attributes.MARGINHEIGHT);
    final String marginwidthAttribute = node.getAttributes().get(Attributes.MARGINWIDTH);
    final String borderAttribute = node.getAttributes().get(Attributes.BORDER);
    final String textAttribute = node.getAttributes().get(Attributes.TEXT);
    String typeAttribute = node.getAttributes().get(Attributes.TYPE);

    if (heightAttribute != null) {
      try {
        ruleSet.addTagRule(node,
            new Rule(CssProperties.HEIGHT, CssParser.parsePropertyValue(heightAttribute), false));
      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value", e);
      } catch (final IOException e) {
        logger.debug("Error parsing value", e);
      }
    }

    if (widthAttribute != null) {
      try {
        ruleSet.addTagRule(node,
            new Rule(CssProperties.WIDTH, CssParser.parsePropertyValue(widthAttribute), false));
      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value " + widthAttribute + " for " + node, e);
      } catch (final IOException e) {
        logger.debug("Error parsing value " + widthAttribute + " for " + node, e);
      }
    }

    if (hspaceAttribute != null) {
      try {
        final LexicalUnit value = CssParser.parsePropertyValue(hspaceAttribute);
        ruleSet.addTagRule(node, new Rule(CssProperties.MARGIN_LEFT, value, false));
        ruleSet.addTagRule(node, new Rule(CssProperties.MARGIN_RIGHT, value, false));
      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value", e);
      } catch (final IOException e) {
        logger.debug("Error parsing value", e);
      }
    }

    if (vspaceAttribute != null) {
      try {
        final LexicalUnit value = CssParser.parsePropertyValue(vspaceAttribute);

        ruleSet.addTagRule(node, new Rule(CssProperties.MARGIN_TOP, value, false));
        ruleSet.addTagRule(node, new Rule(CssProperties.MARGIN_BOTTOM, value, false));
      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value", e);
      } catch (final IOException e) {
        logger.debug("Error parsing value", e);
      }
    }

    if (colorAttribute != null) {
      try {
        ruleSet.addTagRule(node,
            new Rule(CssProperties.COLOR, CssParser.parsePropertyValue(colorAttribute), false));
      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value", e);
      } catch (final IOException e) {
        logger.debug("Error parsing value", e);
      }
    }

    if (bgcolorAttribute != null) {
      try {
        ruleSet.addTagRule(node,
            new Rule(CssProperties.BACKGROUND_COLOR,
                CssParser.parsePropertyValue(bgcolorAttribute), false));
      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value", e);
      } catch (final IOException e) {
        logger.debug("Error parsing value", e);
      }
    }

    if (backgroundAttribute != null) {
      try {
        // TODO il y a un problème ici
        ruleSet.addTagRule(
            node,
            new Rule(CssProperties.BACKGROUND_IMAGE, CssParser.parsePropertyValue("url("
                + backgroundAttribute + ")"), false));
      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value", e);
      } catch (final IOException e) {
        logger.debug("Error parsing value", e);
      }
    }

    if (sizeAttribute != null) {
      try {
        if (node instanceof FONTElement) {
          ruleSet
              .addTagRule(node,
                  new Rule(CssProperties.FONT_SIZE, CssParser.parsePropertyValue(sizeAttribute),
                      false));
        } else if (node instanceof HRElement) {
          ruleSet.addTagRule(node,
              new Rule(CssProperties.HEIGHT, CssParser.parsePropertyValue(sizeAttribute), false));
        }
      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value", e);
      } catch (final IOException e) {
        logger.debug("Error parsing value", e);
      }
    }

    if (faceAttribute != null) {
      try {
        ruleSet
            .addTagRule(node,
                new Rule(CssProperties.FONT_FAMILY, CssParser.parsePropertyValue(faceAttribute),
                    false));
      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value", e);
      } catch (final IOException e) {
        logger.debug("Error parsing value", e);
      }
    }

    if (alignAttribute != null) {
      try {
        ruleSet
            .addTagRule(node,
                new Rule(CssProperties.TEXT_ALIGN, CssParser.parsePropertyValue(alignAttribute),
                    false));
      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value", e);
      } catch (final IOException e) {
        logger.debug("Error parsing value", e);
      }
    }

    if (valignAttribute != null) {
      try {
        ruleSet.addTagRule(node,
            new Rule(CssProperties.VERTICAL_ALIGN, CssParser.parsePropertyValue(valignAttribute),
                false));
      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value", e);
      } catch (final IOException e) {
        logger.debug("Error parsing value", e);
      }
    }

    if (nowrapAttribute != null) {
      try {
        ruleSet.addTagRule(node,
            new Rule(CssProperties.WHITE_SPACE, CssParser.parsePropertyValue("nowrap"), false));
      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value", e);
      } catch (final IOException e) {
        logger.debug("Error parsing value", e);
      }
    }

    if (clearAttribute != null) {
      try {
        if (clearAttribute.equalsIgnoreCase("all")) {
          ruleSet.addTagRule(node,
              new Rule(CssProperties.CLEAR, CssParser.parsePropertyValue("both"), false));
        } else {
          ruleSet.addTagRule(node,
              new Rule(CssProperties.CLEAR, CssParser.parsePropertyValue(clearAttribute), false));
        }

      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value", e);
      } catch (final IOException e) {
        logger.debug("Error parsing value", e);
      }
    }

    if (marginheightAttribute != null) {
      if (node instanceof IFRAMEElement) {
        try {
          final LexicalUnit value = CssParser.parsePropertyValue(marginheightAttribute);

          ruleSet.addTagRule(node, new Rule(CssProperties.MARGIN_TOP, value, false));
          ruleSet.addTagRule(node, new Rule(CssProperties.MARGIN_BOTTOM, value, false));
        } catch (final CSSParsingException e) {
          logger.debug("Error parsing value", e);
        } catch (final IOException e) {
          logger.debug("Error parsing value", e);
        }
      }
    }

    if (marginwidthAttribute != null) {
      if (node instanceof IFRAMEElement) {
        try {
          final LexicalUnit value = CssParser.parsePropertyValue(marginwidthAttribute);

          ruleSet.addTagRule(node, new Rule(CssProperties.MARGIN_LEFT, value, false));
          ruleSet.addTagRule(node, new Rule(CssProperties.MARGIN_RIGHT, value, false));
        } catch (final CSSParsingException e) {
          logger.debug("Error parsing value", e);
        } catch (final IOException e) {
          logger.debug("Error parsing value", e);
        }
      }
    }

    if (borderAttribute != null) {
      try {
        ruleSet.addTagRule(node,
            new Rule(CssProperties.BORDER_WIDTH, CssParser.parsePropertyValue(borderAttribute),
                false));
      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value", e);
      } catch (final IOException e) {
        logger.debug("Error parsing value", e);
      }
    }

    if (node instanceof BODYElement) {
      if (textAttribute != null) {
        try {
          ruleSet.addTagRule(node,
              new Rule(CssProperties.COLOR, CssParser.parsePropertyValue(textAttribute), false));
        } catch (final CSSParsingException e) {
          logger.debug("Error parsing value", e);
        } catch (final IOException e) {
          logger.debug("Error parsing value", e);
        }
      }
    }

    if (typeAttribute != null) {
      try {
        if (node instanceof ULElement || node instanceof OLElement || node instanceof LIElement) {
          typeAttribute = typeAttribute.trim();
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
          ruleSet.addTagRule(node, new Rule(CssProperties.LIST_STYLE_TYPE, value, false));

        }
      } catch (final CSSParsingException e) {
        logger.debug("Error parsing value", e);
      } catch (final IOException e) {
        logger.debug("Error parsing value", e);
      }
    }
  }

  /**
   * Compute user agent rule set.
   * 
   * @param ruleSet the rule set
   * @param node the node
   * @param pseudoElements the pseudo elements
   */
  private void computeUserAgentRuleSet(final CssRuleSet ruleSet, final AbstractElement node,
      final Set<ICaseString> pseudoElements) {
    for (final SelectorRulesPair pair : getMatchingRules(userAgentStylesheet, node, pseudoElements)) {
      for (final Rule rule : pair.getRules()) {
        ruleSet.addUserAgentRule(new CssRule(pair.getSelector(), rule, CssRule.Origin.USER_AGENT));
      }
    }
  }

  /**
   * Compute user rule set.
   * 
   * @param ruleSet the rule set
   * @param node the node
   * @param pseudoElements the pseudo elements
   */
  private void computeUserRuleSet(final CssRuleSet ruleSet, final AbstractElement node,
      final Set<ICaseString> pseudoElements) {
    for (final SelectorRulesPair pair : getMatchingRules(userStylesheet, node, pseudoElements)) {
      for (final Rule rule : pair.getRules()) {
        if (rule.isImportant()) {
          ruleSet.addUserImportantRule(new CssRule(pair.getSelector(), rule, CssRule.Origin.USER));
        } else {
          ruleSet.addUserNormalRule(new CssRule(pair.getSelector(), rule, CssRule.Origin.USER));
        }
      }
    }
  }

  /**
   * Gets the matching rules.
   * 
   * @param css the css
   * @param node the node
   * @param pseudoElements the pseudo elements
   * @return the matching rules
   */
  private List<SelectorRulesPair> getMatchingRules(final Stylesheet css,
      final AbstractElement node, final Set<ICaseString> pseudoElements) {
    final List<SelectorRulesPair> rules = new ArrayList<SelectorRulesPair>();

    // always consider 'all' media rules
    for (final Element element : css.getMedia(Media.ALL).getElements()) {
      if (element instanceof SelectorRulesPair) {
        final SelectorRulesPair pair = (SelectorRulesPair) element;
        final Boolean match = new MatcherVisitor(document, node, pseudoElements).recurse(pair
            .getSelector());
        if (match) {
          rules.add(pair);
        }
      }
    }

    if (!Media.ALL.equals(media)) {
      for (final Element element : css.getMedia(media).getElements()) {
        if (element instanceof SelectorRulesPair) {
          final SelectorRulesPair pair = (SelectorRulesPair) element;
          final Boolean match = new MatcherVisitor(document, node, pseudoElements).recurse(pair
              .getSelector());
          if (match) {
            rules.add(pair);
          }
        }
      }
    }
    return rules;
  }

  /**
   * Gets the rule set.
   * 
   * @param node the node
   * @param pseudoElements the pseudo elements
   * @return the rule set
   */
  public CssRuleSet getRuleSet(final AbstractElement node, final Set<ICaseString> pseudoElements) {
    final CssRuleSet ruleSet = new CssRuleSet();

    computeUserAgentRuleSet(ruleSet, node, pseudoElements);
    computeUserRuleSet(ruleSet, node, pseudoElements);
    computeAuthorRuleSet(ruleSet, node, pseudoElements);
    computeStyleRuleSet(ruleSet, node, pseudoElements);
    computeTagRuleSet(ruleSet, node, pseudoElements);

    return ruleSet;
  }

}
