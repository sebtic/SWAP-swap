/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr> This file
 * is part of SHS. SWAP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version. SWAP P is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with SHS. If not, see
 * <http://www.gnu.org/licenses/>. $Id: CssErrorHandler.java 98 2011-11-24
 * 12:10:32Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.parser.handlers;

import org.slf4j.Logger;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.CSSParseException;
import org.w3c.css.sac.ErrorHandler;

/**
 * The error handler for the CSS parser.
 * 
 * @author Sébastien Aupetit
 */
public class CssErrorHandler implements ErrorHandler {

  /** The logger. */
  private final Logger logger;

  /**
   * Instantiates a new css error handler.
   * 
   * @param logger the uri
   */
  public CssErrorHandler(final Logger logger) {
    this.logger = logger;
  }

  /*
   * (non-Javadoc)
   * @see org.w3c.css.sac.ErrorHandler#error(org.w3c.css.sac.CSSParseException)
   */
  @Override
  public void error(final CSSParseException exception) throws CSSException {
    logger.info(
        "Parse error at {}:{} ({})",
        new Object[] { exception.getLineNumber(), exception.getColumnNumber(),
            exception.getMessage() });
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.ErrorHandler#fatalError(org.w3c.css.sac.CSSParseException)
   */
  @Override
  public void fatalError(final CSSParseException exception) throws CSSException {
    logger.error(
        "Parse error at {}:{} ({})",
        new Object[] { exception.getLineNumber(), exception.getColumnNumber(),
            exception.getMessage() });
  }

  /**
   * Gets the logger.
   * 
   * @return the logger
   */
  public Logger getLogger() {
    return logger;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.w3c.css.sac.ErrorHandler#warning(org.w3c.css.sac.CSSParseException)
   */
  @Override
  public void warning(final CSSParseException exception) throws CSSException {
    logger.debug(
        "Parse error at {}:{} ({})",
        new Object[] { exception.getLineNumber(), exception.getColumnNumber(),
            exception.getMessage() });
  }
}
