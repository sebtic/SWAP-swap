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
 * The Class ColorSpaceUtil.
 * 
 * @author Sébastien Aupetit
 */
public class ColorSpaceUtil {

  /*
   * http://en.wikipedia.org/wiki/Color_difference#cite_note-5
   * http://en.wikipedia.org/wiki/CIE_1931_color_space
   * http://en.wikipedia.org/wiki/SRGB_color_space
   * http://en.wikipedia.org/wiki/RGB_color_space
   * http://www.cambridgeincolour.com/tutorials/color-spaces.htm
   * http://www.brucelindbloom.com/index.html?Eqn_XYZ_to_Lab.html
   * http://www.easyrgb.com/index.php?X=MATH&H=08#text8
   */

  /** Cached values of sRGB to linear RGB components */
  private static final double[] linearRGB;

  /** do some precalculus to reduce computation time */
  static {
    linearRGB = new double[256];
    for (int i = 0; i <= 255; i++) {
      final double d = i / 255.;
      ColorSpaceUtil.linearRGB[i] = d <= 0.03928 ? d / 12.92 : FastMath.pow((d + 0.055) / (1 + 0.055), 2.4);
    }
  }

  /**
   * The D65 white reference color used in sRGB for web standards (X component).
   */
  private static final double d65WhiteX = 0.9505;

  /**
   * The D65 white reference color used in sRGB for web standards (Y component).
   */
  private static final double d65WhiteY = 1.000;

  /**
   * The D65 white reference color used in sRGB for web standards (Z component).
   */
  private static final double d65WhiteZ = 1.0890;

  /**
   * The D65 white reference color used in sRGB for web standards.
   * 
   * @return D65 white in XYZ color space
   */
  public static double[] getD65White() {
    return new double[] { ColorSpaceUtil.d65WhiteX, ColorSpaceUtil.d65WhiteY, ColorSpaceUtil.d65WhiteZ };
  }

  /**
   * Lab to srgb.
   * 
   * @param l the l
   * @param a the a
   * @param b the b
   * @return the int[]
   */
  public static int[] LabToSRGB(final double l, final double a, final double b) {
    final int[] result = new int[3];
    ColorSpaceUtil.LabToSRGB(l, a, b, result);
    return result;
  }

  public static void LabToSRGB(final double l, final double a, final double b, final int[] result) {

    final double fY = (l + 16) / 116;
    final double fX = a / 500 + fY;
    final double fZ = fY - b / 200;

    final double X = fX * fX * fX > 0.008856 ? fX * fX * fX : (116 * fX - 16) / 903.3;
    final double Y = l > 903.3 * 0.008856 ? FastMath.pow((l + 16) / 116, 3) : l / 903.3;
    final double Z = fZ * fZ * fZ > 0.008856 ? fZ * fZ * fZ : (116 * fZ - 16) / 903.3;
    final double x = X * ColorSpaceUtil.d65WhiteX;
    final double y = Y * ColorSpaceUtil.d65WhiteY;
    final double z = Z * ColorSpaceUtil.d65WhiteZ;

    final double linearR = 3.2406 * x - 1.5372 * y - 0.4986 * z;
    final double linearG = -0.9689 * x + 1.8758 * y + 0.0415 * z;
    final double linearB = 0.0557 * x - 0.2040 * y + 1.0570 * z;

    final double nonLinearR = linearR <= 0.0031308 ? 12.92 * linearR : (1 + 0.055) * FastMath.pow(linearR, 1.0 / 2.4)
        - 0.055;
    final double nonLinearG = linearG <= 0.0031308 ? 12.92 * linearG : (1 + 0.055) * FastMath.pow(linearG, 1.0 / 2.4)
        - 0.055;
    final double nonLinearB = linearB <= 0.0031308 ? 12.92 * linearB : (1 + 0.055) * FastMath.pow(linearB, 1.0 / 2.4)
        - 0.055;

    final int intR = (int) FastMath.round(nonLinearR * 255);
    final int intG = (int) FastMath.round(nonLinearG * 255);
    final int intB = (int) FastMath.round(nonLinearB * 255);

    result[0] = FastMath.max(FastMath.min(intR, 255), 0);
    result[1] = FastMath.max(FastMath.min(intG, 255), 0);
    result[2] = FastMath.max(FastMath.min(intB, 255), 0);
  }

