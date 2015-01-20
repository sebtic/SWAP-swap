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
 * <http://www.gnu.org/licenses/>. $Id$
 */
package org.projectsforge.swap.core.mime.css.property.color;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * The class representing a CIE XYZ color.
 * 
 * @author Sébastien Aupetit
 */
public abstract class CIEXYZColor {

  /** The D65 white reference color used in sRGB for web standards. */
  public static final ImmutableCIEXYZColor d65White = new ImmutableCIEXYZColor(ColorSpaceUtil.getD65White());

  /**
   * Gets a string representation of the color in CSV format using colon.
   * 
   * @return the string
   */
  public String asCSVString() {
    return "" + getX() + ";" + getY() + ";" + getZ();
  }

  /**
   * Gets the.
   * 
   * @return the double[]
   */
  public double[] get() {
    return new double[] { getX(), getY(), getZ() };
  }

  /**
   * Gets the x.
   * 
   * @return the x
   */
  public abstract double getX();

  /**
   * Gets the y.
   * 
   * @return the y
   */
  public abstract double getY();

  /**
   * Gets the z.
   * 
   * @return the z
   */
  public abstract double getZ();

  /**
   * Convert the color to a CIE LAB color.
   * 
   * @param <T> the generic type
   * @param clazz the clazz
   * @return the color
   */
  public <T extends CIELabColor> T toCIELab(final Class<T> clazz) {
    final double[] lab = ColorSpaceUtil.XYZToLab(getX(), getY(), getZ());
    try {
      final Constructor<T> c = clazz.getConstructor(Double.TYPE, Double.TYPE, Double.TYPE);
      return c.newInstance(lab[0], lab[1], lab[2]);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
        | NoSuchMethodException | SecurityException e) {
      throw new IllegalArgumentException("Invalid class argument", e);
    }
  }

  /**
   * Convert the color to a sRGB color.
   * 
   * @param <T> the generic type
   * @param clazz the clazz
   * @return the color
   */
  public <T extends SRGBColor> T toSRGB(final Class<T> clazz) {
    final int[] rgb = ColorSpaceUtil.XYZToSRGB(getX(), getY(), getZ());
    try {
      final Constructor<T> c = clazz.getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE);
      return c.newInstance(rgb[0], rgb[1], rgb[2]);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
        | NoSuchMethodException | SecurityException e) {
      throw new IllegalArgumentException("Invalid class argument", e);
    }
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "(" + getX() + "," + getY() + "," + getZ() + ")";
  }
}
