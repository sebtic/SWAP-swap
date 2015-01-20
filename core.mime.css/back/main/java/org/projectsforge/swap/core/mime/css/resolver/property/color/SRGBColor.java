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
 * $Id: SRGBColor.java 110 2011-12-14 13:07:42Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.resolver.color;

/**
 * The class representing a sRGB color (internally the components are stored as
 * integer).
 * 
 * @author Sébastien Aupetit
 */
// http://en.wikipedia.org/wiki/SRGB_color_space
public class SRGBColor {

  /** The red component (integer form). */
  public final int r;

  /** The red component (real form). */
  public final double rd;
  /** The green component (integer form). */
  public final int g;

  /** The green component (real form). */
  public final double gd;

  /** The blue component (integer form). */
  public final int b;

  /** The blue component (real form). */
  public final double bd;

  /** Cached converted value. */
  private CIEXYZColor ciexyz;

  /** Cached value. */
  private Double luminance;

  /** Cached value. */
  private SRGBColor deuteranope;

  /** Cached value. */
  private SRGBColor protanope;

  /** Cached value. */
  private SRGBColor tritanope;

  /**
   * Instantiates a new sRGB color.
   * 
   * @param r
   *          the red component in [0:255]
   * @param g
   *          the green component in [0:255]
   * @param b
   *          the blue component in [0:255]
   */
  public SRGBColor(final int r, final int g, final int b) {
    this.r = r;
    this.g = g;
    this.b = b;
    this.rd = r / 255.;
    this.gd = g / 255.;
    this.bd = b / 255.;
  }

  /**
   * Instantiates a new sRGB color.
   * 
   * @param r
   *          the red component in [0:255]
   * @param g
   *          the green component in [0:255]
   * @param b
   *          the blue component in [0:255]
   * @param ciexyz
   *          the CIE XYZ color from which the color is issued
   */
  public SRGBColor(final int r, final int g, final int b, final CIEXYZColor ciexyz) {
    this.r = r;
    this.g = g;
    this.b = b;
    this.rd = r / 255.;
    this.gd = g / 255.;
    this.bd = b / 255.;
    this.ciexyz = ciexyz;
  }

  /**
   * Gets a string representation of the color in CSV format using colon.
   * 
   * @return the string
   */
  public String asCSVString() {
    return "" + r + ";" + g + ";" + b;
  }

  /**
   * Clamp the component to the range [0:255].
   * 
   * @return the clamped color
   */
  public SRGBColor clamp() {
    int rc = r;
    int gc = g;
    int bc = b;
    if (rc < 0) {
      rc = 0;
    } else if (rc > 255) {
      rc = 255;
    }
    if (gc < 0) {
      gc = 0;
    } else if (gc > 255) {
      gc = 255;
    }
    if (bc < 0) {
      bc = 0;
    } else if (bc > 255) {
      bc = 255;
    }
    return new SRGBColor(rc, gc, bc);
  }

  /**
   * Clamped move.
   * 
   * @param deltaR
   *          the delta r
   * @param deltaG
   *          the delta g
   * @param deltaB
   *          the delta b
   * @return the SRGB color
   */
  public SRGBColor clampedMove(final int deltaR, final int deltaG, final int deltaB) {
    int newR = r + deltaR;
    int newG = g + deltaG;
    int newB = b + deltaB;
    if (newR < 0) {
      newR = 0;
    } else if (newR > 255) {
      newR = 255;
    }
    if (newG < 0) {
      newG = 0;
    } else if (newG > 255) {
      newG = 255;
    }
    if (newB < 0) {
      newB = 0;
    } else if (newB > 255) {
      newB = 255;
    }

    return new SRGBColor(newR, newG, newB);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof SRGBColor) {
      final SRGBColor other = (SRGBColor) obj;
      return r == other.r && g == other.g && b == other.b;
    }
    return false;
  }

  /**
   * Gets the W3C luminance as stated in WCAG.
   * 
   * @return the luminance
   */
  public double getW3CLuminance() {
    if (luminance == null) {
      final double linearR = (this.rd <= 0.03928) ? this.rd / 12.92 : Math.pow((this.rd + 0.055)
          / (1 + 0.055), 2.4);
      final double linearG = (this.gd <= 0.03928) ? this.gd / 12.92 : Math.pow((this.gd + 0.055)
          / (1 + 0.055), 2.4);
      final double linearB = (this.bd <= 0.03928) ? this.bd / 12.92 : Math.pow((this.bd + 0.055)
          / (1 + 0.055), 2.4);

      luminance = 0.2126 * linearR + 0.7152 * linearG + 0.0722 * linearB;
    }
    return luminance;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return (r * 256 + g) * 256 + b;
  }

  /**
   * Move.
   * 
   * @param deltaR
   *          the delta r
   * @param deltaG
   *          the delta g
   * @param deltaB
   *          the delta b
   * @return the SRGB color
   */
  public SRGBColor move(final int deltaR, final int deltaG, final int deltaB) {
    return new SRGBColor(r + deltaR, g + deltaG, b + deltaB);
  }

  /**
   * Simulate dichromacy deficiency.
   * 
   * @param dichromacyDeficiency
   *          the dichromacy deficiency
   * @return the SRGB color
   * @see CIELABColor#simulateDichromacyDeficiency(DichromacyDeficiency)
   */
  public SRGBColor simulateDichromacyDeficiency(final DichromacyDeficiency dichromacyDeficiency) {
    switch (dichromacyDeficiency) {
      case Deuteranope:
        if (deuteranope == null) {
          deuteranope = toCIELAB().simulateDichromacyDeficiency(dichromacyDeficiency).toSRGB();
        }
        return deuteranope;
      case Protanope:
        if (protanope == null) {
          protanope = toCIELAB().simulateDichromacyDeficiency(dichromacyDeficiency).toSRGB();
        }
        return protanope;
      case Tritanope:
        if (tritanope == null) {
          tritanope = toCIELAB().simulateDichromacyDeficiency(dichromacyDeficiency).toSRGB();
        }
        return tritanope;
      default:
        throw new IllegalArgumentException("dichromacy deficiency can not be null");
    }
  }

  /**
   * Convert the color into a CIELAB color.
   * 
   * @return the color
   */
  public CIELABColor toCIELAB() {
    return toCIEXYZ().toCIELAB();
  }

  /**
   * Convert the color into a CIEXYZ color.
   * 
   * @return the color
   */
  public CIEXYZColor toCIEXYZ() {
    if (ciexyz == null) {
      final double linearR = (this.rd <= 0.04045) ? this.rd / 12.92 : Math.pow((this.rd + 0.055)
          / (1 + 0.055), 2.4);
      final double linearG = (this.gd <= 0.04045) ? this.gd / 12.92 : Math.pow((this.gd + 0.055)
          / (1 + 0.055), 2.4);
      final double linearB = (this.bd <= 0.04045) ? this.bd / 12.92 : Math.pow((this.bd + 0.055)
          / (1 + 0.055), 2.4);

      ciexyz = new CIEXYZColor(0.4124 * linearR + 0.3576 * linearG + 0.1805 * linearB, 0.2126
          * linearR + 0.7152 * linearG + 0.0722 * linearB, 0.0193 * linearR + 0.1192 * linearG
          + 0.9505 * linearB, this);
    }
    return ciexyz;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    String v = Integer.toHexString((r * 256 + g) * 256 + b);
    while (v.length() < 6) {
      v = '0' + v;
    }

    return "#" + v;
  }
}
