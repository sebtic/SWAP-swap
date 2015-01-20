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
 * The Class MutableCIELabColor.
 * 
 * @author Sébastien Aupetit
 */
public class MutableCIELabColor extends CIELabColor {

  /** The l. */
  private double l;

  /** The a. */
  private double a;

  /** The b. */
  private double b;

  /**
   * Instantiates a new mutable cie lab color.
   * 
   * @param l the l
   * @param a the a
   * @param b the b
   */
  public MutableCIELabColor(final double l, final double a, final double b) {
    setL(l);
    setA(a);
    setB(b);
  }

  /**
   * Instantiates a new mutable cie lab color.
   * 
   * @param lab the lab
   */
  public MutableCIELabColor(final double[] lab) {
    setL(lab[0]);
    setA(lab[1]);
    setB(lab[2]);
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
   * @param deltaL the delta l
   * @param deltaA the delta a
   * @param deltaB the delta b
   */
  public void move(final double deltaL, final double deltaA, final double deltaB) {
    setL(getL() + deltaL);
    setA(getA() + deltaA);
    setB(getB() + deltaB);
  }

  /**
   * Sets the.
   * 
   * @param l the l
   * @param a the a
   * @param b the b
   */
  public void set(final double l, final double a, final double b) {
    setL(l);
    setA(a);
    setB(b);
  }

  /**
   * Sets the.
   * 
   * @param lab the lab
   */
  public void set(final double[] lab) {
    setL(lab[0]);
    setA(lab[1]);
    setB(lab[2]);
  }

  /**
   * Sets the a.
   * 
   * @param a the new a
   */
  public void setA(final double a) {
    this.a = a;
  }

  /**
   * Sets the b.
   * 
   * @param b the new b
   */
  public void setB(final double b) {
    this.b = b;
  }

  /**
   * Sets the l.
   * 
   * @param l the new l
   */
  public void setL(final double l) {
    this.l = l;
  }

}
