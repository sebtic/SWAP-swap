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

import java.util.List;
import java.util.Set;
import org.projectsforge.swap.core.mime.css.resolver.color.SRGBColor;

/**
 * The Interface ColorDictionary which provided indexed color management.
 */
public interface ColorDictionary {

  /**
   * The Interface Entry.
   */
  public interface Entry {
    /**
     * Gets the color.
     * 
     * @return the color
     */
    public SRGBColor getColor();

    /**
     * Gets the id.
     * 
     * @return the id
     */
    public int getId();

    /**
     * Gets the rules.
     * 
     * @return the rules
     */
    public Set<ColorRule> getRules();

    /**
     * Gets the type.
     * 
     * @return the type
     */
    public Type getType();
  }

  /**
   * The Enum Type.
   */
  public enum Type {
    /** Is a foreground color. */
    FOREGROUND_COLOR,
    /** Is a background color. */
    BACKGROUND_COLOR,
    /** Is an undifferentiated color i.e. a foreground or a background color */
    UNFIFFERENTIATED_COLOR
  }

  /**
   * Gets the color.
   * 
   * @param id
   *          the index
   * @return the color
   */
  public SRGBColor getColor(final int id);

  /**
   * Gets the entries.
   * 
   * @return the entries
   */
  public List<Entry> getEntries();

  /**
   * Gets the entry.
   * 
   * @param id
   *          the index
   * @return the entry
   */
  public Entry getEntry(final int id);

  /**
   * Gets the real dictionary on which the current dictionary is built.
   * 
   * @return the real dictionary
   */
  public ColorDictionary getRealDictionary();

  /**
   * Gets the rules.
   * 
   * @param id
   *          the index
   * @return the rules
   */
  public Set<ColorRule> getRules(final int id);

  /**
   * Gets the type.
   * 
   * @param id
   *          the index
   * @return the type
   */
  public Type getType(int id);

  /**
   * Size.
   * 
   * @return the size
   */
  public int size();

}
