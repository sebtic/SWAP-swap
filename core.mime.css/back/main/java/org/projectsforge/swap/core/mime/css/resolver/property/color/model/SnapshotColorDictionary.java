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

import java.util.AbstractList;
import java.util.List;
import java.util.Set;
import org.projectsforge.swap.core.mime.css.resolver.color.SRGBColor;

/**
 * The Class SnapshotColorDictionary.
 */
public class SnapshotColorDictionary implements ColorDictionary {

  /**
   * The Class Entry.
   */
  class Entry implements ColorDictionary.Entry {

    /** The type. */
    private final Type type;

    /** The color. */
    private SRGBColor color;

    /** The rules. */
    private final Set<ColorRule> rules;

    /** The id. */
    private final int id;

    /**
     * The Constructor.
     * 
     * @param id
     *          the id
     * @param type
     *          the type
     * @param color
     *          the color
     * @param rules
     *          the rules
     */
    public Entry(final int id, final Type type, final SRGBColor color, final Set<ColorRule> rules) {
      this.id = id;
      this.type = type;
      this.color = color;
      this.rules = rules;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.projectsforge.swap.core.mime.css.resolver.color.model.ColorDictionary
     * .Entry#getColor()
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
     * org.projectsforge.swap.core.mime.css.resolver.color.model.ColorDictionary
     * .Entry#getRules()
     */
    @Override
    public Set<ColorRule> getRules() {
      return rules;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.projectsforge.swap.core.mime.css.resolver.color.model.ColorDictionary
     * .Entry#getType()
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

  /** The entries. */
  private final Entry[] entries;

  /**
   * The Constructor.
   * 
   * @param dictionary
   *          the dictionary
   */
  public SnapshotColorDictionary(final ColorDictionary dictionary) {
    entries = new Entry[dictionary.size()];
    for (int i = 0; i < entries.length; ++i) {
      final ColorDictionary.Entry base = dictionary.getEntry(i);
      entries[i] = new Entry(i, base.getType(), base.getColor(), base.getRules());
    }
  }

  /**
   * The Constructor.
   * 
   * @param colors
   *          the colors
   * @param dictionary
   *          the dictionary
   */
  public SnapshotColorDictionary(final SRGBColor[] colors, final ColorDictionary dictionary) {
    entries = new Entry[dictionary.size()];
    for (int i = 0; i < entries.length; ++i) {
      final ColorDictionary.Entry base = dictionary.getEntry(i);
      entries[i] = new Entry(i, base.getType(), colors[i], base.getRules());
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.mime.css.resolver.color.model.ColorDictionary
   * #getColor(int)
   */
  @Override
  public SRGBColor getColor(final int id) {
    return entries[id].color;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.mime.css.resolver.color.model.ColorDictionary
   * #getEntries()
   */
  @Override
  public List<ColorDictionary.Entry> getEntries() {
    return new AbstractList<ColorDictionary.Entry>() {
      @Override
      public ColorDictionary.Entry get(final int index) {
        return getEntry(index);
      }

      @Override
      public int size() {
        return SnapshotColorDictionary.this.size();
      }
    };
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.mime.css.resolver.color.model.ColorDictionary
   * #getEntry(int)
   */
  @Override
  public Entry getEntry(final int id) {
    return entries[id];
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.mime.css.resolver.color.model.ColorDictionary
   * #getRealDictionary()
   */
  @Override
  public ColorDictionary getRealDictionary() {
    return this;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.mime.css.resolver.color.model.ColorDictionary
   * #getRules(int)
   */
  @Override
  public Set<ColorRule> getRules(final int id) {
    return entries[id].rules;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.mime.css.resolver.color.model.ColorDictionary
   * #getType(int)
   */
  @Override
  public Type getType(final int id) {
    return entries[id].type;
  }

  /**
   * Sets the color.
   * 
   * @param id
   *          the id
   * @param color
   *          the color
   */
  public void setColor(final int id, final SRGBColor color) {
    entries[id].color = color;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.mime.css.resolver.color.model.ColorDictionary
   * #size()
   */
  @Override
  public int size() {
    return entries.length;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getEntries().toString();
  }

}
