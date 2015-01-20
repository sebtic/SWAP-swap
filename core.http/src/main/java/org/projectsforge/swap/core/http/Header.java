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
package org.projectsforge.swap.core.http;

/**
 * The Class Header.
 * 
 * @author Sébastien Aupetit
 */
public class Header {

  /** The name. */
  private final String name;

  /** The value. */
  private String value;

  /**
   * Instantiates a new header.
   * 
   * @param name the name
   * @param value the value
   */
  public Header(final String name, final String value) {
    this.name = name;
    this.value = value;
    if (value == null) {
      throw new IllegalArgumentException("Value can not be null");
    }
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
    if (obj instanceof Header) {
      final Header other = (Header) obj;
      return name.equalsIgnoreCase(other.name) && value.equalsIgnoreCase(value);
    }
    return false;
  }

  /**
   * Gets the name.
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the value.
   * 
   * @return the value
   */
  public String getValue() {
    return value;
  }

  /**
   * Gets the date field.
   * 
   * @return the date field
   */
  public long getValueAsDateField() {
    return DateUtil.parse(value);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.toLowerCase().hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  /**
   * Sets the value.
   * 
   * @param value the new value
   */
  public void setValue(final String value) {
    if (value == null) {
      throw new IllegalArgumentException("Value can not be null");
    }
    this.value = value;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return name + " : " + value;
  }
}
