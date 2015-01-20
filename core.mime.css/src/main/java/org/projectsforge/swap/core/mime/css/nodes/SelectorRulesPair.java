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
 * <http://www.gnu.org/licenses/>. $Id: SelectorRulesPair.java 91 2011-07-18
 * 16:28:31Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.nodes;

import java.util.List;
import org.projectsforge.swap.core.mime.css.parser.selectors.CssSelector;

/**
 * The class representing the association between a selector and many CSS rules.
 * 
 * @author Sébastien Aupetit
 */
public class SelectorRulesPair implements CssElement {

  private static Rule[] EMPTY_RULE_ARRAY = new Rule[0];

  /** The selector. */
  private final CssSelector selector;

  /** The rules. */
  private final Rule[] rules;

  /**
   * Instantiates a new selector rules pair.
   * 
   * @param selector the selector
   * @param rules the rules
   */
  public SelectorRulesPair(final CssSelector selector, final List<Rule> rules) {
    this.selector = selector;
    this.rules = rules.toArray(EMPTY_RULE_ARRAY);
  }

  /**
   * Gets the rules.
   * 
   * @return the rules
   */
  public Rule[] getRules() {
    return rules;
  }

  /**
   * Gets the selector.
   * 
   * @return the selector
   */
  public CssSelector getSelector() {
    return selector;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append(selector).append(" { ");
    for (final Rule rule : rules) {
      sb.append(rule).append(" ");
    }
    sb.append("}\n");
    return sb.toString();
  }

}
