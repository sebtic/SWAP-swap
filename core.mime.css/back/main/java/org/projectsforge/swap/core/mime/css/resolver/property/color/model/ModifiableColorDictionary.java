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

import java.util.AbstractList;
import java.util.List;
import java.util.Set;
import org.projectsforge.swap.core.mime.css.resolver.color.SRGBColor;

/**
 * A colorDictionary which allows to override color of a colorDictionary.
 */
public class ModifiableColorDictionary implements ColorDictionary {

  /** The colorDictionary. */
  private final ColorDictionary colorDictionary;

  /** The colors. */
  private final SRGBColor[] colors;

  /** The size. */
  private final int size;

  /**
   * Instantiates a new modifiable colorDictionary.
   * 
   * @param colorDictionary
   *          the colorDictionary
   */
  public ModifiableColorDictionary(final ColorDictionary colorDictionary) {
    this.colorDictionary = colorDictionary;
    this.size = colorDictionary.size();
    this.colors = new SRGBColor[size];
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.plugins.cssimprover.model.Dictionary#getColor(int)
   */
  @Override
  public SRGBColor getColor(final int id) {
    if (colors[id] == null) {
      return colorDictionary.getColor(id);
    } else {
      return colors[id];
    }
  }

  @Override
  public List<Entry> getEntries() {
    return new AbstractList<Entry>() {
      @Override
      public Entry get(final int index) {
        return getEntry(index);
      }

      @Override
      public int size() {
        return ModifiableColorDictionary.this.size();
      }
    };
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.plugins.cssimprover.model.Dictionary#getEntry(int)
   */
  @Override
  public Entry getEntry(final int id) {
    final Entry entry = colorDictionary.getEntry(id);
    return new Entry() {

      @Override
      public SRGBColor getColor() {
        if (colors[id] == null) {
          return entry.getColor();
        } else {
          return colors[id];
        }
      }

      @Override
      public int getId() {
        return id;
      }

      @Override
      public Set<ColorRule> getRules() {
        return entry.getRules();
      }

      @Override
      public Type getType() {
        return entry.getType();
      }

      @Override
      public String toString() {
        return "[" + getColor() + ", " + getType() + ", " + getRules() + "]";
      }
    };
  }

  @Override
  public ColorDictionary getRealDictionary() {
    return colorDictionary.getRealDictionary();
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.plugins.cssimprover.model.Dictionary#getRules(int)
   */
  @Override
  public Set<ColorRule> getRules(final int id) {
    return colorDictionary.getRules(id);
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.plugins.cssimprover.model.Dictionary#getType(int)
   */
  @Override
  public Type getType(final int id) {
    return colorDictionary.getType(id);
  }

  /**
   * Reset.
   */
  public void reset() {
    for (int i = 0; i < size; ++i) {
      colors[i] = null;
    }
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
    colors[id] = color;
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.plugins.cssimprover.model.Dictionary#size()
   */
  @Override
  public int size() {
    return size;
  }

  @Override
  public String toString() {
    return getEntries().toString();
  }

}
