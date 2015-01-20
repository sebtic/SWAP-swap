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
 * <http://www.gnu.org/licenses/>. $Id: MatcherVisitor.java 125 2012-04-04
 * 14:59:59Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.resolver.ruleset;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import org.projectsforge.swap.core.mime.css.CssPseudoClasses;
import org.projectsforge.swap.core.mime.css.parser.conditions.CssAndCondition;
import org.projectsforge.swap.core.mime.css.parser.conditions.CssAttributeCondition;
import org.projectsforge.swap.core.mime.css.parser.conditions.CssBeginHyphenAttributeCondition;
import org.projectsforge.swap.core.mime.css.parser.conditions.CssClassCondition;
import org.projectsforge.swap.core.mime.css.parser.conditions.CssIdCondition;
import org.projectsforge.swap.core.mime.css.parser.conditions.CssLangCondition;
import org.projectsforge.swap.core.mime.css.parser.conditions.CssOneOfAttributeCondition;
import org.projectsforge.swap.core.mime.css.parser.conditions.CssPseudoClassCondition;
import org.projectsforge.swap.core.mime.css.parser.selectors.CssChildSelector;
import org.projectsforge.swap.core.mime.css.parser.selectors.CssConditionalSelector;
import org.projectsforge.swap.core.mime.css.parser.selectors.CssDescendantSelector;
import org.projectsforge.swap.core.mime.css.parser.selectors.CssDirectAdjacentSelector;
import org.projectsforge.swap.core.mime.css.parser.selectors.CssDummySelector;
import org.projectsforge.swap.core.mime.css.parser.selectors.CssElementSelector;
import org.projectsforge.swap.core.mime.css.parser.selectors.CssPseudoElementSelector;
import org.projectsforge.swap.core.mime.html.nodes.Document;
import org.projectsforge.swap.core.mime.html.nodes.elements.AElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.AbstractElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.Attributes;
import org.projectsforge.utils.icasestring.ICaseString;
import org.projectsforge.utils.visitor.VisitingMode;
import org.projectsforge.utils.visitor.Visitor;

/**
 * A visitor class to check matching against CSS rules and a document.
 */
public class MatcherVisitor extends Visitor<Object, Void, Boolean> {

  /** The document. */
  private final Document document;

  /** The element. */
  private AbstractElement element;

  /** The pseudo elements. */
  private Set<ICaseString> pseudoElements;

  /**
   * The Constructor.
   * 
   * @param document the document
   * @param element the element
   * @param pseudoElements the pseudo elements
   */
  public MatcherVisitor(final Document document, final AbstractElement element,
      final Set<ICaseString> pseudoElements) {
    // TODO change to a parallel visitor
    super(VisitingMode.PARALLEL);
    this.document = document;
    this.element = element;
    this.pseudoElements = pseudoElements;
  }

  /**
   * Visit.
   * 
   * @param condition the condition
   * @return true, if visit
   */
  public boolean visit(final CssAndCondition condition) {
    final Boolean first = recurse(condition.getFirstCondition());
    final Boolean second = recurse(condition.getSecondCondition());
    return first && second;
  }

  /**
   * Visit.
   * 
   * @param condition the condition
   * @return true, if visit
   */
  public boolean visit(final CssAttributeCondition condition) {
    final String value = element.getAttributes().get(condition.getAttributeName());
    if (condition.getValue() == null) {
      return value != null && !value.equals("");
    } else {
      return value != null && value.equals(condition.getValue());
    }
  }

  /**
   * Visit.
   * 
   * @param condition the condition
   * @return true, if visit
   */
  public boolean visit(final CssBeginHyphenAttributeCondition condition) {
    final String attributeValue = element.getAttributes().get(condition.getAttributeName());
    return attributeValue != null && attributeValue.startsWith(condition.getValue());
  }

  /**
   * Visit.
   * 
   * @param selector the selector
   * @return true, if visit
   */
  public boolean visit(final CssChildSelector selector) {
    final AbstractElement parentElement = document.getStackCollectorVisitor().getParent(element,
        AbstractElement.class);

    if (parentElement != null) {
      final AbstractElement oldElement = element;
      final Set<ICaseString> oldPseudoElements = pseudoElements;

      element = parentElement;
      pseudoElements = null;
      final Boolean ancestor = recurse(selector.getAncestorSelector());

      element = oldElement;
      pseudoElements = oldPseudoElements;
      final Boolean simple = recurse(selector.getSimpleSelector());

      return ancestor && simple;
    }
    return false;
  }