  /**
   * Lab to srgb.
   * 
   * @param lab the lab
   * @return the int[]
   */
  public static int[] LabToSRGB(final double[] lab) {
    return ColorSpaceUtil.XYZToSRGB(ColorSpaceUtil.LabToXYZ(lab));
  }

  public static void LabToSRGB(final double[] lab, final int[] result) {
    final double l = lab[0];
    final double a = lab[1];
    final double b = lab[2];

    final double fY = (l + 16) / 116;
    final double fX = a / 500 + fY;
    final double fZ = fY - b / 200;

    final double X = fX * fX * fX > 0.008856 ? fX * fX * fX : (116 * fX - 16) / 903.3;
    final double Y = l > 903.3 * 0.008856 ? FastMath.pow((l + 16) / 116, 3) : l / 903.3;
    final double Z = fZ * fZ * fZ > 0.008856 ? fZ * fZ * fZ : (116 * fZ - 16) / 903.3;
    final double x = X * ColorSpaceUtil.d65WhiteX;
    final double y = Y * ColorSpaceUtil.d65WhiteY;
    final double z = Z * ColorSpaceUtil.d65WhiteZ;

    final double linearR = 3.2406 * x - 1.5372 * y - 0.4986 * z;
    final double linearG = -0.9689 * x + 1.8758 * y + 0.0415 * z;
    final double linearB = 0.0557 * x - 0.2040 * y + 1.0570 * z;

    final double nonLinearR = linearR <= 0.0031308 ? 12.92 * linearR : (1 + 0.055) * FastMath.pow(linearR, 1.0 / 2.4)
        - 0.055;
    final double nonLinearG = linearG <= 0.0031308 ? 12.92 * linearG : (1 + 0.055) * FastMath.pow(linearG, 1.0 / 2.4)
        - 0.055;
    final double nonLinearB = linearB <= 0.0031308 ? 12.92 * linearB : (1 + 0.055) * FastMath.pow(linearB, 1.0 / 2.4)
        - 0.055;

    final int intR = (int) FastMath.round(nonLinearR * 255);
    final int intG = (int) FastMath.round(nonLinearG * 255);
    final int intB = (int) FastMath.round(nonLinearB * 255);

    result[0] = FastMath.max(FastMath.min(intR, 255), 0);
    result[1] = FastMath.max(FastMath.min(intG, 255), 0);
    result[2] = FastMath.max(FastMath.min(intB, 255), 0);
  }

  /**
   * Convert the color to a CIE XYZ color.
   * 
   * @param l the l
   * @param a the a
   * @param b the b
   * @return the color
   */
  public static double[] LabToXYZ(final double l, final double a, final double b) {
    final double fY = (l + 16) / 116;
    final double fX = a / 500 + fY;
    final double fZ = fY - b / 200;

    final double X = fX * fX * fX > 0.008856 ? fX * fX * fX : (116 * fX - 16) / 903.3;
    final double Y = l > 903.3 * 0.008856 ? FastMath.pow((l + 16) / 116, 3) : l / 903.3;
    final double Z = fZ * fZ * fZ > 0.008856 ? fZ * fZ * fZ : (116 * fZ - 16) / 903.3;
    return new double[] { X * ColorSpaceUtil.d65WhiteX, Y * ColorSpaceUtil.d65WhiteY, Z * ColorSpaceUtil.d65WhiteZ };
  }

  /**
   * Convert the color to a CIE XYZ color.
   * 
   * @param Lab the lab
   * @return the color
   */
  public static double[] LabToXYZ(final double[] Lab) {
    final double fY = (Lab[0] + 16) / 116;
    final double fX = Lab[1] / 500 + fY;
    final double fZ = fY - Lab[2] / 200;

    final double X = fX * fX * fX > 0.008856 ? fX * fX * fX : (116 * fX - 16) / 903.3;
    final double Y = Lab[0] > 903.3 * 0.008856 ? (Lab[0] + 16) / 116 * ((Lab[0] + 16) / 116) * ((Lab[0] + 16) / 116)
        : Lab[0] / 903.3;
    final double Z = fZ * fZ * fZ > 0.008856 ? fZ * fZ * fZ : (116 * fZ - 16) / 903.3;
    return new double[] { X * ColorSpaceUtil.d65WhiteX, Y * ColorSpaceUtil.d65WhiteY, Z * ColorSpaceUtil.d65WhiteZ };
  }

