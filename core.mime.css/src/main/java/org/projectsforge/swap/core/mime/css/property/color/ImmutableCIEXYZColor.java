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
 * The Class ImmutableCIEXYZColor.
 * 
 * @author Sébastien Aupetit
 */
public class ImmutableCIEXYZColor extends CIEXYZColor {

  /** The x. */
  private final double x;

  /** The y. */
  private final double y;

  /** The z. */
  private final double z;

  /**
   * Instantiates a new immutable ciexyz color.
   * 
   * @param x the x
   * @param y the y
   * @param z the z
   */
  public ImmutableCIEXYZColor(final double x, final double y, final double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * Instantiates a new immutable ciexyz color.
   * 
   * @param xyz the xyz
   */
  public ImmutableCIEXYZColor(final double[] xyz) {
    this.x = xyz[0];
    this.y = xyz[1];
    this.z = xyz[2];
  }

  /* (non-Javadoc)
   * @see org.projectsforge.swap.core.mime.css.property.color.CIEXYZColor#getX()
   */
  @Override
  public double getX() {
    return x;
  }

  /* (non-Javadoc)
   * @see org.projectsforge.swap.core.mime.css.property.color.CIEXYZColor#getY()
   */
  @Override
  public double getY() {
    return y;
  }

  /* (non-Javadoc)
   * @see org.projectsforge.swap.core.mime.css.property.color.CIEXYZColor#getZ()
   */
  @Override
  public double getZ() {
    return z;
  }

  /**
   * Move.
   * 
   * @param <T> the generic type
   * @param clazz the clazz
   * @param deltaX the delta x
   * @param deltaY the delta y
   * @param deltaZ the delta z
   * @return the t
   */
  public <T extends CIEXYZColor> T move(final Class<T> clazz, final double deltaX,
      final double deltaY, final double deltaZ) {
    try {
      final Constructor<T> c = clazz.getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE);
      return c.newInstance(x + deltaX, y + deltaY, z + deltaZ);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException | NoSuchMethodException | SecurityException e) {
      throw new IllegalArgumentException("Invalid class argument", e);
    }
  }

}
