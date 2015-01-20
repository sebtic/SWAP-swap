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
package org.projectsforge.swap.handlers.image;

import org.projectsforge.swap.core.handlers.Handler;
import org.projectsforge.swap.core.handlers.HandlerContext;
import org.projectsforge.swap.core.http.Mime;
import org.projectsforge.swap.core.http.Response;
import org.projectsforge.swap.handlers.mime.MimeHandler;
import org.projectsforge.swap.handlers.mime.StatisticsCollector;

/**
 * The image Mimehandler.
 */
@Handler(singleton = true)
@Mime(mime = { "image/gif", "image/jpeg", "image/png" })
public class ImageMimehandler implements MimeHandler {

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.handlers.MimeHandler#handle(org.projectsforge
   * .swap.core.handlers.Context, org.projectsforge.swap.core.http.Response)
   */
  @Override
  public void handle(final HandlerContext<MimeHandler> context,
      final StatisticsCollector statisticsCollector, final Response response) throws Exception {
    // nothing to do for now
  }

}
