/**
 * Copyright 2012 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr>
 * 
 * This software is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software. If not, see <http://www.gnu.org/licenses/>.
 * 
 * $Id$
 */
package org.projectsforge.swap.core.mime.css.property.color.dictionary;

import java.util.Set;
import org.projectsforge.swap.core.mime.css.property.color.SRGBColor;

/**
 * The class ColorDictionaryEntry.
 * 
 * @param <TColor> the generic type
 * @author Sébastien Aupetit
 */
public class ColorDictionaryEntry<TColor extends SRGBColor> {

  /** The type. */
  private final ColorEntryType type;

  /** The color. */
  private final TColor color;

  /** The id. */
  private final int id;

  /** The rules. */
  private final Set<ColorRule> rules;

  /**
   * Instantiates a new color dictionary entry.
   * 
   * @param id the id
   * @param type the type
   * @param color the color
   * @param rules the rules
   */
  public ColorDictionaryEntry(final int id, final ColorEntryType type, final TColor color,
      final Set<ColorRule> rules) {
    this.type = type;
    this.color = color;
    this.id = id;
    this.rules = rules;
    if (this.type == null) {
      throw new IllegalArgumentException("type can not be null");
    }
    if (this.color == null) {
      throw new IllegalArgumentException("color can not be null");
    }
    if (rules == null) {
      throw new IllegalArgumentException("rules can not be null");
    }
    if (rules.isEmpty()) {
      throw new IllegalArgumentException("rules can nit be empty");
    }
  }

  /**
   * Gets the color.
   * 
   * @return the color
   */
  public TColor getColor() {
    return color;
  }

  /**
   * Gets the id.
   * 
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**
   * Gets the rules.
   * 
   * @return the rules
   */
  public Set<ColorRule> getRules() {
    return rules;
  }

  /**
   * Gets the type.
   * 
   * @return the type
   */
  public ColorEntryType getType() {
    return type;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "[" + getColor() + ", " + getType() + ", " + getRules() + "]";
  }
}
