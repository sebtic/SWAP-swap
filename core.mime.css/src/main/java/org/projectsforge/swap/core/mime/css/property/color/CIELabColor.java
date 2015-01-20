/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr> This file
 * is part of SHS. SHS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version. SHS is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with SHS. If not, see
 * <http://www.gnu.org/licenses/>. $Id$
 */
package org.projectsforge.swap.core.mime.css.property.color;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * The class representing a CIE LAB color (L*a*b*).
 * 
 * @author Sébastien Aupetit
 */
public abstract class CIELabColor {

  /**
   * Gets a string representation of the color in CSV format using colon.
   * 
   * @return the string
   */
  public String asCSVString() {
    return "" + getL() + ";" + getA() + ";" + getB();
  }

  /**
   * Compare colors without taking into account the L* component in the L*a*b*
   * color model (based on delta E (CIE 1976) distance but ignoring the
   * lightness (L) component.
   * 
   * @param other the other color to compare with
   * @return the distance
   */
  public double distanceAB(final CIELabColor other) {
    return CIELabUtil.distanceAB(getA(), getB(), other.getA(), other.getB());
  }

  /**
   * Compare colors taking into account only the L* component in the L*a*b*
   * color model (based on delta E (CIE 1976) distance but ignoring the a and b
   * components.
   * 
   * @param other the other color to compare with
   * @return the distance
   */
  public double distanceL(final CIELabColor other) {
    return CIELabUtil.distanceL(getL(), other.getL());
  }

  /**
   * Compare colors using the delta E (CIE 1976) distance.
   * 
   * @param other the other color to compare with
   * @return the distance
   */
  public double distanceLAB(final CIELabColor other) {
    return CIELabUtil.distanceLAB(getL(), getA(), getB(), other.getL(), other.getA(), other.getB());
  }

  /**
   * Gets the.
   * 
   * @return the double[]
   */
  public double[] get() {
    return new double[] { getL(), getA(), getB() };
  }

  /**
   * Gets the a.
   * 
   * @return the a
   */
  public abstract double getA();

  /**
   * Gets the b.
   * 
   * @return the b
   */
  public abstract double getB();

  /**
   * Gets the l.
   * 
   * @return the l
   */
  public abstract double getL();

  /**
   * Convert the color into a CIEXYZ color.
   * 
   * @param <T> the generic type
   * @param clazz the clazz
   * @return the color
   */
  public <T extends CIEXYZColor> T toCIEXYZ(final Class<T> clazz) {
    final double[] xyz = ColorSpaceUtil.LabToXYZ(getL(), getA(), getB());
    try {
      final Constructor<T> c = clazz.getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE);
      return c.newInstance(xyz[0], xyz[1], xyz[2]);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException | NoSuchMethodException | SecurityException e) {
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
    final int[] rgb = ColorSpaceUtil.LabToSRGB(getL(), getA(), getB());
    try {
      final Constructor<T> c = clazz.getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE);
      return c.newInstance(rgb[0], rgb[1], rgb[2]);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException | NoSuchMethodException | SecurityException e) {
      throw new IllegalArgumentException("Invalid class argument", e);
    }
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "(" + getL() + "," + getA() + "," + getB() + ")";
  }
}
