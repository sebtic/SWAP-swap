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
 * \$Id$
 */
package org.projectsforge.swap.core.mime.css.resolver.color.model;

import java.util.HashSet;
import java.util.Set;
import org.projectsforge.swap.core.mime.css.resolver.color.SRGBColor;
import org.projectsforge.swap.core.mime.css.resolver.color.model.ColorDictionary.Entry;
import org.projectsforge.swap.core.mime.css.resolver.color.model.ColorDictionary.Type;

/**
 * The Class MergedColorDictionaryEntry. With this entry, two entries are
 * different if type or color are different. Rules are not differentiating two
 * entries.
 */
class MergedColorDictionaryEntry extends AbstractModifiableColorEntry {
  /** The rules. */
  private final Set<ColorRule> rules;

  /**
   * Instantiates a new merged rule pair.
   * 
   * @param id
   *          the id
   * @param type
   *          the type
   * @param color
   *          the color
   * @param rule
   *          the rule
   */
  public MergedColorDictionaryEntry(final int id, final Type type, final SRGBColor color,
      final ColorRule rule) {
    super(id, type, color);
    if (rule == null) {
      throw new IllegalArgumentException("rule can not be null");
    }
    this.rules = new HashSet<ColorRule>();
    rules.add(rule);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.plugins.cssimprover.model.SimpleDictionary.RulePair
   * #addRule(org.projectsforge.swap.core.mime.css.resolver.ruleset.CssRule)
   */
  @Override
  public void addRule(final ColorRule rule) {
    rules.add(rule);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof Entry) {
      final Entry other = (Entry) obj;
      return getType().equals(other.getType()) && getColor().equals(other.getColor());
    }
    return false;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.plugins.cssimprover.model.SimpleDictionary.RulePair
   * #getRules()
   */
  @Override
  public Set<ColorRule> getRules() {
    return rules;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return getType().hashCode() * 31 + getColor().hashCode();
  }
}
