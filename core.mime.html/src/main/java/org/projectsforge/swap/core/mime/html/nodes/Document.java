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
 * <http://www.gnu.org/licenses/>. $Id: Document.java 84 2011-06-09 10:09:03Z
 * sebtic $
 */
package org.projectsforge.swap.core.mime.html.nodes;

/**
 * The Document node.
 * 
 * @author Sébastien Aupetit
 */
public class Document extends Node {
  /** The root element. */
  private String rootElement = null;

  /** The public id. */
  private String publicId = null;

  /** The system id. */
  private String systemId = null;

  /** The encoding. */
  private String encoding = null;

  /**
   * Instantiates a new document.
   * 
   * @param encoding the encoding
   */
  public Document(final String encoding) {
    this.encoding = encoding;
  }

  /**
   * Gets the encoding.
   * 
   * @return the encoding
   */
  public String getEncoding() {
    return encoding;
  }

  /**
   * Gets the public id.
   * 
   * @return the public id
   */
  public String getPublicId() {
    return publicId;
  }

  /**
   * Gets the root element.
   * 
   * @return the root element
   */
  public String getRootElement() {
    return rootElement;
  }

  /**
   * Gets the system id.
   * 
   * @return the system id
   */
  public String getSystemId() {
    return systemId;
  }

  /**
   * Sets the encoding.
   * 
   * @param encoding the new encoding
   */
  public void setEncoding(final String encoding) {
    this.encoding = encoding;
  }

  /**
   * Sets the public id.
   * 
   * @param publicId the new public id
   */
  public void setPublicId(final String publicId) {
    this.publicId = publicId;
  }

  /**
   * Sets the root element.
   * 
   * @param rootElement the new root element
   */
  public void setRootElement(final String rootElement) {
    this.rootElement = rootElement;
  }

  /**
   * Sets the system id.
   * 
   * @param systemId the new system id
   */
  public void setSystemId(final String systemId) {
    this.systemId = systemId;
  }
}
