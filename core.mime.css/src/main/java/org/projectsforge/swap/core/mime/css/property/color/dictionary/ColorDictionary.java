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

import java.util.List;
import org.projectsforge.swap.core.mime.css.property.color.SRGBColor;

/**
 * The class ColorDictionary which provided indexed color management.
 * 
 * @param <TColor> the generic type
 * @author Sébastien Aupetit
 */
public abstract class ColorDictionary<TColor extends SRGBColor> {

  /**
   * Gets the entries.
   * 
   * @return the entries
   */
  public abstract List<ColorDictionaryEntry<TColor>> getEntries();

  /**
   * Gets the entry.
   * 
   * @param id the index
   * @return the entry
   */
  public abstract ColorDictionaryEntry<TColor> getEntry(final int id);

  /**
   * Size.
   * 
   * @return the size
   */
  public abstract int size();

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getEntries().toString();
  }
}
