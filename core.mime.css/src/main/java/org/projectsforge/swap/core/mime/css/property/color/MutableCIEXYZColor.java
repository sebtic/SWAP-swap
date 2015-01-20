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

/**
 * The Class MutableCIEXYZColor.
 * 
 * @author Sébastien Aupetit
 */
public class MutableCIEXYZColor extends CIEXYZColor {
  
  /** The x. */
  private double x;

  /** The y. */
  private double y;

  /** The z. */
  private double z;

  /**
   * Instantiates a new mutable ciexyz color.
   * 
   * @param x the x
   * @param y the y
   * @param z the z
   */
  public MutableCIEXYZColor(final double x, final double y, final double z) {
    setX(x);
    setY(y);
    setZ(z);
  }

  /**
   * Instantiates a new mutable ciexyz color.
   * 
   * @param xyz the xyz
   */
  public MutableCIEXYZColor(final double[] xyz) {
    setX(xyz[0]);
    setY(xyz[1]);
    setZ(xyz[2]);
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
   * @param deltaX the delta x
   * @param deltaY the delta y
   * @param deltaZ the delta z
   */
  public void move(final double deltaX, final double deltaY, final double deltaZ) {
    setX(getX() + deltaX);
    setY(getY() + deltaY);
    setZ(getZ() + deltaZ);
  }

  /**
   * Sets the.
   * 
   * @param x the x
   * @param y the y
   * @param z the z
   */
  public void set(final double x, final double y, final double z) {
    setX(x);
    setY(y);
    setZ(z);
  }

  /**
   * Sets the.
   * 
   * @param xyz the xyz
   */
  public void set(final double[] xyz) {
    setX(xyz[0]);
    setY(xyz[1]);
    setZ(xyz[2]);
  }

  /**
   * Sets the x.
   * 
   * @param x the new x
   */
  public void setX(final double x) {
    this.x = x;
  }

  /**
   * Sets the y.
   * 
   * @param y the new y
   */
  public void setY(final double y) {
    this.y = y;
  }

  /**
   * Sets the z.
   * 
   * @param z the new z
   */
  public void setZ(final double z) {
    this.z = z;
  }

}
