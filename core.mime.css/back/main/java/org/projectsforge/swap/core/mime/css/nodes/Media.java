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
 * <http://www.gnu.org/licenses/>. $Id: Media.java 91 2011-07-18 16:28:31Z
 * sebtic $
 */
package org.projectsforge.swap.core.mime.css.nodes;

import java.util.ArrayList;
import java.util.List;
import org.projectsforge.utils.icasestring.ICaseString;

/**
 * The class representing a {@code @media} CSS node
 * 
 * @author Sébastien Aupetit
 */
public final class Media {

  /** The Constant ALL. */
  public final static ICaseString ALL = new ICaseString("all");

  /** The Constant BRAILLE. */
  public final static ICaseString BRAILLE = new ICaseString("braille");

  /** The Constant EMBOSSED. */
  public final static ICaseString EMBOSSED = new ICaseString("embossed");

  /** The Constant HANDLED. */
  public final static ICaseString HANDLED = new ICaseString("handled");

  /** The Constant PRINT. */
  public final static ICaseString PRINT = new ICaseString("print");

  /** The Constant PROJECTION. */
  public final static ICaseString PROJECTION = new ICaseString("projection");

  /** The Constant SCREEN. */
  public final static ICaseString SCREEN = new ICaseString("screen");

  /** The Constant SPEECH. */
  public final static ICaseString SPEECH = new ICaseString("speech");

  /** The Constant TTY. */
  public final static ICaseString TTY = new ICaseString("tty");

  /** The Constant TV. */
  public final static ICaseString TV = new ICaseString("tv");

  /** The Constant DEFAULT. */
  public final static ICaseString DEFAULT = Media.ALL;

  /** The name. */
  private final ICaseString name;

  /** The elements. */
  private final List<Element> elements = new ArrayList<Element>();

  /**
   * Instantiates a new media.
   * 
   * @param name the name
   */
  public Media(final ICaseString name) {
    this.name = name;
  }

  /**
   * Adds an element.
   * 
   * @param element the element
   */
  public void add(final Element element) {
    elements.add(element);
  }

  /**
   * Gets the elements.
   * 
   * @return the elements
   */
  public List<Element> getElements() {
    return elements;
  }

  /**
   * Gets the name.
   * 
   * @return the name
   */
  public ICaseString getName() {
    return name;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("@media ").append(name).append(" { ");
    for (final Element element : elements) {
      sb.append(element).append(" ");
    }
    sb.append("}");
    return sb.toString();
  }
}
