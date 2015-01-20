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
 * <http://www.gnu.org/licenses/>. $Id: Stylesheet.java 91 2011-07-18 16:28:31Z
 * sebtic $
 */
package org.projectsforge.swap.core.mime.css.nodes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.projectsforge.swap.core.encoding.css.CSSEncodingDetector;
import org.projectsforge.utils.icasestring.ICaseString;
import org.projectsforge.utils.icasestring.ICaseStringKeyCollections;

/**
 * The class representing a stylesheet.
 * 
 * @author Sébastien Aupetit
 */
public final class Stylesheet {

  /** The medias. */
  private final Map<ICaseString, Media> medias = ICaseStringKeyCollections
      .caseInsensitiveMap(new HashMap<ICaseString, Media>());

  /** The import nodes. */
  List<ImportNode> importNodes = new ArrayList<ImportNode>();

  /** The encoding. */
  private CSSEncodingDetector encodingDetector;

  /**
   * Adds an import node.
   * 
   * @param importNode the import node
   */
  public void addImport(final ImportNode importNode) {
    importNodes.add(importNode);
  }

  /**
   * Gets the encoding detector.
   * 
   * @return the encoding detector
   */
  public CSSEncodingDetector getEncodingDetector() {
    return encodingDetector;
  }

  /**
   * Gets the import nodes valid for the given media.
   * 
   * @param mediaName the media name
   * @return the import nodes
   */
  public List<ImportNode> getImportNodes(final ICaseString mediaName) {
    final ArrayList<ImportNode> nodes = new ArrayList<ImportNode>();
    for (final ImportNode importNode : importNodes) {
      if (mediaName.equals(Media.ALL)) {
        nodes.add(importNode);
      } else {
        if (importNode.getMedias().contains(mediaName)
            || importNode.getMedias().contains(Media.ALL)) {
          nodes.add(importNode);
        }
      }
    }
    return nodes;
  }

  /**
   * Gets the media node associated with the given name.
   * 
   * @param name the name
   * @return the media
   */
  public Media getMedia(final ICaseString name) {
    Media media = medias.get(name);
    if (media == null) {
      media = new Media(name);
      medias.put(name, media);
    }
    return media;
  }

  /**
   * Gets the medias.
   * 
   * @return the medias
   */
  public Map<ICaseString, Media> getMedias() {
    return medias;
  }

  /**
   * Sets the encoding detector.
   * 
   * @param encodingDetector the new encoding
   */
  public void setEncodingDetector(final CSSEncodingDetector encodingDetector) {
    this.encodingDetector = encodingDetector;
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("@charset \"").append(encodingDetector.getEffectiveEncoding()).append("\";\n");
    for (final ImportNode importNode : importNodes) {
      sb.append(importNode).append("\n");
    }
    for (final Media media : medias.values()) {
      sb.append(media).append("\n");
    }
    return sb.toString();
  }
}
