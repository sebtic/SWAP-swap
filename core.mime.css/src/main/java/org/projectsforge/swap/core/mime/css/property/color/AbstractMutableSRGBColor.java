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

import org.apache.commons.math3.util.FastMath;

/**
 * The Class MutableSRGBColor.
 * 
 * @author Sébastien Aupetit
 */
public abstract class AbstractMutableSRGBColor extends SRGBColor {

  /**
   * Clamp the component to the range [0:255].
   */
  public void clamp() {
    setR(FastMath.min(FastMath.max(getR(), 0), 255));
    setG(FastMath.min(FastMath.max(getG(), 0), 255));
    setB(FastMath.min(FastMath.max(getB(), 0), 255));
  }

  /**
   * Clamped move.
   * 
   * @param deltaR the delta r
   * @param deltaG the delta g
   * @param deltaB the delta b
   */
  public void clampedMove(final int deltaR, final int deltaG, final int deltaB) {
    move(deltaR, deltaG, deltaB);
    clamp();;
  }

  /**
   * Simulate dichromacy deficiency.
   * 
   * @param dichromacyDeficiency the dichromacy deficiency
   * @see DichromacyDeficiency#kuhnSimulation(DichromacyDeficiency, double[])
   */
  public void kuhnSimulationOfDichromacy(final DichromacyDeficiency dichromacyDeficiency) {
    final double[] labBefore = ColorSpaceUtil.XYZToLab(ColorSpaceUtil.SRGBToXYZ(getR(), getG(),
        getB()));
    final double[] labAfter = DichromacyDeficiency.kuhnSimulation(dichromacyDeficiency, labBefore);
    final int[] rgbAfter = ColorSpaceUtil.XYZToSRGB(ColorSpaceUtil.LabToXYZ(labAfter));
    set(rgbAfter);
  }

  /**
   * Move.
   * 
   * @param deltaR the delta r
   * @param deltaG the delta g
   * @param deltaB the delta b
   */
  public void move(final int deltaR, final int deltaG, final int deltaB) {
    setR(getR() + deltaR);
    setG(getG() + deltaG);
    setB(getB() + deltaB);
  }

  /**
   * Sets the.
   * 
   * @param r the r
   * @param g the g
   * @param b the b
   */
  public void set(final double r, final double g, final double b) {
    this.setR(FastMath.min(255, (int) FastMath.round(r * 255)));
    this.setG(FastMath.min(255, (int) FastMath.round(g * 255)));
    this.setB(FastMath.min(255, (int) FastMath.round(b * 255)));
  }

  /**
   * Sets the.
   * 
   * @param rgb the rgb
   */
  public void set(final double[] rgb) {
    setR(rgb[0]);
    setG(rgb[1]);
    setB(rgb[2]);
  }

  /**
   * Sets the.
   * 
   * @param r the r
   * @param g the g
   * @param b the b
   */
  public void set(final int r, final int g, final int b) {
    setR(r);
    setG(g);
    setB(b);
  }

  /**
   * Sets the.
   * 
   * @param rgb the rgb
   */
  public void set(final int[] rgb) {
    setR(rgb[0]);
    setG(rgb[1]);
    setB(rgb[2]);
  }

  /**
   * Sets the.
   * 
   * @param color the color
   */
  public void set(final SRGBColor color) {
    setR(color.getR());
    setG(color.getG());
    setB(color.getB());
  }

  /**
   * Sets the b.
   * 
   * @param b the new b
   */
  public void setB(final double b) {
    setB(FastMath.min(255, (int) FastMath.round(b * 255)));
  }

  /**
   * Sets the b.
   * 
   * @param b the new b
   */
  public abstract void setB(final int b);

  /**
   * Sets the g.
   * 
   * @param g the new g
   */
  public void setG(final double g) {
    setG(FastMath.min(255, (int) FastMath.round(g * 255)));
  }

  /**
   * Sets the g.
   * 
   * @param g the new g
   */
  public abstract void setG(final int g);

  /**
   * Sets the r.
   * 
   * @param r the new r
   */
  public void setR(final double r) {
    setR(FastMath.min(255, (int) FastMath.round(r * 255)));
  }

  /**
   * Sets the r.
   * 
   * @param r the new r
   */
  public abstract void setR(final int r);

}
