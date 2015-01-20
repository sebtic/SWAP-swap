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
 * <http://www.gnu.org/licenses/>. $Id: CssMatcherTester.java 125 2012-04-04
 * 14:59:59Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.resolver;

import java.util.List;
import java.util.StringTokenizer;
import org.projectsforge.swap.core.mime.css.CssPseudoClasses;
import org.projectsforge.swap.core.mime.css.parser.conditions.CssAndCondition;
import org.projectsforge.swap.core.mime.css.parser.conditions.CssAttributeCondition;
import org.projectsforge.swap.core.mime.css.parser.conditions.CssBeginHyphenAttributeCondition;
import org.projectsforge.swap.core.mime.css.parser.conditions.CssClassCondition;
import org.projectsforge.swap.core.mime.css.parser.conditions.CssCondition;
import org.projectsforge.swap.core.mime.css.parser.conditions.CssIdCondition;
import org.projectsforge.swap.core.mime.css.parser.conditions.CssLangCondition;
import org.projectsforge.swap.core.mime.css.parser.conditions.CssOneOfAttributeCondition;
import org.projectsforge.swap.core.mime.css.parser.conditions.CssPseudoClassCondition;
import org.projectsforge.swap.core.mime.css.parser.selectors.CssChildSelector;
import org.projectsforge.swap.core.mime.css.parser.selectors.CssConditionalSelector;
import org.projectsforge.swap.core.mime.css.parser.selectors.CssDescendantSelector;
import org.projectsforge.swap.core.mime.css.parser.selectors.CssDirectAdjacentSelector;
import org.projectsforge.swap.core.mime.css.parser.selectors.CssElementSelector;
import org.projectsforge.swap.core.mime.css.parser.selectors.CssPseudoElementSelector;
import org.projectsforge.swap.core.mime.css.parser.selectors.CssSelector;
import org.projectsforge.swap.core.mime.html.nodes.elements.AElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.AbstractElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.Attributes;
import org.projectsforge.utils.icasestring.ICaseString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.Selector;

/**
 * A class used to check matching against CSS rules and a document.
 * 
 * @author Sébastien Aupetit
 */
@Component
public class CssMatcherTester {

  /** The logger. */
  private final Logger logger = LoggerFactory.getLogger(CssMatcherTester.class);

  private boolean match(final CssCondition condition, final ResolverState resolverState) {

    switch (condition.getConditionType()) {
      case Condition.SAC_AND_CONDITION:
        return visit((CssAndCondition) condition, resolverState);
      case Condition.SAC_ATTRIBUTE_CONDITION:
        return visit((CssAttributeCondition) condition, resolverState);
      case Condition.SAC_BEGIN_HYPHEN_ATTRIBUTE_CONDITION:
        return visit((CssBeginHyphenAttributeCondition) condition, resolverState);
      case Condition.SAC_CLASS_CONDITION:
        return visit((CssClassCondition) condition, resolverState);
      case Condition.SAC_ID_CONDITION:
        return visit((CssIdCondition) condition, resolverState);
      case Condition.SAC_LANG_CONDITION:
        return visit((CssLangCondition) condition, resolverState);
      case Condition.SAC_ONE_OF_ATTRIBUTE_CONDITION:
        return visit((CssOneOfAttributeCondition) condition, resolverState);
      case Condition.SAC_PSEUDO_CLASS_CONDITION:
        return visit((CssPseudoClassCondition) condition, resolverState);
      case Condition.SAC_NEGATIVE_CONDITION:
      case Condition.SAC_POSITIONAL_CONDITION:
      case Condition.SAC_ONLY_CHILD_CONDITION:
      case Condition.SAC_ONLY_TYPE_CONDITION:
      case Condition.SAC_CONTENT_CONDITION:
      default: {
        logger.warn("Not implemented CssCondition %s", condition.getConditionType());
        // accept by default
        return true;
      }
    }
  }

