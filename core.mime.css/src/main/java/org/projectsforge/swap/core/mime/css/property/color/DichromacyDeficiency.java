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

/**
 * The Enum DichromacyDefficiency.
 * 
 * @author Sébastien Aupetit
 */
public enum DichromacyDeficiency {

  /** The Deuteranope. */
  Deuteranope,
  /** The Protanope. */
  Protanope,
  /** The Tritanope. */
  Tritanope;

  /**
   * Simulate dichromacy deficiency from formula of "An efficient
   * naturalness-preserving image-recoloring method for dichromats, G. R. Kuhn,
   * M. M. Oliveira, L. A. F. Fernandes"
   * 
   * @param dichromacyDeficiency the dichromacy deficiency
   * @param L the l
   * @param a the a
   * @param b the b
   * @return the CIELab color
   */
  public static double[] kuhnSimulation(final DichromacyDeficiency dichromacyDeficiency,
      final double L, final double a, final double b) {
    switch (dichromacyDeficiency) {
      case Deuteranope:
        return new double[] { 1. * L + 0. * a + 0. * b, 0. * L + 0.0199019 * a + -0.1396631 * b,
            0. * L + -0.1396631 * a + 0.9800982 * b };
      case Protanope:
        return new double[] { 1. * L + 0. * a + 0. * b, 0. * L + 0.0396113 * a + -0.1950442 * b,
            0. * L + -0.1950442 * a + 0.9603886 * b };
      case Tritanope:
        return new double[] { 1. * L + 0. * a + 0. * b, 0. * L + 0.5239019 * a + 0.4994284 * b,
            0. * L + 0.4994284 * a + 0.4760981 * b };
      default:
        throw new IllegalArgumentException("Argument can not be null");
    }
  }

  /**
   * Simulate dichromacy deficiency from formula of "An efficient
   * naturalness-preserving image-recoloring method for dichromats, G. R. Kuhn,
   * M. M. Oliveira, L. A. F. Fernandes"
   * 
   * @param dichromacyDeficiency the dichromacy deficiency
   * @param lab the lab
   * @return the CIELab color
   */
  public static double[] kuhnSimulation(final DichromacyDeficiency dichromacyDeficiency,
      final double[] lab) {
    final double L = lab[0];
    final double a = lab[1];
    final double b = lab[2];
    switch (dichromacyDeficiency) {
      case Deuteranope:
        return new double[] { 1. * L + 0. * a + 0. * b, 0. * L + 0.0199019 * a + -0.1396631 * b,
            0. * L + -0.1396631 * a + 0.9800982 * b };
      case Protanope:
        return new double[] { 1. * L + 0. * a + 0. * b, 0. * L + 0.0396113 * a + -0.1950442 * b,
            0. * L + -0.1950442 * a + 0.9603886 * b };
      case Tritanope:
        return new double[] { 1. * L + 0. * a + 0. * b, 0. * L + 0.5239019 * a + 0.4994284 * b,
            0. * L + 0.4994284 * a + 0.4760981 * b };
      default:
        throw new IllegalArgumentException("Argument can not be null");
    }
  }

  /**
   * Simulate dichromacy deficiency from formula of "An efficient
   * naturalness-preserving image-recoloring method for dichromats, G. R. Kuhn,
   * M. M. Oliveira, L. A. F. Fernandes"
   * 
   * @param dichromacyDeficiency the dichromacy deficiency
   * @param lab the lab
   * @return the CIELab color
   */
  public static void kuhnSimulation(final DichromacyDeficiency dichromacyDeficiency,
      final double[] inputLab, final double[] outputLab) {
    final double L = inputLab[0];
    final double a = inputLab[1];
    final double b = inputLab[2];
    switch (dichromacyDeficiency) {
      case Deuteranope:
        outputLab[0] = 1. * L + 0. * a + 0. * b;
        outputLab[1] = 0. * L + 0.0199019 * a + -0.1396631 * b;
        outputLab[2] = 0. * L + -0.1396631 * a + 0.9800982 * b;
        break;
      case Protanope:
        outputLab[0] = 1. * L + 0. * a + 0. * b;
        outputLab[1] = 0. * L + 0.0396113 * a + -0.1950442 * b;
        outputLab[2] = 0. * L + -0.1950442 * a + 0.9603886 * b;
        break;
      case Tritanope:
        outputLab[0] = 1. * L + 0. * a + 0. * b;
        outputLab[1] = 0. * L + 0.5239019 * a + 0.4994284 * b;
        outputLab[2] = 0. * L + 0.4994284 * a + 0.4760981 * b;
        break;
      default:
        throw new IllegalArgumentException("Argument can not be null");
    }
  }
}
