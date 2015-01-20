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
 * The Class W3CUtil.
 * 
 * @author Sébastien Aupetit
 */
public class W3CUtil {

  /** The Constant CONTRASTRATIO_MINVALUE. */
  public static final double CONTRASTRATIO_MINVALUE = 1;

  /** The Constant CONTRASTRATIO_MAXVALUE. */
  public static final double CONTRASTRATIO_MAXVALUE = 21;

  /** The Constant BRIGHTNESSDIFFERENCE_MINVALUE. */
  public static final double BRIGHTNESSDIFFERENCE_MINVALUE = 0;

  /** The Constant BRIGHTNESSDIFFERENCE_MAXVALUE. */
  public static final double BRIGHTNESSDIFFERENCE_MAXVALUE = 255;

  /** The Constant TONALITYDIFFERENCE_MINVALUE. */
  public static final double TONALITYDIFFERENCE_MINVALUE = 0;

  /** The Constant TONALITYDIFFERENCE_MAXVALUE. */
  public static final double TONALITYDIFFERENCE_MAXVALUE = 255 * 3;

  /** The Constant WCAG2_LEVELAA_CONTRASTRATIO. */
  public static final double WCAG2_LEVELAA_CONTRASTRATIO = 4.5;

  /** The Constant WCAG2_LEVELAAA_CONTRASTRATIO. */
  public static final double WCAG2_LEVELAAA_CONTRASTRATIO = 7;

  /** The Constant WCAG1_TONALITYDIFFERENCE. */
  public static final double WCAG1_TONALITYDIFFERENCE = 500;

  /** The Constant WCAG1_BRIGHTNESSDIFFERENCE. */
  public static final double WCAG1_BRIGHTNESSDIFFERENCE = 125;

  /** Cached values of sRGB to linear RGB components */
  private static final double[] linearRGB;

  /** do some precalculus to reduce computation time */
  static {
    linearRGB = new double[256];
    for (int i = 0; i <= 255; i++) {
      final double d = i / 255.;
      W3CUtil.linearRGB[i] = d <= 0.03928 ? d / 12.92 : FastMath.pow((d + 0.055) / (1 + 0.055), 2.4);
    }
  }

  /**
   * Brightness difference.
   * 
   * @param srgb1
   *          the srgb1
   * @param srgb2
   *          the srgb2
   * @return the double
   */
  public static double brightnessDifference(final int[] srgb1, final int[] srgb2) {
    return FastMath.abs(0.299 * (srgb1[0] - srgb2[0]) + 0.587 * (srgb1[1] - srgb2[1]) + 0.114 * (srgb1[2] - srgb2[2]));
  }

  /**
   * Brightness difference.
   * 
   * @param srgb1
   *          the srgb1
   * @param srgb2
   *          the srgb2
   * @return the double
   */
  public static double brightnessDifference(final SRGBColor srgb1, final SRGBColor srgb2) {
    return FastMath.abs(0.299 * (srgb1.getR() - srgb2.getR()) + 0.587 * (srgb1.getG() - srgb2.getG()) + 0.114
        * (srgb1.getB() - srgb2.getB()));
  }

  /**
   * Contrast ratio.
   * 
   * @param srgb1
   *          the srgb1
   * @param srgb2
   *          the srgb2
   * @return the double
   */
  public static double contrastRatio(final int[] srgb1, final int[] srgb2) {
    double l1 = W3CUtil.getsRGBLuminance(srgb1);
    double l2 = W3CUtil.getsRGBLuminance(srgb2);
    double l;
    if (l1 < l2) {
      l = l1;
      l1 = l2;
      l2 = l;
    }
    // l1 >= l2
    return (l1 + 0.05) / (l2 + 0.05);
  }

  /**
   * Contrast ratio.
   * 
   * @param srgb1
   *          the srgb1
   * @param srgb2
   *          the srgb2
   * @return the double
   */
  public static double contrastRatio(final SRGBColor srgb1, final SRGBColor srgb2) {
    double l1 = srgb1.getW3CLuminance();
    double l2 = srgb2.getW3CLuminance();
    double l;
    if (l1 < l2) {
      l = l1;
      l1 = l2;
      l2 = l;
    }
    // l1 >= l2
    return (l1 + 0.05) / (l2 + 0.05);
  }

  /**
   * Gets the W3C luminance of a sRGB color as stated in WCAG.
   * 
   * @param r
   *          the r
   * @param g
   *          the g
   * @param b
   *          the b
   * @return the luminance
   */
  public static double getsRGBLuminance(final int r, final int g, final int b) {
    /*
     * replace calculus with precalculus
     * 
     * final double rd = r / 255.;
     * final double gd = g / 255.;
     * final double bd = b / 255.;
     * final double linearR = (rd <= 0.03928) ? rd / 12.92 : FastMath.pow((rd + 0.055) / (1 + 0.055), 2.4);
     * final double linearG = (gd <= 0.03928) ? gd / 12.92 : FastMath.pow((gd + 0.055) / (1 + 0.055), 2.4);
     * final double linearB = (bd <= 0.03928) ? bd / 12.92 : FastMath.pow((bd + 0.055) / (1 + 0.055), 2.4);
     */
    return 0.2126 * W3CUtil.linearRGB[r] + 0.7152 * W3CUtil.linearRGB[g] + 0.0722 * W3CUtil.linearRGB[b];
  }

  /**
   * Gets the W3C luminance of a sRGB color as stated in WCAG.
   * 
   * @param rgb
   *          the rgb
   * @return the luminance
   */
  public static double getsRGBLuminance(final int[] rgb) {
    /*
     * replace calculus with precalculus
     * 
     * final double rd = rgb[0] / 255.;
     * final double gd = rgb[1] / 255.;
     * final double bd = rgb[2] / 255.;
     * final double linearR = rd <= 0.03928 ? rd / 12.92 : FastMath.pow((rd + 0.055) / (1 + 0.055), 2.4);
     * final double linearG = gd <= 0.03928 ? gd / 12.92 : FastMath.pow((gd + 0.055) / (1 + 0.055), 2.4);
     * final double linearB = bd <= 0.03928 ? bd / 12.92 : FastMath.pow((bd + 0.055) / (1 + 0.055), 2.4);
     */

    return 0.2126 * W3CUtil.linearRGB[rgb[0]] + 0.7152 * W3CUtil.linearRGB[rgb[1]] + 0.0722 * W3CUtil.linearRGB[rgb[2]];
  }

  public static double tonalityDifference(final int c1r, final int c1g, final int c1b, final int c2r, final int c2g,
      final int c2b) {
    return FastMath.abs(c1r - c2r) + FastMath.abs(c1g - c2g) + FastMath.abs(c1b - c2b);
  }

  public static double tonalityDifference(final int[] c1, final int[] c2) {
    return FastMath.abs(c1[0] - c2[0]) + FastMath.abs(c1[1] - c2[1]) + FastMath.abs(c1[2] - c2[2]);
  }

  /**
   * Tonality difference.
   * 
   * @param c1
   *          the c1
   * @param c2
   *          the c2
   * @return the double
   */
  public static double tonalityDifference(final SRGBColor c1, final SRGBColor c2) {
    return FastMath.abs(c1.getR() - c2.getR()) + FastMath.abs(c1.getG() - c2.getG())
        + FastMath.abs(c1.getB() - c2.getB());
  }

  /**
   * Instantiates a new w3 c util.
   */
  private W3CUtil() {
    // nothing to do
  }

}
