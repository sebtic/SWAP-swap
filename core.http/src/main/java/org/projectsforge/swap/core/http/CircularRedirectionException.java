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
 * The Class CircularRedirectionException.
 * 
 * @author Sébastien Aupetit
 */
public class CircularRedirectionException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new circular redirection exception.
   */
  public CircularRedirectionException() {
    super();
  }

  /**
   * Instantiates a new circular redirection exception.
   * 
   * @param message the message
   */
  public CircularRedirectionException(final String message) {
    super(message);
  }

  /**
   * Instantiates a new circular redirection exception.
   * 
   * @param message the message
   * @param cause the cause
   */
  public CircularRedirectionException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Instantiates a new circular redirection exception.
   * 
   * @param cause the cause
   */
  public CircularRedirectionException(final Throwable cause) {
    super(cause);
  }

}
