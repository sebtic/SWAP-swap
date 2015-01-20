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
 * <http://www.gnu.org/licenses/>. $Id: CssLangCondition.java 84 2011-06-09
 * 10:09:03Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.parser.conditions;

import org.projectsforge.swap.core.mime.css.nodes.Specificity;
import org.projectsforge.utils.icasestring.ICaseString;
import org.w3c.css.sac.Condition;
import org.w3c.css.sac.LangCondition;

/**
 * The Class CssLangCondition.
 */
public class CssLangCondition implements LangCondition, CssCondition {

  /** The lang. */
  private final ICaseString lang;

  /** The lang hyphen. */
  private final ICaseString langHyphen;

  /**
   * Instantiates a new css lang condition.
   * 
   * @param lang the lang
   */
  public CssLangCondition(final ICaseString lang) {
    this.lang = lang;
    this.langHyphen = new ICaseString(lang.get() + '-');
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final CssLangCondition other = (CssLangCondition) obj;
    if (lang == null) {
      if (other.lang != null) {
        return false;
      }
    } else if (!lang.equals(other.lang)) {
      return false;
    }
    if (langHyphen == null) {
      if (other.langHyphen != null) {
        return false;
      }
    } else if (!langHyphen.equals(other.langHyphen)) {
      return false;
    }
    return true;
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.Condition#getConditionType()
   */
  @Override
  public short getConditionType() {
    return Condition.SAC_LANG_CONDITION;
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.LangCondition#getLang()
   */
  @Override
  @Deprecated
  public String getLang() {
    return lang.getLowerCasedValue();
  }

  /**
   * Gets the lang hyphen.
   * 
   * @return the lang hyphen
   */
  public ICaseString getLangHyphen() {
    return langHyphen;
  }

  /**
   * Gets the lang name.
   * 
   * @return the lang name
   */
  public ICaseString getLangName() {
    return lang;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((lang == null) ? 0 : lang.hashCode());
    result = prime * result + ((langHyphen == null) ? 0 : langHyphen.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return ":lang(" + lang + ')';
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.transformations.css.parser.conditions.CssCondition
   * #updateSpecificity
   * (org.projectsforge.swap.transformations.css.nodes.Specificity)
   */
  @Override
  public void updateSpecificity(final Specificity specificity) {
    specificity.addAnElementNameOrAPseudoElements();
  }
}
