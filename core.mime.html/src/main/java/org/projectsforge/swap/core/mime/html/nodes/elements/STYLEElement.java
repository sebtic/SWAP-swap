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
 * <http://www.gnu.org/licenses/>. $Id: STYLEElement.java 91 2011-07-18
 * 16:28:31Z sebtic $
 */
package org.projectsforge.swap.core.mime.html.nodes.elements;

import org.projectsforge.utils.icasestring.ICaseString;

/**
 * The style tag.
 * 
 * @author Sébastien Aupetit
 */
@HtmlTag
public class STYLEElement extends AbstractElement {

  /** The Constant TYPE. */
  public static final ICaseString TYPE = new ICaseString("type");

  /** The Constant MEDIA. */
  public static final ICaseString MEDIA = new ICaseString("media");

  /** The Constant TAGNAME. */
  public static final ICaseString TAGNAME = new ICaseString("style");

  /**
   * Instantiates a new sTYLE element.
   */
  protected STYLEElement() {
  }
}
