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
package org.projectsforge.swap.core.mime.css.property.color;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.apache.commons.math3.util.FastMath;

/**
 * The Class ImmutableSRGBColor.
 * 
 * @author Sébastien Aupetit
 */
public final class ImmutableSRGBColor extends SRGBColor implements Serializable {

  private static final long serialVersionUID = 1L;

  /** The r. */
  private final int r;

  /** The g. */
  private final int g;

  /** The b. */
  private final int b;

  /**
   * Instantiates a new immutable srgb color.
   * 
   * @param r the r
   * @param g the g
   * @param b the b
   */
  public ImmutableSRGBColor(final double r, final double g, final double b) {
    this.r = (int) FastMath.min(255, FastMath.round(r * 255));
    this.g = (int) FastMath.min(255, FastMath.round(g * 255));
    this.b = (int) FastMath.min(255, FastMath.round(b * 255));
  }

  /**
   * Instantiates a new immutable srgb color.
   * 
   * @param rgb the rgb
   */
  public ImmutableSRGBColor(final double[] rgb) {
    this.r = (int) FastMath.min(255, FastMath.round(rgb[0] * 255));
    this.g = (int) FastMath.min(255, FastMath.round(rgb[1] * 255));
    this.b = (int) FastMath.min(255, FastMath.round(rgb[2] * 255));
  }

  /**
   * Instantiates a new immutable srgb color.
   * 
   * @param r the r
   * @param g the g
   * @param b the b
   */
  public ImmutableSRGBColor(final int r, final int g, final int b) {
    this.r = r;
    this.g = g;
    this.b = b;
  }

  /**
   * Instantiates a new immutable srgb color.
   * 
   * @param rgb the rgb
   */
  public ImmutableSRGBColor(final int[] rgb) {
    this.r = rgb[0];
    this.g = rgb[1];
    this.b = rgb[2];
  }

  /**
   * Instantiates a new immutable srgb color.
   * 
   * @param color the color
   */
  public ImmutableSRGBColor(final SRGBColor color) {
    this.r = color.getR();
    this.g = color.getG();
    this.b = color.getB();
  }

  /**
   * Clamp the component to the range [0:255].
   * 
   * @param <T> the generic type
   * @param clazz the clazz
   * @return the clamped color
   */
  public <T extends SRGBColor> T clamp(final Class<T> clazz) {
    try {
      final Constructor<T> c = clazz.getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE);
      return c.newInstance(FastMath.min(FastMath.max(0, r), 255), FastMath.min(FastMath.max(0, g), 255),
          FastMath.min(FastMath.max(0, b), 255));
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
        | NoSuchMethodException | SecurityException e) {
      throw new IllegalArgumentException("Invalid class argument", e);
    }
  }

  /**
   * Clamped move.
   * 
   * @param <T> the generic type
   * @param clazz the clazz
   * @param deltaR the delta r
   * @param deltaG the delta g
   * @param deltaB the delta b
   * @return the SRGB color
   */
  public <T extends SRGBColor> T clampedMove(final Class<T> clazz, final int deltaR, final int deltaG, final int deltaB) {
    try {
      final Constructor<T> c = clazz.getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE);
      return c.newInstance(FastMath.min(FastMath.max(r + deltaR, 0), 255),
          FastMath.min(FastMath.max(g + deltaG, 0), 255), FastMath.min(FastMath.max(b + deltaB, 0), 255));
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
        | NoSuchMethodException | SecurityException e) {
      throw new IllegalArgumentException("Invalid class argument", e);
    }
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.mime.css.property.color.SRGBColor#getB()
   */
  @Override
  public int getB() {
    return b;
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.mime.css.property.color.SRGBColor#getG()
   */
  @Override
  public int getG() {
    return g;
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.mime.css.property.color.SRGBColor#getR()
   */
  @Override
  public int getR() {
    return r;
  }

  /**
   * Move.
   * 
   * @param <T> the generic type
   * @param clazz the clazz
   * @param deltaR the delta r
   * @param deltaG the delta g
   * @param deltaB the delta b
   * @return the SRGB color
   */
  public <T extends SRGBColor> T move(final Class<T> clazz, final int deltaR, final int deltaG, final int deltaB) {
    try {
      final Constructor<T> c = clazz.getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE);
      return c.newInstance(r + deltaR, g + deltaG, b + deltaB);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
        | NoSuchMethodException | SecurityException e) {
      throw new IllegalArgumentException("Invalid class argument", e);
    }
  }

}