  public static double[] SRGBToLab(final int r, final int g, final int b) {
    final double[] result = new double[3];
    ColorSpaceUtil.SRGBToLab(r, g, b, result);
    return result;
  }

  public static void SRGBToLab(final int r, final int g, final int b, final double[] result) {
    /*
     * replace calculus with precalculus final double rd = r / 255.; final
     * double gd = g / 255.; final double bd = b / 255.; final double linearR =
     * (rd <= 0.04045) ? rd / 12.92 : FastMath.pow((rd + 0.055) / (1 + 0.055),
     * 2.4); final double linearG = (gd <= 0.04045) ? gd / 12.92 :
     * FastMath.pow((gd + 0.055) / (1 + 0.055), 2.4); final double linearB = (bd
     * <= 0.04045) ? bd / 12.92 : FastMath.pow((bd + 0.055) / (1 + 0.055), 2.4);
     */
    final double linearR = ColorSpaceUtil.linearRGB[r];
    final double linearG = ColorSpaceUtil.linearRGB[g];
    final double linearB = ColorSpaceUtil.linearRGB[b];

    final double x = 0.4124 * linearR + 0.3576 * linearG + 0.1805 * linearB;
    final double y = 0.2126 * linearR + 0.7152 * linearG + 0.0722 * linearB;
    final double z = 0.0193 * linearR + 0.1192 * linearG + 0.9505 * linearB;

    final double relativeX = x / ColorSpaceUtil.d65WhiteX;
    final double relativeY = y / ColorSpaceUtil.d65WhiteY;
    final double relativeZ = z / ColorSpaceUtil.d65WhiteZ;

    final double fX = relativeX > 0.008856 ? FastMath.cbrt(relativeX) : 7.787 * relativeX + 16 / 116.;
    final double fY = relativeY > 0.008856 ? FastMath.cbrt(relativeY) : 7.787 * relativeY + 16 / 116.;
    final double fZ = relativeZ > 0.008856 ? FastMath.cbrt(relativeZ) : 7.787 * relativeZ + 16 / 116.;

    result[0] = 116 * fY - 16;
    result[1] = 500 * (fX - fY);
    result[2] = 200 * (fY - fZ);
  }

  /**
   * SRGB to lab.
   * 
   * @param rgb the rgb
   * @return the double[]
   */
  public static double[] SRGBToLab(final int[] rgb) {
    return ColorSpaceUtil.XYZToLab(ColorSpaceUtil.SRGBToXYZ(rgb));
  }

  /**
   * SRGB to xyz.
   * 
   * @param r the r
   * @param g the g
   * @param b the b
   * @return the double[]
   */
  public static double[] SRGBToXYZ(final int r, final int g, final int b) {
    /*
     * replace calculus with precalculus final double rd = r / 255.; final
     * double gd = g / 255.; final double bd = b / 255.; final double linearR =
     * (rd <= 0.04045) ? rd / 12.92 : FastMath.pow((rd + 0.055) / (1 + 0.055),
     * 2.4); final double linearG = (gd <= 0.04045) ? gd / 12.92 :
     * FastMath.pow((gd + 0.055) / (1 + 0.055), 2.4); final double linearB = (bd
     * <= 0.04045) ? bd / 12.92 : FastMath.pow((bd + 0.055) / (1 + 0.055), 2.4);
     */
    final double linearR = ColorSpaceUtil.linearRGB[r];
    final double linearG = ColorSpaceUtil.linearRGB[g];
    final double linearB = ColorSpaceUtil.linearRGB[b];

    return new double[] { 0.4124 * linearR + 0.3576 * linearG + 0.1805 * linearB,
        0.2126 * linearR + 0.7152 * linearG + 0.0722 * linearB, 0.0193 * linearR + 0.1192 * linearG + 0.9505 * linearB };
  }

