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
package org.projectsforge.swap.handlers.html;

import org.projectsforge.swap.core.encoding.html.HTMLEncodingDetector;
import org.projectsforge.swap.core.environment.Environment;
import org.projectsforge.swap.core.handlers.Handler;
import org.projectsforge.swap.core.handlers.HandlerContext;
import org.projectsforge.swap.core.handlers.Resource;
import org.projectsforge.swap.core.http.Response;
import org.projectsforge.swap.handlers.mime.StatisticsCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The Class HtmlEncodingDetectorTransformation.
 * 
 * @author Sébastien Aupetit
 */
@Handler(singleton = true)
public class HtmlEncodingDetectorTransformation implements HtmlTransformation {

  /** The resource name of the detected encoding. */
  public static final String HTML_ENCODING = "html.encoding";

  /** The logger. */
  private static final Logger logger = LoggerFactory.getLogger(HtmlEncodingDetectorTransformation.class);

  /** The Constant detector. */
  private static final HTMLEncodingDetector detector = new HTMLEncodingDetector();

  /** The environment. */
  @Autowired
  private Environment environment;

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.handlers.html.HtmlTransformation#transform(org.
   * projectsforge.swap.core.handlers.HandlerContext,
   * org.projectsforge.swap.core.http.Response,
   * org.projectsforge.swap.core.timelimitedoperations.ProbeManager)
   */
  @Override
  public boolean transform(final HandlerContext<HtmlTransformation> context,
      final StatisticsCollector statisticsCollector, final Response response) throws Exception {

    final String encoding = HtmlEncodingDetectorTransformation.detector.detectEncoding(response,
        HtmlHandlersPropertyHolder.statisticalDetectionFirst.get());
    if (encoding != null) {
      HtmlEncodingDetectorTransformation.logger.info("Detected response encoding: {}", encoding);
    } else {
      HtmlEncodingDetectorTransformation.logger.info("Can not detect response encoding");
    }
    context.addResource(new Resource<String>(HtmlEncodingDetectorTransformation.HTML_ENCODING, encoding));
    return true;
  }
}
