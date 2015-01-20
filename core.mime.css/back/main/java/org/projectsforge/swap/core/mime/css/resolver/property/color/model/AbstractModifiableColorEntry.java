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
 * $Id$
 */
package org.projectsforge.swap.core.mime.css.resolver.color.model;

import org.projectsforge.swap.core.mime.css.resolver.color.SRGBColor;
import org.projectsforge.swap.core.mime.css.resolver.color.model.ColorDictionary.Type;

/**
 * The Class AbstractModifiableColorEntry.
 */
abstract class AbstractModifiableColorEntry implements SimpleColorDictionary.ModifiableColorEntry {
  /** The color. */
  private final SRGBColor color;
  /** The type. */
  private final Type type;

  /** The id. */
  private final int id;

  /**
   * Instantiates a new abstract modifiable entry.
   * 
   * @param id
   *          the id of the entry
   * @param type
   *          the type
   * @param color
   *          the color
   */
  public AbstractModifiableColorEntry(final int id, final Type type, final SRGBColor color) {
    this.type = type;
    this.color = color;
    this.id = id;
    if (this.type == null) {
      throw new IllegalArgumentException("type can not be null");
    }
    if (this.color == null) {
      throw new IllegalArgumentException("color can not be null");
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.plugins.cssimprover.model.SimpleDictionary.RulePair
   * #getColor()
   */
  @Override
  public SRGBColor getColor() {
    return color;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.mime.css.resolver.color.model.ColorDictionary
   * .Entry#getId()
   */
  public int getId() {
    return id;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.plugins.cssimprover.model.SimpleDictionary.RulePair
   * #getType()
   */
  @Override
  public Type getType() {
    return type;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "[" + getColor() + ", " + getType() + ", " + getRules() + "]";
  }
}
