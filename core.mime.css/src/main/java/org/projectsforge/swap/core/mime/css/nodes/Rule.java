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
 * <http://www.gnu.org/licenses/>. $Id: Rule.java 91 2011-07-18 16:28:31Z sebtic
 * $
 */
package org.projectsforge.swap.core.mime.css.nodes;

import org.projectsforge.swap.core.mime.css.LexicalUnitUtil;
import org.projectsforge.utils.icasestring.ICaseString;
import org.w3c.css.sac.LexicalUnit;

/**
 * The class representing a CSS rule.
 * 
 * @author Sébastien Aupetit
 */
public class Rule {

  /** The value. */
  private final LexicalUnit value;

  /** The important flag. */
  private final boolean important;

  /** The name of the property. */
  private final ICaseString name;

  private final String valueAsString;

  /**
   * Instantiates a new rule.
   * 
   * @param name
   *          the name of the property
   * @param value
   *          the value
   * @param important
   *          the important flag
   */
  public Rule(final ICaseString name, final LexicalUnit value, final boolean important) {
    this.name = name;
    this.value = value;
    String vas;
    try {
      vas = LexicalUnitUtil.toString(value);
    } catch (final IllegalStateException ex) {
      vas = ""; // something goes wrong during parsing of the CSS but we can do nothing
    }
    this.valueAsString = vas;
    this.important = important;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Rule)) {
      return false;
    }
    final Rule other = (Rule) obj;
    if (important != other.important) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    if (valueAsString == null) {
      if (other.valueAsString != null) {
        return false;
      }
    } else if (!valueAsString.equals(other.valueAsString)) {
      return false;
    }
    return true;
  }

  /**
   * Gets the property name.
   * 
   * @return the name
   */
  public ICaseString getName() {
    return name;
  }

  /**
   * Gets the value.
   * 
   * @return the value
   */
  public LexicalUnit getValue() {
    return value;
  }

  public String getValueAsString() {
    return valueAsString;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (important ? 1231 : 1237);
    result = prime * result + (name == null ? 0 : name.hashCode());
    result = prime * result + (valueAsString == null ? 0 : valueAsString.hashCode());
    return result;
  }

  /**
   * Checks if is important.
   * 
   * @return true, if is important
   */
  public boolean isImportant() {
    return important;
  }

  @Override
  public String toString() {
    return name + ":" + valueAsString + (important ? "!" : "") + ";";
  }
}
