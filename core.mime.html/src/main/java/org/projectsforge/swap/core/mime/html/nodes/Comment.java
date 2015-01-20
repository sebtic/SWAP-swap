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
 * $Id: Comment.java 125 2012-04-04 14:59:59Z sebtic $
 */
package org.projectsforge.swap.core.mime.html.nodes;

/**
 * The comment node.
 * 
 * @author Sébastien Aupetit
 */
public class Comment extends Node {

  /** The comment. */
  private String comment;

  /**
   * Instantiates a new comment.
   */
  public Comment() {
  }

  /**
   * Gets the comment.
   * 
   * @return the comment
   */
  public String getComment() {
    return comment;
  }

  /**
   * Sets the comment.
   * 
   * @param comment
   *          the new comment
   */
  public void setComment(final String comment) {
    this.comment = comment;
  }

}
