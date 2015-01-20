/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr>
 * 
 * This file is part of SWAP.
 * 
 * SWAP is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * SWAP is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with SWAP. If not, see <http://www.gnu.org/licenses/>.
 * 
 * $Id$
 */
package org.projectsforge.swap.core.mime.css.resolver.color;

/**
 * The class representing a CIE XYZ color.
 * 
 * @author Sébastien Aupetit
 */
public class CIEXYZColor {

  /** The x component. */
  public final double x;

  /** The y component. */
  public final double y;

  /** The z component. */
  public final double z;

  /** Cached converted value. */
  private SRGBColor srgb;

  private CIELABColor cielab;

  /**
   * http://en.wikipedia.org/wiki/Color_difference#cite_note-5
   * 
   * http://en.wikipedia.org/wiki/CIE_1931_color_space
   * 
   * http://en.wikipedia.org/wiki/SRGB_color_space
   * 
   * http://en.wikipedia.org/wiki/RGB_color_space
   * 
   * http://www.cambridgeincolour.com/tutorials/color-spaces.htm
   * 
   * http://www.brucelindbloom.com/index.html?Eqn_XYZ_to_Lab.html
   * 
   * http://www.easyrgb.com/index.php?X=MATH&H=08#text8
   */

  /** The D65 white reference color used in sRGB for web standards. */
  public static final CIEXYZColor d65White = new CIEXYZColor(0.9505, 1.000, 1.0890);

  /**
   * Instantiates a new color.
   * 
   * @param x
   *          the x component
   * @param y
   *          the y component
   * @param z
   *          the z component
   */
  public CIEXYZColor(final double x, final double y, final double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * Instantiates a new color.
   * 
   * @param x
   *          the x component
   * @param y
   *          the y component
   * @param z
   *          the z component
   * @param cielab
   *          the CIE LAB color from which the color is issued
   */
  public CIEXYZColor(final double x, final double y, final double z, final CIELABColor cielab) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.cielab = cielab;
  }

  /**
   * Instantiates a new color.
   * 
   * @param x
   *          the x component
   * @param y
   *          the y component
   * @param z
   *          the z component
   * @param srgb
   *          the sRGB color from which the color is issued
   */
  public CIEXYZColor(final double x, final double y, final double z, final SRGBColor srgb) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.srgb = srgb;
  }

  /**
   * Gets a string representation of the color in CSV format using colon.
   * 
   * @return the string
   */
  public String asCSVString() {
    return "" + x + ";" + y + ";" + z;
  }

  /**
   * Convert the color to a CIE LAB color.
   * 
   * @return the color
   */
  public CIELABColor toCIELAB() {
    if (cielab == null) {
      final double relativeX = this.x / CIEXYZColor.d65White.x;
      final double relativeY = this.y / CIEXYZColor.d65White.y;
      final double relativeZ = this.z / CIEXYZColor.d65White.z;

      final double fX = (relativeX > 0.008856) ? Math.pow(relativeX, 1 / 3.)
          : 7.787 * relativeX + 16 / 116.;
      final double fY = (relativeY > 0.008856) ? Math.pow(relativeY, 1 / 3.)
          : 7.787 * relativeY + 16 / 116.;
      final double fZ = (relativeZ > 0.008856) ? Math.pow(relativeZ, 1 / 3.)
          : 7.787 * relativeZ + 16 / 116.;

      cielab = new CIELABColor(116 * fY - 16, 500 * (fX - fY), 200 * (fY - fZ), this);
    }
    return cielab;
  }

  /**
   * Convert the color to a sRGB color.
   * 
   * @return the color
   */
  public SRGBColor toSRGB() {
    if (srgb == null) {
      final double linearR = 3.2406 * this.x - 1.5372 * this.y - 0.4986 * this.z;
      final double linearG = -0.9689 * this.x + 1.8758 * this.y + 0.0415 * this.z;
      final double linearB = 0.0557 * this.x - 0.2040 * this.y + 1.0570 * this.z;

      final double nonLinearR = (linearR <= 0.0031308) ? 12.92 * linearR : (1 + 0.055)
          * Math.pow(linearR, 1.0 / 2.4) - 0.055;
      final double nonLinearG = (linearG <= 0.0031308) ? 12.92 * linearG : (1 + 0.055)
          * Math.pow(linearG, 1.0 / 2.4) - 0.055;
      final double nonLinearB = (linearB <= 0.0031308) ? 12.92 * linearB : (1 + 0.055)
          * Math.pow(linearB, 1.0 / 2.4) - 0.055;

      int intR = (int) Math.round(nonLinearR * 255);
      int intG = (int) Math.round(nonLinearG * 255);
      int intB = (int) Math.round(nonLinearB * 255);

      // clamping
      if (intR < 0) {
        intR = 0;
      } else if (intR > 255) {
        intR = 255;
      }
      if (intG < 0) {
        intG = 0;
      } else if (intG > 255) {
        intG = 255;
      }
      if (intB < 0) {
        intB = 0;
      } else if (intB > 255) {
        intB = 255;
      }
      srgb = new SRGBColor(intR, intG, intB, this);
    }
    return srgb;
  }

  @Override
  public String toString() {
    return "(" + x + "," + y + "," + z + ")";
  }
}
