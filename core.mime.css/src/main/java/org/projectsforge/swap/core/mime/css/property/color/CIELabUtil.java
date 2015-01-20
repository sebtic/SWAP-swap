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

import org.apache.commons.math3.util.FastMath;

/**
 * The Class CIELabUtil.
 * 
 * @author Sébastien Aupetit
 */
public class CIELabUtil {

  /**
   * Compare colors without taking into account the L* component in the L*a*b*
   * color model (based on delta E (CIE 1976) distance but ignoring the
   * lightness (L) component.
   * 
   * @param a1 the a1
   * @param b1 the b1
   * @param a2 the a2
   * @param b2 the b2
   * @return the distance
   */
  public static double distanceAB(final double a1, final double b1, final double a2, final double b2) {
    final double da = a1 - a2;
    final double db = b1 - b2;
    return FastMath.sqrt(da * da + db * db);
  }

  /**
   * Compare colors without taking into account the L* component in the L*a*b*
   * color model (based on delta E (CIE 1976) distance but ignoring the
   * lightness (L) component.
   * 
   * @param lab1 the lab1
   * @param lab2 the lab2
   * @return the distance
   */
  public static double distanceAB(final double[] lab1, final double[] lab2) {
    final double da = lab1[1] - lab2[1];
    final double db = lab1[2] - lab2[2];
    return FastMath.sqrt(da * da + db * db);
  }

  /**
   * Compare colors taking into account only the L* component in the L*a*b*
   * color model (based on delta E (CIE 1976) distance but ignoring the a and b
   * components.
   * 
   * @param l1 the l1
   * @param l2 the l2
   * @return the distance
   */
  public static double distanceL(final double l1, final double l2) {
    final double dl = l1 - l2;
    return FastMath.abs(dl); // same as the square root or the squared value
  }

  /**
   * Compare colors taking into account only the L* component in the L*a*b*
   * color model (based on delta E (CIE 1976) distance but ignoring the a and b
   * components.
   * 
   * @param lab1 the lab1
   * @param lab2 the lab2
   * @return the distance
   */
  public static double distanceL(final double[] lab1, final double[] lab2) {
    final double dl = lab1[0] - lab2[0];
    return FastMath.abs(dl); // same as the square root or the squared value
  }

  /**
   * Compare colors using the delta E (CIE 1976) distance.
   * 
   * @param l1 the l1
   * @param a1 the a1
   * @param b1 the b1
   * @param l2 the l2
   * @param a2 the a2
   * @param b2 the b2
   * @return the distance
   */
  public static double distanceLAB(final double l1, final double a1, final double b1,
      final double l2, final double a2, final double b2) {
    final double dl = l1 - l2;
    final double da = a1 - a2;
    final double db = b1 - b2;
    return FastMath.sqrt(dl * dl + da * da + db * db);
  }

  /**
   * Compare colors using the delta E (CIE 1976) distance.
   * 
   * @param lab1 the lab1
   * @param lab2 the lab2
   * @return the distance
   */
  public static double distanceLAB(final double[] lab1, final double[] lab2) {
    final double dl = lab1[0] - lab2[0];
    final double da = lab1[1] - lab2[1];
    final double db = lab1[2] - lab2[2];
    return FastMath.sqrt(dl * dl + da * da + db * db);
  }

  /**
   * Instantiates a new cIE lab util.
   */
  private CIELabUtil() {
  }
}