  /**
   * SRGB to xyz.
   * 
   * @param rgb the rgb
   * @return the double[]
   */
  public static double[] SRGBToXYZ(final int[] rgb) {
    /*
     * replace calculus with precalculus final double rd = rgb[0] / 255.; final
     * double gd = rgb[1] / 255.; final double bd = rgb[2] / 255.; final double
     * linearR = (rd <= 0.04045) ? rd / 12.92 : FastMath.pow((rd + 0.055) / (1 +
     * 0.055), 2.4); final double linearG = (gd <= 0.04045) ? gd / 12.92 :
     * FastMath.pow((gd + 0.055) / (1 + 0.055), 2.4); final double linearB = (bd
     * <= 0.04045) ? bd / 12.92 : FastMath.pow((bd + 0.055) / (1 + 0.055), 2.4);
     */
    final double linearR = ColorSpaceUtil.linearRGB[rgb[0]];
    final double linearG = ColorSpaceUtil.linearRGB[rgb[1]];
    final double linearB = ColorSpaceUtil.linearRGB[rgb[2]];

    return new double[] { 0.4124 * linearR + 0.3576 * linearG + 0.1805 * linearB,
        0.2126 * linearR + 0.7152 * linearG + 0.0722 * linearB, 0.0193 * linearR + 0.1192 * linearG + 0.9505 * linearB };
  }

  /**
   * SRGB to xyz.
   * 
   * @param rgb the rgb
   * @return the double[]
   */
  public static void SRGBToXYZ(final int[] rgb, final double[] xyz) {
    /*
     * replace calculus with precalculus final double rd = rgb[0] / 255.; final
     * double gd = rgb[1] / 255.; final double bd = rgb[2] / 255.; final double
     * linearR = (rd <= 0.04045) ? rd / 12.92 : FastMath.pow((rd + 0.055) / (1 +
     * 0.055), 2.4); final double linearG = (gd <= 0.04045) ? gd / 12.92 :
     * FastMath.pow((gd + 0.055) / (1 + 0.055), 2.4); final double linearB = (bd
     * <= 0.04045) ? bd / 12.92 : FastMath.pow((bd + 0.055) / (1 + 0.055), 2.4);
     */
    final double linearR = ColorSpaceUtil.linearRGB[rgb[0]];
    final double linearG = ColorSpaceUtil.linearRGB[rgb[1]];
    final double linearB = ColorSpaceUtil.linearRGB[rgb[2]];

    xyz[0] = 0.4124 * linearR + 0.3576 * linearG + 0.1805 * linearB;
    xyz[1] = 0.2126 * linearR + 0.7152 * linearG + 0.0722 * linearB;
    xyz[2] = 0.0193 * linearR + 0.1192 * linearG + 0.9505 * linearB;
  }

  /**
   * Compute the hexadecimal representation of a sRGB color.
   * 
   * @param r the red
   * @param g the green
   * @param b the blue
   * @return the string
   */
  public static String toString(final int r, final int g, final int b) {
    String v = Integer.toHexString((r << 16) + (g << 8) + b);
    while (v.length() < 6) {
      v = '0' + v;
    }
    return "#" + v;
  }

  /**
   * Convert a CIE XYZ color to a CIE LAB color.
   * 
   * @param x the x
   * @param y the y
   * @param z the z
   * @return the color
   */
  public static double[] XYZToLab(final double x, final double y, final double z) {

    final double relativeX = x / ColorSpaceUtil.d65WhiteX;
    final double relativeY = y / ColorSpaceUtil.d65WhiteY;
    final double relativeZ = z / ColorSpaceUtil.d65WhiteZ;

    final double fX = relativeX > 0.008856 ? FastMath.cbrt(relativeX) : 7.787 * relativeX + 16 / 116.;
    final double fY = relativeY > 0.008856 ? FastMath.cbrt(relativeY) : 7.787 * relativeY + 16 / 116.;
    final double fZ = relativeZ > 0.008856 ? FastMath.cbrt(relativeZ) : 7.787 * relativeZ + 16 / 116.;

    return new double[] { 116 * fY - 16, 500 * (fX - fY), 200 * (fY - fZ) };
  }

  /**
   * Convert a CIE XYZ color to a CIE LAB color.
   * 
   * @param xyz the xyz
   * @return the color
   */
  public static double[] XYZToLab(final double[] xyz) {
    final double relativeX = xyz[0] / ColorSpaceUtil.d65WhiteX;
    final double relativeY = xyz[1] / ColorSpaceUtil.d65WhiteY;
    final double relativeZ = xyz[2] / ColorSpaceUtil.d65WhiteZ;

    final double fX = relativeX > 0.008856 ? FastMath.cbrt(relativeX) : 7.787 * relativeX + 16 / 116.;
    final double fY = relativeY > 0.008856 ? FastMath.cbrt(relativeY) : 7.787 * relativeY + 16 / 116.;
    final double fZ = relativeZ > 0.008856 ? FastMath.cbrt(relativeZ) : 7.787 * relativeZ + 16 / 116.;

    return new double[] { 116 * fY - 16, 500 * (fX - fY), 200 * (fY - fZ) };
  }

