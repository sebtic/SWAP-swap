/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr>
 * 
 * This file is part of SHS.
 * 
 * SWAP is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * SWAP P is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with SHS. If not, see <http://www.gnu.org/licenses/>.
 * 
 * $Id: Text.java 125 2012-04-04 14:59:59Z sebtic $
 */
package org.projectsforge.swap.core.mime.html.nodes;

import java.util.regex.Pattern;

/**
 * The text node.
 * 
 * @author Sébastien Aupetit
 */
public class Text extends Node {

  /** The content. */
  private final StringBuilder content = new StringBuilder();

  /** The cached content. */
  private String cachedContent = null;

  /** The clean pattern. */
  private final Pattern cleanPattern = Pattern.compile(" +|\t+|\n+");

  /** The cached cleaned content. */
  private String cachedCleanedContent;

  /**
   * Instantiates a new text.
   */
  public Text() {
  }

  /**
   * Instantiates a new text.
   * 
   * @param content
   *          the content
   */
  public Text(final String content) {
    addContent(content);
  }

  /**
   * Adds the content.
   * 
   * @param content
   *          the content
   */
  public void addContent(final String content) {
    this.content.append(content);
    cachedContent = null;
    cachedCleanedContent = null;
  }

  /**
   * Removes extraneous spaces, tab and newline from the string.
   * 
   * @param content
   *          the content to clean
   * @return the cleaned string
   */
  private String cleanString(String content) {
    boolean again;
    do {
      final String newContent = cleanPattern.matcher(content).replaceAll(" ");
      again = !content.equals(newContent);
      content = newContent;
    } while (again);
    return content;
  }

  /**
   * Get a cleaned content where extraneous spaces, tab and newline have been
   * removed.
   * 
   * @return the cleaned content
   */
  public synchronized String getCleanedContent() {
    if (cachedCleanedContent == null) {
      cachedCleanedContent = cleanString(getContent());
    }
    return cachedCleanedContent;
  }

  /**
   * Gets the content.
   * 
   * @return the content
   */
  public synchronized String getContent() {
    if (cachedContent == null) {
      cachedContent = content.toString();
    }
    return cachedContent;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getContent();
  }

}
