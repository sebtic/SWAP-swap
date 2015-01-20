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
 * <http://www.gnu.org/licenses/>. $Id$
 */
package org.projectsforge.swap.core.mime.css.parser;

/**
 * The exception raised when an error occurred while parsing CSS styles.
 * 
 * @author Sébastien Aupetit
 */
public class CSSParsingException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new cSS parsing exception.
   */
  public CSSParsingException() {
  }

  /**
   * Instantiates a new cSS parsing exception.
   * 
   * @param message the message
   */
  public CSSParsingException(final String message) {
    super(message);
  }

  /**
   * Instantiates a new cSS parsing exception.
   * 
   * @param message the message
   * @param cause the cause
   */
  public CSSParsingException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Instantiates a new cSS parsing exception.
   * 
   * @param message the message
   * @param cause the cause
   * @param enableSuppression the enable suppression
   * @param writableStackTrace the writable stack trace
   */
  public CSSParsingException(final String message, final Throwable cause,
      final boolean enableSuppression, final boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  /**
   * Instantiates a new cSS parsing exception.
   * 
   * @param cause the cause
   */
  public CSSParsingException(final Throwable cause) {
    super(cause);
  }

}
