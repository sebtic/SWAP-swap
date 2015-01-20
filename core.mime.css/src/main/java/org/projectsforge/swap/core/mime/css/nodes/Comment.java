/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr>
 * 
 * This file is part of SWAP.
 * 
 * SWAP is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * SWAP is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with SWAP. If not, see <http://www.gnu.org/licenses/>.
 * 
 * $Id: Comment.java 91 2011-07-18 16:28:31Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.nodes;

/**
 * The class that store a comment.
 */
public final class Comment implements CssElement {

  /** The comment. */
  private final String comment;

  /**
   * Instantiates a new comment.
   * 
   * @param comment
   *          the comment
   */
  public Comment(final String comment) {
    this.comment = comment;
  }

  /**
   * Gets the value.
   * 
   * @return the value
   */
  public String getValue() {
    return comment;
  }

  @Override
  public String toString() {
    return String.format("/* %s */", comment);
  }

}
