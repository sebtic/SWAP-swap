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
 * <http://www.gnu.org/licenses/>. $Id: Specificity.java 83 2011-06-08 15:37:32Z
 * sebtic $
 */
package org.projectsforge.swap.core.mime.css.nodes;

/**
 * The class used to compute specificity of a selector.
 * 
 * @author Sébastien Aupetit
 */
public final class Specificity {

  /** The is style element. */
  private boolean isStyleElement = false;

  /** The id attributes counter. */
  private int idAttributesCounter = 0;

  /** The other attributes and pseudo classes counter. */
  private int otherAttributesAndPseudoClassesCounter = 0;

  /** The element names and pseudo elementsToRoot counter. */
  private int elementNamesAndPseudoElementsCounter = 0;

  /**
   * Instantiates a new specificity.
   */
  public Specificity() {
  }

  /**
   * Adds the an element name or a pseudo elementsToRoot.
   */
  public void addAnElementNameOrAPseudoElements() {
    elementNamesAndPseudoElementsCounter++;
  }

  /**
   * Adds the an id attribute.
   */
  public void addAnIdAttribute() {
    idAttributesCounter++;
  }

  /**
   * Adds the an other attribute or a pseudo class.
   */
  public void addAnOtherAttributeOrAPseudoClass() {
    otherAttributesAndPseudoClassesCounter++;
  }

  /**
   * Compare two specificities.
   * 
   * @param second the second
   * @return -1 if this is more specific, 0 if equal, 1 if second is more
   *         specific
   */
  public int compare(final Specificity second) {
    if (isStyleElement && !second.isStyleElement) {
      return -1;
    } else if (!isStyleElement && second.isStyleElement) {
      return 1;
    } else { // same isStyleElement
      if (idAttributesCounter > second.idAttributesCounter) {
        return -1;
      } else if (idAttributesCounter < second.idAttributesCounter) {
        return 1;
      } else { // same idAttributesCounter
        if (otherAttributesAndPseudoClassesCounter > second.otherAttributesAndPseudoClassesCounter) {
          return -1;
        } else if (otherAttributesAndPseudoClassesCounter < second.otherAttributesAndPseudoClassesCounter) {
          return 1;
        } else { // same otherAttributesAndPseudoClassesCounter
          if (elementNamesAndPseudoElementsCounter > second.elementNamesAndPseudoElementsCounter) {
            return -1;
          } else if (elementNamesAndPseudoElementsCounter < second.elementNamesAndPseudoElementsCounter) {
            return 1;
          } else {
            return 0;
          }
        }
      }
    }
  }

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
    final Specificity other = (Specificity) obj;
    if (elementNamesAndPseudoElementsCounter != other.elementNamesAndPseudoElementsCounter) {
      return false;
    }
    if (idAttributesCounter != other.idAttributesCounter) {
      return false;
    }
    if (isStyleElement != other.isStyleElement) {
      return false;
    }
    if (otherAttributesAndPseudoClassesCounter != other.otherAttributesAndPseudoClassesCounter) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + elementNamesAndPseudoElementsCounter;
    result = prime * result + idAttributesCounter;
    result = prime * result + (isStyleElement ? 1231 : 1237);
    result = prime * result + otherAttributesAndPseudoClassesCounter;
    return result;
  }

  /**
   * Sets the style element.
   * 
   * @param isStyleElement the new style element
   */
  public void setStyleElement(final boolean isStyleElement) {
    this.isStyleElement = isStyleElement;
  }

  @Override
  public String toString() {
    return "Specificity [isStyleElement=" + isStyleElement + ", idAttributesCounter="
        + idAttributesCounter + ", otherAttributesAndPseudoClassesCounter="
        + otherAttributesAndPseudoClassesCounter + ", elementNamesAndPseudoElementsCounter="
        + elementNamesAndPseudoElementsCounter + "]";
  }

}
