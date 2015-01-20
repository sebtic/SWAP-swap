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
 * <http://www.gnu.org/licenses/>. $Id: ImportNode.java 91 2011-07-18 16:28:31Z
 * sebtic $
 */
package org.projectsforge.swap.core.mime.css.nodes;

import java.util.Set;
import org.projectsforge.utils.icasestring.ICaseString;

/**
 * The class representing {@code @import} CSS node.
 * 
 * @author Sébastien Aupetit
 */
public class ImportNode {

  /** The uri. */
  private final String uri;

  /** The medias. */
  private final Set<ICaseString> medias;

  /**
   * Instantiates a new import node.
   * 
   * @param uri the uri
   * @param medias the medias
   */
  public ImportNode(final String uri, final Set<ICaseString> medias) {
    this.uri = uri;
    this.medias = medias;
  }

  /**
   * Gets the medias.
   * 
   * @return the medias
   */
  public Set<ICaseString> getMedias() {
    return medias;
  }

  /**
   * Gets the uri.
   * 
   * @return the uri
   */
  public String getUri() {
    return uri;
  }

  @Override
  public String toString() {
    boolean first = true;
    final StringBuilder sb = new StringBuilder();
    sb.append("@import url(\"").append(uri).append("\") ");
    for (final ICaseString media : medias) {
      if (!first) {
        sb.append(", ");
      } else {
        first = false;
      }
      sb.append(media);
    }
    sb.append(";");
    return sb.toString();
  }
}
