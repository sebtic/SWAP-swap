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
package org.projectsforge.swap.core.mime.css.property.color;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * The Class ImmutableCIELabColor.
 * 
 * @author Sébastien Aupetit
 */
public class ImmutableCIELabColor extends CIELabColor {

  /** The l. */
  private final double l;

  /** The a. */
  private final double a;

  /** The b. */
  private final double b;

  /**
   * Instantiates a new immutable cie lab color.
   * 
   * @param l the l
   * @param a the a
   * @param b the b
   */
  public ImmutableCIELabColor(final double l, final double a, final double b) {
    this.l = l;
    this.a = a;
    this.b = b;
  }

  /**
   * Instantiates a new immutable cie lab color.
   * 
   * @param lab the lab
   */
  public ImmutableCIELabColor(final double[] lab) {
    this.l = lab[0];
    this.a = lab[1];
    this.b = lab[2];
  }

  /* (non-Javadoc)
   * @see org.projectsforge.swap.core.mime.css.property.color.CIELabColor#getA()
   */
  @Override
  public double getA() {
    return a;
  }

  /* (non-Javadoc)
   * @see org.projectsforge.swap.core.mime.css.property.color.CIELabColor#getB()
   */
  @Override
  public double getB() {
    return b;
  }

  /* (non-Javadoc)
   * @see org.projectsforge.swap.core.mime.css.property.color.CIELabColor#getL()
   */
  @Override
  public double getL() {
    return l;
  }

  /**
   * Move.
   * 
   * @param <T> the generic type
   * @param clazz the clazz
   * @param deltaL the delta l
   * @param deltaA the delta a
   * @param deltaB the delta b
   * @return the t
   */
  public <T extends CIELabColor> T move(final Class<T> clazz, final double deltaL,
      final double deltaA, final double deltaB) {
    try {
      final Constructor<T> c = clazz.getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE);
      return c.newInstance(l + deltaL, a + deltaA, b + deltaB);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException | NoSuchMethodException | SecurityException e) {
      throw new IllegalArgumentException("Invalid class argument", e);
    }
  }

}