  /**
   * Convert a CIE XYZ color to a CIE LAB color.
   * 
   * @param xyz the xyz
   * @return the color
   */
  public static void XYZToLab(final double[] xyz, final double[] lab) {
    final double relativeX = xyz[0] / ColorSpaceUtil.d65WhiteX;
    final double relativeY = xyz[1] / ColorSpaceUtil.d65WhiteY;
    final double relativeZ = xyz[2] / ColorSpaceUtil.d65WhiteZ;

    final double fX = relativeX > 0.008856 ? FastMath.cbrt(relativeX) : 7.787 * relativeX + 16 / 116.;
    final double fY = relativeY > 0.008856 ? FastMath.cbrt(relativeY) : 7.787 * relativeY + 16 / 116.;
    final double fZ = relativeZ > 0.008856 ? FastMath.cbrt(relativeZ) : 7.787 * relativeZ + 16 / 116.;

    lab[0] = 116 * fY - 16;
    lab[1] = 500 * (fX - fY);
    lab[2] = 200 * (fY - fZ);
  }

  /**
   * XYZ to srgb.
   * 
   * @param x the x
   * @param y the y
   * @param z the z
   * @return the int[]
   */
  public static int[] XYZToSRGB(final double x, final double y, final double z) {
    final double linearR = 3.2406 * x - 1.5372 * y - 0.4986 * z;
    final double linearG = -0.9689 * x + 1.8758 * y + 0.0415 * z;
    final double linearB = 0.0557 * x - 0.2040 * y + 1.0570 * z;

    final double nonLinearR = linearR <= 0.0031308 ? 12.92 * linearR : (1 + 0.055) * FastMath.pow(linearR, 1.0 / 2.4)
        - 0.055;
    final double nonLinearG = linearG <= 0.0031308 ? 12.92 * linearG : (1 + 0.055) * FastMath.pow(linearG, 1.0 / 2.4)
        - 0.055;
    final double nonLinearB = linearB <= 0.0031308 ? 12.92 * linearB : (1 + 0.055) * FastMath.pow(linearB, 1.0 / 2.4)
        - 0.055;

    final int intR = (int) FastMath.round(nonLinearR * 255);
    final int intG = (int) FastMath.round(nonLinearG * 255);
    final int intB = (int) FastMath.round(nonLinearB * 255);

    return new int[] { FastMath.max(FastMath.min(intR, 255), 0), FastMath.max(FastMath.min(intG, 255), 0),
        FastMath.max(FastMath.min(intB, 255), 0), };
  }

  /**
   * XYZ to srgb.
   * 
   * @param xyz the xyz
   * @return the int[]
   */
  public static int[] XYZToSRGB(final double[] xyz) {
    final double x = xyz[0];
    final double y = xyz[1];
    final double z = xyz[2];
    final double linearR = 3.2406 * x - 1.5372 * y - 0.4986 * z;
    final double linearG = -0.9689 * x + 1.8758 * y + 0.0415 * z;
    final double linearB = 0.0557 * x - 0.2040 * y + 1.0570 * z;

    final double nonLinearR = linearR <= 0.0031308 ? 12.92 * linearR : (1 + 0.055) * FastMath.pow(linearR, 1.0 / 2.4)
        - 0.055;
    final double nonLinearG = linearG <= 0.0031308 ? 12.92 * linearG : (1 + 0.055) * FastMath.pow(linearG, 1.0 / 2.4)
        - 0.055;
    final double nonLinearB = linearB <= 0.0031308 ? 12.92 * linearB : (1 + 0.055) * FastMath.pow(linearB, 1.0 / 2.4)
        - 0.055;

    final int intR = (int) FastMath.round(nonLinearR * 255);
    final int intG = (int) FastMath.round(nonLinearG * 255);
    final int intB = (int) FastMath.round(nonLinearB * 255);

    return new int[] { FastMath.max(FastMath.min(intR, 255), 0), FastMath.max(FastMath.min(intG, 255), 0),
        FastMath.max(FastMath.min(intB, 255), 0), };
  }

  /**
   * Instantiates a new color space util.
   */
  private ColorSpaceUtil() {
  }
}
