/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr> This file
 * is part of SWAP. SWAP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version. SWAP is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with SWAP. If not, see
 * <http://www.gnu.org/licenses/>. $Id$
 */
package org.projectsforge.swap.core.mime.css;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import org.w3c.css.sac.LexicalUnit;

/**
 * The LexicalUnit utility class.
 */
public class LexicalUnitUtil {

  /** The Constant DECIMAL_FORMAT. */
  private final static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.##",
      new DecimalFormatSymbols(new Locale("en")));

  /**
   * To string.
   * 
   * @param lu the lu
   * @return the string
   */
  public static String toString(LexicalUnit lu) {
    final StringBuilder sb = new StringBuilder();

    while (lu != null) {
      if (sb.length() > 0) {
        sb.append(' ');
      }

      final int lexicalUnitType = lu.getLexicalUnitType();
      switch (lexicalUnitType) {
        case LexicalUnit.SAC_INTEGER:
          sb.append(lu.getIntegerValue());
          break;
        case LexicalUnit.SAC_REAL:
          sb.append(lu.getFloatValue());
          break;
        case LexicalUnit.SAC_DIMENSION:
        case LexicalUnit.SAC_EM:
        case LexicalUnit.SAC_EX:
        case LexicalUnit.SAC_PIXEL:
        case LexicalUnit.SAC_INCH:
        case LexicalUnit.SAC_CENTIMETER:
        case LexicalUnit.SAC_MILLIMETER:
        case LexicalUnit.SAC_POINT:
        case LexicalUnit.SAC_PICA:
        case LexicalUnit.SAC_PERCENTAGE:
        case LexicalUnit.SAC_DEGREE:
        case LexicalUnit.SAC_GRADIAN:
        case LexicalUnit.SAC_RADIAN:
        case LexicalUnit.SAC_MILLISECOND:
        case LexicalUnit.SAC_SECOND:
        case LexicalUnit.SAC_HERTZ:
        case LexicalUnit.SAC_KILOHERTZ:
          sb.append(LexicalUnitUtil.DECIMAL_FORMAT.format(lu.getFloatValue())).append(
              lu.getDimensionUnitText());
          break;
        case LexicalUnit.SAC_IDENT:
        case LexicalUnit.SAC_STRING_VALUE:
        case LexicalUnit.SAC_UNICODERANGE:
          sb.append(lu.getStringValue());
          break;
        case LexicalUnit.SAC_URI:
          sb.append("url").append(lu.getStringValue());
          break;
        case LexicalUnit.SAC_ATTR:
        case LexicalUnit.SAC_COUNTER_FUNCTION:
        case LexicalUnit.SAC_COUNTERS_FUNCTION:
        case LexicalUnit.SAC_RECT_FUNCTION:
        case LexicalUnit.SAC_FUNCTION:
          sb.append(lu.getFunctionName()).append('(')
              .append(LexicalUnitUtil.toString(lu.getParameters())).append(')');
          break;
        case LexicalUnit.SAC_RGBCOLOR:
          sb.append("rgb(").append(LexicalUnitUtil.toString(lu.getParameters())).append(")");
          break;
        case LexicalUnit.SAC_INHERIT:
          sb.append("inherit");
          break;
        case LexicalUnit.SAC_OPERATOR_COMMA:
          sb.append(',');
          break;
        case LexicalUnit.SAC_OPERATOR_SLASH:
          sb.append("/");
          break;

        case LexicalUnit.SAC_OPERATOR_PLUS:
          sb.append("+");
          break;
        case LexicalUnit.SAC_OPERATOR_MINUS:
          sb.append("-");
          break;
        case LexicalUnit.SAC_OPERATOR_MULTIPLY:
          sb.append("*");
          break;
        case LexicalUnit.SAC_OPERATOR_MOD:
          sb.append("%");
          break;
        case LexicalUnit.SAC_OPERATOR_EXP:
          sb.append("^");
          break;
        case LexicalUnit.SAC_OPERATOR_LT:
          sb.append("<");
          break;
        case LexicalUnit.SAC_OPERATOR_GT:
          sb.append(">");
          break;
        case LexicalUnit.SAC_OPERATOR_LE:
          sb.append("<=");
          break;
        case LexicalUnit.SAC_OPERATOR_GE:
          sb.append(">=");
          break;
        case LexicalUnit.SAC_OPERATOR_TILDE:
          sb.append("~");
          break;
        case LexicalUnit.SAC_SUB_EXPRESSION:
          sb.append('(').append(LexicalUnitUtil.toString(lu.getParameters())).append(')');
          break;
        default:
          sb.append("unknown");
      }
      lu = lu.getNextLexicalUnit();
    }
    return sb.toString();
  }
}
