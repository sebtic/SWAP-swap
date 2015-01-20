/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr>
 * 
 * This file is part of SHS.
 * 
 * SHS is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * SHS is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with SHS. If not, see <http://www.gnu.org/licenses/>.
 * 
 * $Id$
 */
package org.projectsforge.swap.core.mime.css.resolver.color;

/**
 * The class representing a CIE LAB color (L*a*b*).
 * 
 * @author Sébastien Aupetit
 */
public class CIELABColor {

  /** The L component. */
  public final double L;

  /** The a component. */
  public final double a;

  /** The b component. */
  public final double b;

  /** Cached converted value. */
  private CIEXYZColor ciexyz;

  /**
   * Instantiates a new color.
   * 
   * @param L
   *          the L component
   * @param a
   *          the a component
   * @param b
   *          the b component
   */
  public CIELABColor(final double L, final double a, final double b) {
    this.L = L;
    this.a = a;
    this.b = b;
  }

  /**
   * Instantiates a new color.
   * 
   * @param L
   *          the L component
   * @param a
   *          the a component
   * @param b
   *          the b component
   * @param ciexyz
   *          the CIE XYZ color from which the color is issued
   */
  public CIELABColor(final double L, final double a, final double b, final CIEXYZColor ciexyz) {
    this.L = L;
    this.a = a;
    this.b = b;
    this.ciexyz = ciexyz;
  }

  /**
   * Gets a string representation of the color in CSV format using colon.
   * 
   * @return the string
   */
  public String asCSVString() {
    return "" + L + ";" + a + ";" + b;
  }

  /**
   * Compare colors without taking into account the L* component in the L*a*b*
   * color model (based on delta E (CIE 1976) distance but ignoring the
   * lightness (L) component.
   * 
   * @param other
   *          the other color to compare with
   * @return the distance
   */
  public double distanceAB(final CIELABColor other) {
    return Math.sqrt((a - other.a) * (a - other.a) + (b - other.b) * (b - other.b));
  }

  /**
   * Compare colors taking into account only the L* component in the L*a*b*
   * color model (based on delta E (CIE 1976) distance but ignoring the a and b
   * components.
   * 
   * @param other
   *          the other color to compare with
   * @return the distance
   */
  public double distanceL(final CIELABColor other) {
    return Math.abs(L - other.L); // same as the square root or the squared
                                  // value
  }

  /**
   * Compare colors using the delta E (CIE 1976) distance.
   * 
   * @param other
   *          the other color to compare with
   * @return the distance
   */
  public double distanceLAB(final CIELABColor other) {
    return Math.sqrt((L - other.L) * (L - other.L) + (a - other.a) * (a - other.a) + (b - other.b)
        * (b - other.b));
  }

  /**
   * Simulate dichromacy deficiency from formula of "An efficient
   * naturalness-preserving image-recoloring method for dichromats, G. R. Kuhn,
   * M. M. Oliveira, L. A. F. Fernandes"
   * 
   * @param dichromacyDeficiency
   *          the dichromacy deficiency
   * @return the cIELAB color
   */
  public CIELABColor simulateDichromacyDeficiency(final DichromacyDeficiency dichromacyDeficiency) {
    switch (dichromacyDeficiency) {
      case Deuteranope:
        return new CIELABColor(1. * L + 0. * a + 0. * b, 0. * L + 0.0199019 * a + -0.1396631 * b,
            0. * L + -0.1396631 * a + 0.9800982 * b);
      case Protanope:
        return new CIELABColor(1. * L + 0. * a + 0. * b, 0. * L + 0.0396113 * a + -0.1950442 * b,
            0. * L + -0.1950442 * a + 0.9603886 * b);
      case Tritanope:
        return new CIELABColor(1. * L + 0. * a + 0. * b, 0. * L + 0.5239019 * a + 0.4994284 * b, 0.
            * L + 0.4994284 * a + 0.4760981 * b);
      default:
        throw new IllegalArgumentException("Argument can not be null");
    }
  }

  /**
   * Convert the color to a CIE XYZ color.
   * 
   * @return the color
   */
  public CIEXYZColor toCIEXYZ() {
    if (ciexyz == null) {
      final double fY = (this.L + 16) / 116;
      final double fX = this.a / 500 + fY;
      final double fZ = fY - this.b / 200;

      final double X = ((fX * fX * fX > 0.008856) ? fX * fX * fX : (116 * fX - 16) / 903.3);
      final double Y = (this.L > 903.3 * 0.008856) ? Math.pow((this.L + 16) / 116, 3)
          : this.L / 903.3;
      final double Z = ((fZ * fZ * fZ > 0.008856) ? fZ * fZ * fZ : (116 * fZ - 16) / 903.3);
      ciexyz = new CIEXYZColor(X * CIEXYZColor.d65White.x, Y * CIEXYZColor.d65White.y, Z
          * CIEXYZColor.d65White.z, this);
    }
    return ciexyz;
  }

  /**
   * Convert the color to a sRGB color.
   * 
   * @return the color
   */
  public SRGBColor toSRGB() {
    return toCIEXYZ().toSRGB();
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "(" + L + "," + a + "," + b + ")";
  }
}