  /**
   * Recurse.
   * 
   * @param selector the selector
   * @param resolverState the resolver state
   * @return true, if successful
   */
  public boolean match(final CssSelector selector, final ResolverState resolverState) {
    switch (selector.getSelectorType()) {
      case Selector.SAC_CONDITIONAL_SELECTOR:
        return visit((CssConditionalSelector) selector, resolverState);
      case Selector.SAC_DESCENDANT_SELECTOR:
        return visit((CssDescendantSelector) selector, resolverState);
      case Selector.SAC_DIRECT_ADJACENT_SELECTOR:
        return visit((CssDirectAdjacentSelector) selector, resolverState);
      case Selector.SAC_CHILD_SELECTOR:
        return visit((CssChildSelector) selector, resolverState);
      case Selector.SAC_PSEUDO_ELEMENT_SELECTOR:
        return visit((CssPseudoElementSelector) selector, resolverState);
      case Selector.SAC_ELEMENT_NODE_SELECTOR:
        return visit((CssElementSelector) selector, resolverState);
      case Selector.SAC_ANY_NODE_SELECTOR:
      case Selector.SAC_ROOT_NODE_SELECTOR:
      case Selector.SAC_NEGATIVE_SELECTOR:
      case Selector.SAC_TEXT_NODE_SELECTOR:
      case Selector.SAC_CDATA_SECTION_NODE_SELECTOR:
      case Selector.SAC_PROCESSING_INSTRUCTION_NODE_SELECTOR:
      case Selector.SAC_COMMENT_NODE_SELECTOR:
      default: {
        logger.warn("Not implemented CssSelector %s", selector.getSelectorType());
        // match by default
        return true;
      }
    }
  }

  /**
   * Visit.
   * 
   * @param condition the condition
   * @param resolverState the resolver state
   * @return true, if visit
   */
  private boolean visit(final CssAndCondition condition, final ResolverState resolverState) {
    return match((CssCondition) condition.getFirstCondition(), resolverState)
        && match((CssCondition) condition.getSecondCondition(), resolverState);
  }

  /**
   * Visit.
   * 
   * @param condition the condition
   * @param resolverState the resolver state
   * @return true, if visit
   */
  private boolean visit(final CssAttributeCondition condition, final ResolverState resolverState) {
    final String value = resolverState.getElement().getAttribute(condition.getAttributeName());
    if (condition.getValue() == null) {
      return value != null && !value.isEmpty();
    } else {
      return value != null && value.equals(condition.getValue());
    }
  }

  /**
   * Visit.
   * 
   * @param condition the condition
   * @param resolverState the resolver state
   * @return true, if visit
   */
  private boolean visit(final CssBeginHyphenAttributeCondition condition,
      final ResolverState resolverState) {
    final String attributeValue = resolverState.getElement().getAttribute(
        condition.getAttributeName());
    return attributeValue != null && attributeValue.startsWith(condition.getValue());
  }

  /**
   * Visit.
   * 
   * @param selector the selector
   * @param resolverState the resolver state
   * @return true, if visit
   */
  private boolean visit(final CssChildSelector selector, final ResolverState resolverState) {
    if (resolverState.getParentResolverState() != null) {
      if (match((CssSelector) selector.getAncestorSelector(),
          resolverState.getParentResolverState())) {
        return match((CssSelector) selector.getSimpleSelector(), resolverState);
      }
    }
    return false;
  }

