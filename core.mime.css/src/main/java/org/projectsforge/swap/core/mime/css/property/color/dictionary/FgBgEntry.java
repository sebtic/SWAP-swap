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

import org.projectsforge.swap.core.mime.css.property.color.SRGBColor;

/**
 * The Class FgBgEntry.
 * 
 * @author Sébastien Aupetit
 */
public class FgBgEntry {
  
  /** The foreground index. */
  private final int foregroundIndex;

  /** The weight. */
  private final double weight;

  /** The background index. */
  private final int backgroundIndex;

  /**
   * Instantiates a new fg bg entry.
   * 
   * @param foregroundIndex the foreground index
   * @param backgroundIndex the background index
   * @param weight the weight
   */
  public FgBgEntry(final int foregroundIndex, final int backgroundIndex, final double weight) {
    this.foregroundIndex = foregroundIndex;
    this.backgroundIndex = backgroundIndex;
    this.weight = weight;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof FgBgEntry) {
      final FgBgEntry entry = (FgBgEntry) obj;
      return foregroundIndex == entry.foregroundIndex && backgroundIndex == entry.backgroundIndex;
    }
    return false;
  }

  /**
   * Gets the background color.
   * 
   * @param <TColor> the generic type
   * @param colorDictionary the color dictionary
   * @return the background color
   */
  public <TColor extends SRGBColor> TColor getBackgroundColor(
      final ColorDictionary<TColor> colorDictionary) {
    return colorDictionary.getEntry(backgroundIndex).getColor();
  }

  /**
   * Gets the background entry.
   * 
   * @param <TColor> the generic type
   * @param colorDictionary the color dictionary
   * @return the background entry
   */
  public <TColor extends SRGBColor> ColorDictionaryEntry<TColor> getBackgroundEntry(
      final ColorDictionary<TColor> colorDictionary) {
    return colorDictionary.getEntry(backgroundIndex);
  }

  /**
   * Gets the background index.
   * 
   * @return the background index
   */
  public int getBackgroundIndex() {
    return backgroundIndex;
  }

  /**
   * Gets the foreground color.
   * 
   * @param <TColor> the generic type
   * @param colorDictionary the color dictionary
   * @return the foreground color
   */
  public <TColor extends SRGBColor> TColor getForegroundColor(
      final ColorDictionary<TColor> colorDictionary) {
    return colorDictionary.getEntry(foregroundIndex).getColor();
  }

  /**
   * Gets the foreground entry.
   * 
   * @param <TColor> the generic type
   * @param colorDictionary the color dictionary
   * @return the foreground entry
   */
  public <TColor extends SRGBColor> ColorDictionaryEntry<TColor> getForegroundEntry(
      final ColorDictionary<TColor> colorDictionary) {
    return colorDictionary.getEntry(foregroundIndex);
  }

  /**
   * Gets the foreground index.
   * 
   * @return the foreground index
   */
  public int getForegroundIndex() {
    return foregroundIndex;
  }

  /**
   * Gets the weight.
   * 
   * @return the weight
   */
  public double getWeight() {
    return weight;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return foregroundIndex * 31 + backgroundIndex;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "[" + foregroundIndex + ":" + backgroundIndex + "=>" + weight + "]";
  }

}
