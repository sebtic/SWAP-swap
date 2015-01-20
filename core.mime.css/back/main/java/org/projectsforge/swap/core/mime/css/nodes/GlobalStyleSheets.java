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
 * <http://www.gnu.org/licenses/>. $Id: StyleSheetFactory.java 98 2011-11-24
 * 12:10:32Z sebtic $
 */
package org.projectsforge.swap.core.mime.css.nodes;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import javax.annotation.PostConstruct;
import org.projectsforge.swap.core.encoding.css.CSSEncodingDetector;
import org.projectsforge.swap.core.mime.css.parser.CSSParsingException;
import org.projectsforge.swap.core.mime.css.parser.CssParser;
import org.projectsforge.utils.path.JRegExPathMatcher;
import org.projectsforge.utils.path.PathMatcherCollection;
import org.projectsforge.utils.path.URISearcher;
import org.projectsforge.utils.temporarystreams.URLBasedContentHolder;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * A factory for accessing default StyleSheet instances.
 * 
 * @author Sébastien Aupetit
 */
@Component
public final class GlobalStyleSheets {

  /** The Constant logger. */
  private final Logger logger = org.slf4j.LoggerFactory.getLogger(GlobalStyleSheets.class);

  /** The user agent stylesheet. */
  private Stylesheet userAgentStylesheet;

  /** The user stylesheet. */
  private Stylesheet userStylesheet;

  @Autowired
  private URISearcher uriSearcher;

  /**
   * Gets the user agent stylesheet.
   * 
   * @return the user agent stylesheet
   */
  public Stylesheet getUserAgentStylesheet() {
    return userAgentStylesheet;
  }

  /**
   * Gets the user stylesheet.
   * 
   * @return the user stylesheet
   */
  public Stylesheet getUserStylesheet() {
    return userStylesheet;
  }

  @PostConstruct
  void init() {
    final JRegExPathMatcher userAgentMatcher = new JRegExPathMatcher(new jregex.Pattern(
        ".*stylesheets/defaultuseragent\\.css"));
    final JRegExPathMatcher userMatcher = new JRegExPathMatcher(new jregex.Pattern(
        ".*stylesheets/defaultuser\\.css"));
    uriSearcher.search(new PathMatcherCollection(userAgentMatcher, userMatcher));
    userAgentStylesheet = loadStylesheet(userAgentMatcher.getMatchedPaths());
    userStylesheet = loadStylesheet(userMatcher.getMatchedPaths());
  }

  /**
   * Load stylesheet.
   * 
   * @param collection the paths
   * @return the stylesheet
   */
  private Stylesheet loadStylesheet(final Collection<URI> collection) {
    if (collection.size() != 1) {
      logger.warn("Multiple default stylesheet found: {}", collection);
    }

    for (final URI uri : collection) {
      return loadStylesheet(uri);
    }
    throw new IllegalArgumentException("Stylesheet not found from " + collection);
  }

  /**
   * Load stylesheet.
   * 
   * @param uri the uri
   * @return the stylesheet
   */
  private Stylesheet loadStylesheet(final URI uri) {
    try {
      final URLBasedContentHolder holder = new URLBasedContentHolder(uri.toURL());
      try {
        // we assume here an UTF-8 encoding by default
        final CSSEncodingDetector detector = new CSSEncodingDetector();
        detector.setParentEncoding("UTF-8");
        return CssParser.parseStylesheet(detector, holder, uri.toURL().toString());
      } finally {
        holder.close();
      }
    } catch (final CSSParsingException | IOException | RuntimeException e) {
      logger.error("Error parsing default CSS stylesheet", e);
      throw new IllegalArgumentException("Stylesheet " + uri + " can not be parsed");
    }
  }
}