  /**
   * Visit.
   * 
   * @param condition the condition
   * @return true, if visit
   */
  public boolean visit(final CssClassCondition condition) {
    final String className = element.getAttributes().get(Attributes.CLASS);
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
   * @return true, if visit
   */
  public boolean visit(final CssConditionalSelector selector) {
    final Boolean simple = recurse(selector.getSimpleSelector());
    final Boolean condition = recurse(selector.getCondition());

    return simple && condition;
  }

  /**
   * Visit.
   * 
   * @param selector the selector
   * @return true, if visit
   */
  public boolean visit(final CssDescendantSelector selector) {
    final Boolean simple = recurse(selector.getSimpleSelector());

    if (simple) {
      AbstractElement parent = document.getStackCollectorVisitor().getParent(element,
          AbstractElement.class);
      while (parent != null) {
        final AbstractElement oldElement = element;
        final Set<ICaseString> oldPseudoElements = pseudoElements;

        element = parent;
        pseudoElements = null;
        final Boolean condition = recurse(selector.getAncestorSelector());
        element = oldElement;
        pseudoElements = oldPseudoElements;

        if (condition) {
          return true;
        }
        parent = document.getStackCollectorVisitor().getParent(parent, AbstractElement.class);
      }
    }
    return false;
  }

  /**
   * Visit.
   * 
   * @param selector the selector
   * @return true, if visit
   */
  public boolean visit(final CssDirectAdjacentSelector selector) {
    final Boolean sibling = recurse(selector.getSiblingSelector());

    if (sibling) {
      final AbstractElement previousSibling = document.getStackCollectorVisitor()
          .getPreviousSibling(element, AbstractElement.class, AbstractElement.class);
      if (previousSibling == null) {
        return false;
      } else {
        final AbstractElement oldElement = element;
        final Set<ICaseString> oldPseudoElements = pseudoElements;

        element = previousSibling;
        pseudoElements = null;
        final Boolean adjacent = recurse(selector.getSelector());
        element = oldElement;
        pseudoElements = oldPseudoElements;

        return adjacent;
      }
    } else {
      return false;
    }

  }

  /**
   * Visit.
   * 
   * @param selector the selector
   * @return true, if visit
   */
  public boolean visit(final CssDummySelector selector) {
    return true;
  }

  /**
   * Visit.
   * 
   * @param selector the selector
   * @return true, if visit
   */
  public boolean visit(final CssElementSelector selector) {
    return (selector.getName() == null) || selector.getName().equals(element.getName());
  }

  /**
   * Visit.
   * 
   * @param condition the condition
   * @return true, if visit
   */
  public boolean visit(final CssIdCondition condition) {
    final String id = element.getAttributes().get(Attributes.ID);
    return (id != null) && (id.equals(condition.getValue()));
  }

  /**
   * Visit.
   * 
   * @param condition the condition
   * @return true, if visit
   */
  public boolean visit(final CssLangCondition condition) {
    String s = element.getAttributes().get(Attributes.LANG);
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
   * @return true, if visit
   */
  public boolean visit(final CssOneOfAttributeCondition condition) {
    final String attr = element.getAttributes().get(condition.getAttributeName());
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
   * @return true, if visit
   */
  public boolean visit(final CssPseudoClassCondition condition) {
    if (CssPseudoClasses.FIRST_CHILD.equalsIgnoreCase(condition.getValue())) {
      final AbstractElement parent = document.getStackCollectorVisitor().getParent(element,
          AbstractElement.class);
      if (parent != null) {
        final List<AbstractElement> nodes = new ArrayList<AbstractElement>();
        parent.getChildren(AbstractElement.class, nodes);
        if (nodes.size() > 0 && nodes.get(0) == element) {
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

      AbstractElement node = element;

      while (node != null) {
        final String val = node.getAttributes().get(Attributes.LANG).toLowerCase();
        if (val != null && (val.equals(lang) || val.startsWith(langHyphen))) {
          return true;
        }
        node = document.getStackCollectorVisitor().getParent(node, AbstractElement.class);
      }
      return false;
    } else if (element instanceof AElement) {
      // c'est un <a></a>
      if (CssPseudoClasses.LINK.equalsIgnoreCase(condition.getValue())
          && !((AElement) element).hasBeenVisited()) {
        return true;
      } else if (CssPseudoClasses.VISITED.equalsIgnoreCase(condition.getValue())
          && ((AElement) element).hasBeenVisited()) {
        return true;
      } else {
        return false;
      }
    } else if (CssPseudoClasses.ACTIVE.equalsIgnoreCase(condition.getValue()) && element.isActive()) {
      return true;
    } else if (CssPseudoClasses.FOCUS.equalsIgnoreCase(condition.getValue()) && element.isFocused()) {
      return true;
    } else if (CssPseudoClasses.HOVER.equalsIgnoreCase(condition.getValue()) && element.isHovered()) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Visit.
   * 
   * @param selector the selector
   * @return true, if visit
   */
  public boolean visit(final CssPseudoElementSelector selector) {
    return pseudoElements != null && pseudoElements.contains(selector.getName());
  }
}
