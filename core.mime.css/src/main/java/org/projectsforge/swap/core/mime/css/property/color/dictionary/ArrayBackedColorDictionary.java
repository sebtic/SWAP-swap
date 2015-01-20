/**
 * Copyright 2012 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr> This
 * software is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This software is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this software. If not, see
 * <http://www.gnu.org/licenses/>. $Id$
 */
package org.projectsforge.swap.core.mime.css.property.color.dictionary;

import java.util.Arrays;
import java.util.List;

import org.projectsforge.swap.core.mime.css.property.color.AbstractMutableSRGBColor;
import org.projectsforge.swap.core.mime.css.property.color.SRGBColor;

/**
 * The Class ArrayBackedColorDictionary.
 * 
 * @author Sébastien Aupetit
 */
public class ArrayBackedColorDictionary extends ColorDictionary<AbstractMutableSRGBColor> {

  /**
   * The Class ArrayBackedSRGBColor.
   * 
   * @author Sébastien Aupetit
   */
  private class ArrayBackedSRGBColor extends AbstractMutableSRGBColor {

    /** The indexes. */
    private final int rIndex, gIndex, bIndex;

    /**
     * Instantiates a new array backed srgb color.
     * 
     * @param colorIndex
     *          the color index
     */
    public ArrayBackedSRGBColor(final int colorIndex) {
      this.rIndex = colorIndex * 3;
      this.gIndex = colorIndex * 3 + 1;
      this.bIndex = colorIndex * 3 + 2;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.projectsforge.swap.core.mime.css.property.color.SRGBColor#get()
     */
    @Override
    public int[] get() {
      return new int[] { array[rIndex], array[gIndex], array[bIndex] };
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.projectsforge.swap.core.mime.css.property.color.MutableSRGBColor#
     * getB()
     */
    @Override
    public int getB() {
      return array[bIndex];
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.projectsforge.swap.core.mime.css.property.color.MutableSRGBColor#
     * getG()
     */
    @Override
    public int getG() {
      return array[gIndex];
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.projectsforge.swap.core.mime.css.property.color.MutableSRGBColor#
     * getR()
     */
    @Override
    public int getR() {
      return array[rIndex];
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.projectsforge.swap.core.mime.css.property.color.MutableSRGBColor#
     * set(int[])
     */
    @Override
    public void set(final int[] rgb) {
      array[rIndex] = rgb[0];
      array[gIndex] = rgb[1];
      array[bIndex] = rgb[2];
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.projectsforge.swap.core.mime.css.property.color.MutableSRGBColor#
     * setB(int)
     */
    @Override
    public void setB(final int b) {
      array[bIndex] = b;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.projectsforge.swap.core.mime.css.property.color.MutableSRGBColor#
     * setG(int)
     */
    @Override
    public void setG(final int g) {
      array[gIndex] = g;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.projectsforge.swap.core.mime.css.property.color.MutableSRGBColor#
     * setR(int)
     */
    @Override
    public void setR(final int r) {
      array[rIndex] = r;
    }
  }

  /** The array. */
  int[] array;

  /** The colors. */
  ArrayBackedSRGBColor[] colors;

  /** The size. */
  int size;

  /** The entries. */
  ColorDictionaryEntry<AbstractMutableSRGBColor>[] entries;

  @Deprecated
  public ArrayBackedColorDictionary(final ArrayBackedColorDictionary colorDictionary) {
    this(colorDictionary, colorDictionary.getColorsAsArray());
  }

  /**
   * Instantiates a new array backed color distionary.
   * 
   * @param <T>
   *          the generic type
   * @param colorDictionary
   *          the color dictionary
   */
  @SuppressWarnings("unchecked")
  public <T extends SRGBColor> ArrayBackedColorDictionary(final ColorDictionary<T> colorDictionary) {
    if (colorDictionary instanceof ArrayBackedColorDictionary) {
      // with ArrayBackedColorDictionary we can speed up the copy of the color using arrays directly
      final ArrayBackedColorDictionary abcd = (ArrayBackedColorDictionary) colorDictionary;
      size = colorDictionary.size();
      array = new int[size * 3];
      for (int i = 0; i < array.length; ++i) {
        array[i] = abcd.array[i];
      }
      colors = new ArrayBackedSRGBColor[size];
      entries = new ColorDictionaryEntry[size];

      for (int i = 0; i < size; ++i) {
        final ColorDictionaryEntry<T> entry = colorDictionary.getEntry(i);
        colors[i] = new ArrayBackedSRGBColor(i);
        entries[i] = new ColorDictionaryEntry<AbstractMutableSRGBColor>(i, entry.getType(), colors[i], entry.getRules());
      }
    } else {
      size = colorDictionary.size();
      array = new int[size * 3];
      colors = new ArrayBackedSRGBColor[size];
      entries = new ColorDictionaryEntry[size];

      for (int i = 0; i < size; ++i) {
        final ColorDictionaryEntry<T> entry = colorDictionary.getEntry(i);

        final SRGBColor color = entry.getColor();
        array[i * 3] = color.getR();
        array[i * 3 + 1] = color.getG();
        array[i * 3 + 2] = color.getB();
        colors[i] = new ArrayBackedSRGBColor(i);

        entries[i] = new ColorDictionaryEntry<AbstractMutableSRGBColor>(i, entry.getType(), colors[i], entry.getRules());
      }
    }
  }

  /**
   * Instantiates a new array backed color distionary.
   * 
   * @param <T>
   *          the generic type
   * @param colorDictionary
   *          the color dictionary
   * @param array
   *          the array which must be stored in the dictionary (the reference is kept)
   */
  @SuppressWarnings("unchecked")
  public <T extends SRGBColor> ArrayBackedColorDictionary(final ColorDictionary<T> colorDictionary, final int[] array) {
    size = colorDictionary.size();
    if (array.length != 3 * size) {
      throw new IllegalArgumentException("Invalid length for parameter array");
    }

    this.array = array;
    colors = new ArrayBackedSRGBColor[size];
    entries = new ColorDictionaryEntry[size];

    for (int i = 0; i < size; ++i) {
      final ColorDictionaryEntry<T> entry = colorDictionary.getEntry(i);
      colors[i] = new ArrayBackedSRGBColor(i);
      entries[i] = new ColorDictionaryEntry<AbstractMutableSRGBColor>(i, entry.getType(), colors[i], entry.getRules());
    }
  }

  public void copy(final ArrayBackedColorDictionary colorDictionary) {
    for (int i = 0; i < array.length; ++i) {
      array[i] = colorDictionary.array[i];
    }
  }

  /**
   * Gets the colors as array.
   * 
   * @return the colors as array
   */
  public int[] getColorsAsArray() {
    return array;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.projectsforge.swap.core.mime.css.property.color.dictionary.ColorDictionary
   * #getEntries()
   */
  @Override
  public List<ColorDictionaryEntry<AbstractMutableSRGBColor>> getEntries() {
    return Arrays.asList(entries);
  }

  @Override
  public ColorDictionaryEntry<AbstractMutableSRGBColor> getEntry(final int id) {
    return entries[id];
  }

  /**
   * Sets the colors as array.
   * 
   * @param array
   *          the new colors as array (the reference replace the current reference)
   */
  public void setColorsAsArray(final int[] array) {
    if (this.array.length != array.length) {
      throw new IllegalArgumentException("Invalid length for parameter array");
    }
    this.array = array;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.projectsforge.swap.core.mime.css.property.color.dictionary.ColorDictionary
   * #size()
   */
  @Override
  public int size() {
    return size;
  }

}
