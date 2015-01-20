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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.projectsforge.swap.core.mime.css.property.color.SRGBColor;

/**
 * The Class SeparatedColorDictionaryEntry. With this entry, two entries are
 * different if type, color or rule are different.
 * 
 * @param <TColor> the generic type
 * @author Sébastien Aupetit
 */
class SeparatedColorDictionaryEntry<TColor extends SRGBColor> extends
    ModifiableColorDictionaryEntry<TColor> {

  /** The rule. */
  private final ColorRule rule;

  /**
   * Instantiates a new separated rule pair.
   * 
   * @param id the id
   * @param type the type
   * @param color the color
   * @param rule the rule
   */
  public SeparatedColorDictionaryEntry(final int id, final ColorEntryType type, final TColor color,
      final ColorRule rule) {
    super(id, type, color, new HashSet<ColorRule>(Arrays.asList(rule)));
    this.rule = rule;
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.mime.css.property.color.dictionary.
   * ModifiableColorDictionaryEntry
   * #addRule(org.projectsforge.swap.core.mime.css.
   * property.color.dictionary.ColorRule)
   */
  @Override
  public void addRule(final ColorRule rule) {
    if (!this.rule.equals(rule)) {
      throw new IllegalArgumentException("A SeparatedColorDictionaryEntry can only contain 1 rule");
    }
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof ColorDictionaryEntry) {
      @SuppressWarnings("rawtypes")
      final ColorDictionaryEntry other = (ColorDictionaryEntry) obj;
      @SuppressWarnings("unchecked")
      final Set<ColorRule> rules = other.getRules();
      if (rules.size() != 1) {
        return false;
      } else {
        for (final ColorRule r : rules) {
          return getType().equals(other.getType()) && getColor().equals(other.getColor())
              && rule.equals(r);
        }
      }
    }
    return false;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return (getType().hashCode() * 31 + getColor().hashCode()) * 31 + rule.hashCode();
  }
}
