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
 * <http://www.gnu.org/licenses/>. $Id: AbstractCssAttributeCondition.java 84
 * 2011-06-09 10:09:03Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.parser.conditions;

import org.projectsforge.utils.icasestring.ICaseString;
import org.w3c.css.sac.AttributeCondition;

/**
 * The Class AbstractCssAttributeCondition.
 * 
 * @author Sébastien Aupetit
 */
public abstract class AbstractCssAttributeCondition implements AttributeCondition, CssCondition {

  /** The value. */
  private final String value;

  /** The local name. */
  private final ICaseString localName;

  /**
   * Instantiates a new abstract css attribute condition.
   * 
   * @param localName the local name
   * @param value the value
   */
  public AbstractCssAttributeCondition(final ICaseString localName, final String value) {
    this.localName = localName;
    this.value = value;
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
    final AbstractCssAttributeCondition other = (AbstractCssAttributeCondition) obj;
    if (localName == null) {
      if (other.localName != null) {
        return false;
      }
    } else if (!localName.equals(other.localName)) {
      return false;
    }
    if (value == null) {
      if (other.value != null) {
        return false;
      }
    } else if (!value.equals(other.value)) {
      return false;
    }
    return true;
  }

  /**
   * Gets the attribute name.
   * 
   * @return the attribute name
   */
  public ICaseString getAttributeName() {
    return localName;
  }

  /**
   * Gets the attribute value.
   * 
   * @return the attribute value
   */
  public String getAttributeValue() {
    return value;
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.AttributeCondition#getLocalName()
   * @see AbstractCssAttributeCondition#getLocalNameCaseInsensitive()
   */
  @Override
  @Deprecated
  public String getLocalName() {
    return localName.get();
  }

  /**
   * Gets the local name (case insensitive).
   * 
   * @return the local name (case insensitive)
   */
  public ICaseString getLocalNameCaseInsensitive() {
    return localName;
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.AttributeCondition#getNamespaceURI()
   */
  @Override
  public String getNamespaceURI() {
    return null;
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.AttributeCondition#getSpecified()
   */
  @Override
  public boolean getSpecified() {
    return true;
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.AttributeCondition#getValue()
   */
  @Override
  public String getValue() {
    return value;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((localName == null) ? 0 : localName.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }
}
