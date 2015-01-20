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
 * <http://www.gnu.org/licenses/>. $Id: CssRuleSet.java 91 2011-07-18 16:28:31Z
 * sebtic $
 */
package org.projectsforge.swap.core.mime.css.resolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * The class storing a set of {@link CssRule} managing properly the origin and
 * the priorities.
 * 
 * @author Sébastien Aupetit
 */
public class CssRuleSet {

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
  private CssRule[] result = null;

  private static CssRule[] EMPTY_CSS_RULE_ARRAY = new CssRule[0];

  /**
   * Adds the author important rule.
   * 
   * @param rule the rule
   */
  public void addAuthorImportantRule(final CssRule rule) {
    if (result != null) {
      throw new IllegalStateException("Can not add rule to a finalized rule set");
    }
    authorImportantRuleSet.add(rule);
  }

  /**
   * Adds the author normal rule.
   * 
   * @param rule the rule
   */
  public void addAuthorNormalRule(final CssRule rule) {
    if (result != null) {
      throw new IllegalStateException("Can not add rule to a finalized rule set");
    }
    authorNormalRuleSet.add(rule);
  }

  /**
   * Adds the tag rule.
   * 
   * @param rule the rule
   */
  public void addHtmlTagPropertyRule(final CssRule rule) {
    if (result != null) {
      throw new IllegalStateException("Can not add rule to a finalized rule set");
    }
    tagRuleSet.add(rule);
  }

  /**
   * Adds the user agent rule.
   * 
   * @param rule the rule
   */
  public void addUserAgentRule(final CssRule rule) {
    if (result != null) {
      throw new IllegalStateException("Can not add rule to a finalized rule set");
    }
    userAgentRuleSet.add(rule);
  }

  /**
   * Adds the user important rule.
   * 
   * @param rule the rule
   */
  public void addUserImportantRule(final CssRule rule) {
    if (result != null) {
      throw new IllegalStateException("Can not add rule to a finalized rule set");
    }
    userImportantRuleSet.add(rule);
  }

  /**
   * Adds the user normal rule.
   * 
   * @param rule the rule
   */
  public void addUserNormalRule(final CssRule rule) {
    if (result != null) {
      throw new IllegalStateException("Can not add rule to a finalized rule set");
    }
    userNormalRuleSet.add(rule);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    throw new IllegalStateException("Not comparable");
  }

  /**
   * Gets the combined rules ordered by specificities and origins.
   * 
   * @return the rules
   */
  public CssRule[] getRules() {
    if (result == null) {

      Collections.sort(userAgentRuleSet);
      Collections.sort(userNormalRuleSet);
      Collections.sort(authorNormalRuleSet);
      Collections.sort(authorImportantRuleSet);
      Collections.sort(userImportantRuleSet);

      final ArrayList<CssRule> allRules = new ArrayList<>();

      allRules.addAll(userAgentRuleSet);
      allRules.addAll(userNormalRuleSet);
      allRules.addAll(authorNormalRuleSet);
      allRules.addAll(authorImportantRuleSet);
      allRules.addAll(userImportantRuleSet);
      allRules.addAll(tagRuleSet);

      // garbage collect
      userAgentRuleSet = null;
      userNormalRuleSet = null;
      userImportantRuleSet = null;
      authorImportantRuleSet = null;
      authorNormalRuleSet = null;
      tagRuleSet = null;

      result = allRules.toArray(EMPTY_CSS_RULE_ARRAY);
    }
    return result;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    throw new IllegalStateException("Not comparable");
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "[userAgentRuleSet=" + userAgentRuleSet + ", userNormalRuleSet=" + userNormalRuleSet
        + ", authorNormalRuleSet=" + authorNormalRuleSet + ", authorImportantRuleSet="
        + authorImportantRuleSet + ", userImportantRuleSet=" + userImportantRuleSet
        + ", tagRuleSet=" + tagRuleSet + ", prioritized rules=" + Arrays.toString(result) + "]";
  }

}
