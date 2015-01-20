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
import org.projectsforge.swap.core.mime.css.property.color.SRGBColor;

/**
 * The Class MergedColorDictionaryEntry. With this entry, two entries are
 * different if type or color are different. Rules are not differentiating two
 * entries.
 * 
 * @param <TColor> the generic type
 * @author Sébastien Aupetit
 */
class MergedColorDictionaryEntry<TColor extends SRGBColor> extends
    ModifiableColorDictionaryEntry<TColor> {

  /**
   * Instantiates a new merged rule pair.
   * 
   * @param id the id
   * @param type the type
   * @param color the color
   * @param rule the rule
   */
  public MergedColorDictionaryEntry(final int id, final ColorEntryType type, final TColor color,
      final ColorRule rule) {
    super(id, type, color, new HashSet<ColorRule>(Arrays.asList(rule)));
    if (rule == null) {
      throw new IllegalArgumentException("rule can not be null");
    }
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
    getRules().add(rule);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof ColorDictionaryEntry) {
      @SuppressWarnings({ "rawtypes" })
      final ColorDictionaryEntry other = (ColorDictionaryEntry) obj;
      return getType().equals(other.getType()) && getColor().equals(other.getColor());
    }
    return false;
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