  /**
   * Visit.
   * 
   * @param condition the condition
   * @param resolverState the resolver state
   * @return true, if visit
   */
  private boolean visit(final CssClassCondition condition, final ResolverState resolverState) {
    final String className = resolverState.getElement().getAttribute(Attributes.CLASS);
    if (className != null) {
      final StringTokenizer tokenizer = new StringTokenizer(className);
      while (tokenizer.hasMoreTokens()) {
        if (tokenizer.nextToken().equalsIgnoreCase(condition.getValue())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Visit.
   * 
   * @param selector the selector
   * @param resolverState the resolver state
   * @return true, if visit
   */
  private boolean visit(final CssConditionalSelector selector, final ResolverState resolverState) {
    if (match((CssSelector) selector.getSimpleSelector(), resolverState)) {
      return match((CssCondition) selector.getCondition(), resolverState);
    } else {
      return false;
    }
  }

  /**
   * Visit.
   * 
   * @param selector the selector
   * @param resolverState the resolver state
   * @return true, if visit
   */
  private boolean visit(final CssDescendantSelector selector, final ResolverState resolverState) {
    if (match((CssSelector) selector.getSimpleSelector(), resolverState)) {
      ResolverState parent = resolverState.getParentResolverState();
      while (parent != null) {
        if (match((CssSelector) selector.getAncestorSelector(), parent)) {
          return true;
        }
        parent = parent.getParentResolverState();
      }
    }
    return false;
  }

  /**
   * Visit.
   * 
   * @param selector the selector
   * @param resolverState the resolver state
   * @return true, if visit
   */
  private boolean visit(final CssDirectAdjacentSelector selector, final ResolverState resolverState) {
    if (match((CssSelector) selector.getSiblingSelector(), resolverState)) {
      final AbstractElement previousSibling = resolverState.getElement().getPrevious(
          AbstractElement.class);
      if (previousSibling == null) {
        return false;
      } else {
        // Warning: a fake resolverstate is created for the previous element
        return match(
            (CssSelector) selector.getSelector(),
            new ResolverState(previousSibling, State.getState(0), resolverState
                .getParentResolverState()));
      }
    } else {
      return false;
    }

  }

  /**
   * Visit.
   * 
   * @param selector the selector
   * @param resolverState the resolver state
   * @return true, if visit
   */
  private boolean visit(final CssElementSelector selector, final ResolverState resolverState) {
    return (selector.getName() == null)
        || selector.getName().equals(resolverState.getElement().getTagName());
  }

  /**
   * Visit.
   * 
   * @param condition the condition
   * @param resolverState the resolver state
   * @return true, if visit
   */
  private boolean visit(final CssIdCondition condition, final ResolverState resolverState) {
    final String id = resolverState.getElement().getAttribute(Attributes.ID);
    return (id != null) && (id.equals(condition.getValue()));
  }

  /**
   * Visit.
   * 
   * @param condition the condition
   * @param resolverState the resolver state
   * @return true, if visit
   */
  private boolean visit(final CssLangCondition condition, final ResolverState resolverState) {
    String s = resolverState.getElement().getAttribute(Attributes.LANG);
    if (s != null) {
      s = s.toLowerCase();
    }
    final ICaseString lg = condition.getLangName();
    final ICaseString lh = condition.getLangHyphen();

    if (s != null
        && (s.equals((lg == null) ? "" : lg.getLowerCasedValue()) || s.startsWith((lh == null) ? ""
            : lh.getLowerCasedValue()))) {
      return true;
    }
    return false;
  }

  /**
   * Visit.
   * 
   * @param condition the condition
   * @param resolverState the resolver state
   * @return true, if visit
   */
  private boolean visit(final CssOneOfAttributeCondition condition,
      final ResolverState resolverState) {
    final String attr = resolverState.getElement().getAttribute(condition.getAttributeName());
    final String val = condition.getValue();

    final int i = attr.indexOf(val);
    if (i == -1) {
      return false;
    }
    if (i != 0 && !Character.isSpaceChar(attr.charAt(i - 1))) {
      return false;
    }
    final int j = i + val.length();
    return (j == attr.length() || (j < attr.length() && Character.isSpaceChar(attr.charAt(j))));
  }

  /**
   * Visit.
   * 
   * @param condition the condition
   * @param resolverState the resolver state
   * @return true, if visit
   */
  private boolean visit(final CssPseudoClassCondition condition, final ResolverState resolverState) {
    if (CssPseudoClasses.FIRST_CHILD.equalsIgnoreCase(condition.getValue())) {
      final AbstractElement parent = resolverState.getElement().getParent(AbstractElement.class);
      if (parent != null) {
        final List<AbstractElement> nodes = parent.getChildrenToLeaves(AbstractElement.class);
        if (nodes.size() > 0 && nodes.get(0) == resolverState.getElement()) {
          return true;
        } else {
          return false;
        }
      } else {
        return false;
      }
    } else if (CssPseudoClasses.LANG.equalsIgnoreCase(condition.getValue())) {
      final int s = condition.getValue().indexOf('(');
      if (s == -1) {
        return false;
      }
      final int e = condition.getValue().indexOf(')', s + 1);
      if (e == -1) {
        return false;
      }

      final String lang = condition.getValue().substring(s + 1, e - 1).toLowerCase();
      final String langHyphen = lang + '-';

      AbstractElement node = resolverState.getElement();

      while (node != null) {
        final String val = node.getAttribute(Attributes.LANG).toLowerCase();
        if (val != null && (val.equals(lang) || val.startsWith(langHyphen))) {
          return true;
        }
        node = node.getParent(AbstractElement.class);
      }
      return false;
    } else if (resolverState.getElement() instanceof AElement) {
      // c'est un <a></a>
      if (CssPseudoClasses.LINK.equalsIgnoreCase(condition.getValue())
          && !resolverState.getElementState().visited) {
        return true;
      } else if (CssPseudoClasses.VISITED.equalsIgnoreCase(condition.getValue())
          && resolverState.getElementState().visited) {
        return true;
      } else {
        return false;
      }
    } else if (CssPseudoClasses.ACTIVE.equalsIgnoreCase(condition.getValue())
        && resolverState.getElementState().active) {
      return true;
    } else if (CssPseudoClasses.FOCUS.equalsIgnoreCase(condition.getValue())
        && resolverState.getElementState().focused) {
      return true;
    } else if (CssPseudoClasses.HOVER.equalsIgnoreCase(condition.getValue())
        && resolverState.getElementState().hovered) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Visit.
   * 
   * @param selector the selector
   * @param resolverState the resolver state
   * @return true, if visit
   */
  private boolean visit(final CssPseudoElementSelector selector, final ResolverState resolverState) {
    // TODO not implemented
    return true;
  }
}
