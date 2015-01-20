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
 * <http://www.gnu.org/licenses/>. \$Id$
 */
package org.projectsforge.swap.core.mime.css.property.color.dictionary;

import org.projectsforge.swap.core.mime.css.resolver.CssRule;

/**
 * The Class ColorRule.
 * 
 * @author Sébastien Aupetit
 */
public class ColorRule {

  /**
   * The Enum ColorOrigin.
   * 
   * @author Sébastien Aupetit
   */
  public enum ColorOrigin {
    /** The FOREGROUND. */
    FOREGROUND,
    /** The BACKGROUND. */
    BACKGROUND
  }

  /** The rule. */
  private final CssRule rule;

  /** The color origin. */
  private final ColorOrigin colorOrigin;

  /**
   * The Constructor.
   * 
   * @param rule the rule
   * @param colorOrigin the color origin
   */
  public ColorRule(final CssRule rule, final ColorOrigin colorOrigin) {
    this.rule = rule;
    this.colorOrigin = colorOrigin;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof ColorRule) {
      final ColorRule other = (ColorRule) obj;
      return rule.equals(other.rule);
    }
    return false;
  }

  /**
   * Gets the color origin.
   * 
   * @return the color origin
   */
  public ColorOrigin getColorOrigin() {
    return colorOrigin;
  }

  /**
   * Gets the rule.
   * 
   * @return the rule
   */
  public CssRule getRule() {
    return rule;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return rule.hashCode();
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "[" + colorOrigin + " " + rule + "]";
  }
}
