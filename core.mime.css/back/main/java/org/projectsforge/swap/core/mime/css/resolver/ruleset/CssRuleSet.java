/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr>
 * 
 * This file is part of SWAP.
 * 
 * SWAP is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * SWAP is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with SWAP. If not, see <http://www.gnu.org/licenses/>.
 * 
 * $Id: CssRuleSet.java 91 2011-07-18 16:28:31Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.resolver.ruleset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.projectsforge.swap.core.mime.css.nodes.Rule;
import org.projectsforge.swap.core.mime.css.parser.selectors.CssDummySelector;
import org.projectsforge.swap.core.mime.css.parser.selectors.CssSelector;
import org.projectsforge.swap.core.mime.html.nodes.elements.AbstractElement;

/**
 * The class storing a set of {@link CssRule} managing properly the origin and
 * the priorities.
 * 
 * @author Sébastien Aupetit
 */
public class CssRuleSet {
  // More specific rule are at the end
  /** The Constant comparator. */
  private static final CssRuleComparator comparator = new CssRuleComparator();

  /** The Constant dummySelector. */
  private static final CssSelector dummySelector = new CssDummySelector();

  /** The user agent rule set. */
  private List<CssRule> userAgentRuleSet = new ArrayList<CssRule>();

  /** The user normal rule set. */
  private List<CssRule> userNormalRuleSet = new ArrayList<CssRule>();

  /** The author normal rule set. */
  private List<CssRule> authorNormalRuleSet = new ArrayList<CssRule>();

  /** The author important rule set. */
  private List<CssRule> authorImportantRuleSet = new ArrayList<CssRule>();

  /** The user important rule set. */
  private List<CssRule> userImportantRuleSet = new ArrayList<CssRule>();

  /** The tag rule set. */
  private List<CssRule> tagRuleSet = new ArrayList<CssRule>();

  /** The result. */
  private final List<CssRule> result = new ArrayList<CssRule>();

  /** The finalized. */
  private boolean finalized = false;

  /**
   * Adds the author important rule.
   * 
   * @param rule
   *          the rule
   */
  public void addAuthorImportantRule(final CssRule rule) {
    if (finalized) {
      throw new IllegalStateException("Can not add rule to a finalized rule set");
    }
    authorImportantRuleSet.add(rule);
  }

  /**
   * Adds the author normal rule.
   * 
   * @param rule
   *          the rule
   */
  public void addAuthorNormalRule(final CssRule rule) {
    if (finalized) {
      throw new IllegalStateException("Can not add rule to a finalized rule set");
    }
    authorNormalRuleSet.add(rule);
  }

  /**
   * Adds the tag rule.
   * 
   * @param element
   *          the element
   * @param rule
   *          the rule
   */
  public void addTagRule(final AbstractElement element, final Rule rule) {
    if (finalized) {
      throw new IllegalStateException("Can not add rule to a finalized rule set");
    }
    tagRuleSet.add(new CssRule(element, CssRuleSet.dummySelector, rule));
  }

  /**
   * Adds the user agent rule.
   * 
   * @param rule
   *          the rule
   */
  public void addUserAgentRule(final CssRule rule) {
    if (finalized) {
      throw new IllegalStateException("Can not add rule to a finalized rule set");
    }
    userAgentRuleSet.add(rule);
  }

  /**
   * Adds the user important rule.
   * 
   * @param rule
   *          the rule
   */
  public void addUserImportantRule(final CssRule rule) {
    if (finalized) {
      throw new IllegalStateException("Can not add rule to a finalized rule set");
    }
    userImportantRuleSet.add(rule);
  }

  /**
   * Adds the user normal rule.
   * 
   * @param rule
   *          the rule
   */
  public void addUserNormalRule(final CssRule rule) {
    if (finalized) {
      throw new IllegalStateException("Can not add rule to a finalized rule set");
    }
    userNormalRuleSet.add(rule);
  }

  /**
   * Gets the combined rules ordered by specificities and origins.
   * 
   * @return the rules
   */
  public List<CssRule> getRules() {
    if (!finalized) {
      finalized = true;

      Collections.sort(userAgentRuleSet, CssRuleSet.comparator);
      Collections.sort(userNormalRuleSet, CssRuleSet.comparator);
      Collections.sort(authorNormalRuleSet, CssRuleSet.comparator);
      Collections.sort(authorImportantRuleSet, CssRuleSet.comparator);
      Collections.sort(userImportantRuleSet, CssRuleSet.comparator);

      result.addAll(userAgentRuleSet);
      result.addAll(userNormalRuleSet);
      result.addAll(authorNormalRuleSet);
      result.addAll(authorImportantRuleSet);
      result.addAll(userImportantRuleSet);
      result.addAll(tagRuleSet);

      // garbage collect
      userAgentRuleSet = null;
      userNormalRuleSet = null;
      userImportantRuleSet = null;
      authorImportantRuleSet = null;
      authorNormalRuleSet = null;
      tagRuleSet = null;
    }
    return result;
  }

}
