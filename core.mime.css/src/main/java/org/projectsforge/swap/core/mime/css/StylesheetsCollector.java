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
package org.projectsforge.swap.core.mime.css;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import org.projectsforge.swap.core.encoding.css.CSSEncodingDetector;
import org.projectsforge.swap.core.http.CacheManager;
import org.projectsforge.swap.core.http.CircularRedirectionException;
import org.projectsforge.swap.core.http.Request;
import org.projectsforge.swap.core.http.Response;
import org.projectsforge.swap.core.http.ResponseHeaderConstants;
import org.projectsforge.swap.core.mime.css.nodes.ImportNode;
import org.projectsforge.swap.core.mime.css.nodes.Stylesheet;
import org.projectsforge.swap.core.mime.css.parser.CSSParsingException;
import org.projectsforge.swap.core.mime.css.parser.CssParser;
import org.projectsforge.swap.core.mime.html.nodes.Document;
import org.projectsforge.swap.core.mime.html.nodes.Node;
import org.projectsforge.swap.core.mime.html.nodes.Text;
import org.projectsforge.swap.core.mime.html.nodes.elements.Attributes;
import org.projectsforge.swap.core.mime.html.nodes.elements.BODYElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.LINKElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.STYLEElement;
import org.projectsforge.utils.icasestring.ICaseString;
import org.projectsforge.utils.tasksexecutor.RecursiveTaskExecutorException;
import org.projectsforge.utils.visitor.VisitingMode;
import org.projectsforge.utils.visitor.Visitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// TODO: LINK HTTP header is not handled
// (http://www.w3.org/TR/html40/present/styles.html)
// TODO: handle conditional comment
// http://reference.sitepoint.com/css/conditionalcomments
/**
 * A visitor class which collects the stylesheets of a document.
 * 
 * @author Sébastien Aupetit
 */
@Component
public class StylesheetsCollector {

  /**
   * The Class InternalVisitor.
   * 
   * @author Sébastien Aupetit
   */
  class InternalVisitor extends Visitor<Node, StylesheetState, List<Stylesheet>> {

    /**
     * The constructor.
     */
    public InternalVisitor() {
      super(VisitingMode.PARALLEL);
    }

    /**
     * Import stylesheet.
     * 
     * @param state the state
     * @param uri the uri
     * @return the list
     */
    private List<Stylesheet> importStylesheet(final StylesheetState state, final String uri) {
      if (uri.startsWith("mailto:")) {
        return Collections.emptyList();
      }

      try {
        final Response response = cacheManager.newContextualRequest(state.baseRequest, uri).doRequest(true);

        if (response.getStatusCode() == 200) {
          try {
            final CSSEncodingDetector detector = new CSSEncodingDetector();
            detector.setHttpHeaderEncoding(response.getHeaders()
                .getFirstValue(ResponseHeaderConstants.CONTENT_ENCODING));
            detector.setParentEncoding(state.encoding);
            detector.detectEncoding(response.getContent());

            final Stylesheet stylesheet = CssParser.parseStylesheet(detector, response.getContent(), response
                .getRequest().getURL().toExternalForm());
            final List<Stylesheet> result = new ArrayList<>();
            result.add(stylesheet);
            result.addAll(processStylesheet(new StylesheetState(response.getRequest(), state.media, stylesheet
                .getEncoding().getEncoding()), stylesheet));
            return result;
          } catch (final CSSParsingException e) {
            logger.warn("Cannot parse response for " + response.getRequest().getURL(), e.getMessage());
          }
        } else {
          logger.warn("Error {} while retrieving {}", response.getStatusCode(), response.getRequest().getURL());
        }
      } catch (final RuntimeException | IOException | CircularRedirectionException e) {
        logger.warn("Can not get resource " + uri + " from " + state.baseRequest.getURL(), e);
      }
      return Collections.emptyList();
    }

    /**
     * Process stylesheet.
     * 
     * @param state the state
     * @param stylesheet the stylesheet
     * @return the list
     */
    private List<Stylesheet> processStylesheet(final StylesheetState state, final Stylesheet stylesheet) {
      List<Stylesheet> result = Collections.emptyList();
      for (final ImportNode importNode : stylesheet.getImportNodes(state.media)) {
        if (result.isEmpty()) {
          result = new ArrayList<>();
        }
        result.addAll(importStylesheet(state, importNode.getUri()));
      }
      return result;
    }

    /**
     * Visit which converts body tag stylizing attributes into a CSS stylesheet.
     * 
     * @param element the element
     * @param state the state
     * @return the list
     */
    List<Stylesheet> visit(final BODYElement element, final StylesheetState state) {
      final String alinkAttribute = element.getAttribute(Attributes.ALINK);
      final String linkAttribute = element.getAttribute(Attributes.LINK);
      final String vlinkAttribute = element.getAttribute(Attributes.VLINK);

      final StringBuilder sb = new StringBuilder();

      if (alinkAttribute != null) {
        sb.append("a:active { color: ").append(alinkAttribute).append("; }\n");
        element.removeAttribute(Attributes.ALINK);
      }
      if (linkAttribute != null) {
        sb.append("a:link { color: ").append(linkAttribute).append("; }\n");
        element.removeAttribute(Attributes.LINK);
      }
      if (vlinkAttribute != null) {
        sb.append("a:visited { color: ").append(vlinkAttribute).append("; }\n");
        element.removeAttribute(Attributes.VLINK);
      }

      // Warning: BGCOLOR and TEXT attributes must not be converted here for
      // proper handling by RuleSetResolver
      // Warning: STYLE attributes is handled as usual by RuleSetResolver

      if (sb.length() > 0) {
        try {
          final CSSEncodingDetector detector = new CSSEncodingDetector();
          detector.setParentEncoding(state.encoding);
          // TODO use CssParser.parsePropertyValue if possible
          return Arrays.asList(CssParser.parseStylesheet(detector, sb.toString()));
        } catch (final CSSParsingException | RuntimeException e) {
          logger.warn("Can not parse stylesheet", e);
        }
      }
      return Collections.emptyList();
    }

    // TODO: current implementation only considers persistent and preferred
    // stylesheets (not alternative)
    /**
     * Visit.
     * 
     * @param element the element
     * @param state the state
     * @return the list
     */
    List<Stylesheet> visit(final LINKElement element, final StylesheetState state) {
      String relAttribute = element.getAttribute(LINKElement.REL);
      String titleAttribute = element.getAttribute(LINKElement.TITLE);
      String hrefAttribute = element.getAttribute(LINKElement.HREF);
      String typeAttribute = element.getAttribute(LINKElement.TYPE);
      String mediaAttribute = element.getAttribute(LINKElement.MEDIA);
      String charsetAttribute = element.getAttribute(LINKElement.CHARSET);

      if (relAttribute != null) {
        relAttribute = relAttribute.trim();
      };
      if (titleAttribute != null) {
        titleAttribute = titleAttribute.trim();
      }
      if (hrefAttribute != null) {
        hrefAttribute = hrefAttribute.trim();
      }
      if (typeAttribute != null) {
        typeAttribute = typeAttribute.trim();
      }
      if (mediaAttribute != null) {
        mediaAttribute = mediaAttribute.trim();
      }
      if (charsetAttribute != null) {
        charsetAttribute = charsetAttribute.trim();
      }

      if (typeAttribute != null) {
        if (!"text/css".equalsIgnoreCase(typeAttribute)) {
          return Collections.emptyList();
        }
      }

      // check media
      if (mediaAttribute != null) {
        boolean found = false;
        final StringTokenizer tokenizer = new StringTokenizer(mediaAttribute, " ,");

        while (!found && tokenizer.hasMoreTokens()) {
          final String token = tokenizer.nextToken();
          found = "all".equalsIgnoreCase(token.trim()) || state.media.get().equalsIgnoreCase(token.trim());
        }
        if (!found) {
          return Collections.emptyList();
        }
      }

      // only keep persistent and preferred stylesheets
      final boolean persistent = (titleAttribute == null || titleAttribute.isEmpty()) && relAttribute == null;
      final boolean preferred = relAttribute != null && "stylesheet".equalsIgnoreCase(relAttribute);

      if (!preferred && !persistent) {
        return Collections.emptyList();
      }

      // retrieve stylesheet
      return importStylesheet(state, hrefAttribute);
    }

    /**
     * Visit.
     * 
     * @param node the node
     * @param state the state
     * @return the list
     * @throws RecursiveTaskExecutorException
     */
    List<Stylesheet> visit(final Node node, final StylesheetState state) throws RecursiveTaskExecutorException {
      return StylesheetsCollector.mergeLists(recurse(node.getChildrenCollection(), state));
    }

    /**
     * Visit.
     * 
     * @param element the element
     * @param state the state
     * @return the list
     */
    List<Stylesheet> visit(final STYLEElement element, final StylesheetState state) {
      final String typeAttribute = element.getAttribute(STYLEElement.TYPE);
      final String mediaAttribute = element.getAttribute(STYLEElement.MEDIA);

      // we are in head since sub node of BODY are not visited

      // check type
      if (!"text/css".equalsIgnoreCase(typeAttribute)) {
        return Collections.emptyList();
      }

      // check media
      if (mediaAttribute != null) {
        boolean found = false;
        final StringTokenizer tokenizer = new StringTokenizer(mediaAttribute, " ,");

        while (!found && tokenizer.hasMoreTokens()) {
          final String token = tokenizer.nextToken();
          found = "all".equalsIgnoreCase(token.trim()) || state.media.get().equalsIgnoreCase(token.trim());
        }
        if (!found) {
          return Collections.emptyList();
        }
      }

      // check text sub nodes (using getChildrenToLeaves allows to transparently
      // manage
      // hidden content (in comment)
      try {
        final List<Text> texts = element.getChildrenToLeaves(Text.class);
        for (final Text text : texts) {
          if (!text.getContent().trim().isEmpty()) {
            final CSSEncodingDetector detector = new CSSEncodingDetector();
            detector.setParentEncoding(state.encoding);
            try {
              final List<Stylesheet> result = new ArrayList<>();
              final Stylesheet stylesheet = CssParser.parseStylesheet(detector, text.getContent());
              result.add(stylesheet);
              result.addAll(processStylesheet(state, stylesheet));
              return result;
            } catch (final CSSParsingException e) {
              logger.warn("Cannot parse content {} : {}", text.getContent(), e.getMessage());
            }
          }
        }
      } catch (final RuntimeException e) {
        logger.debug("<style> content can not be parsed", e);
      }
      return Collections.emptyList();
    }
  }

  /**
   * The Class StylesheetState.
   * 
   * @author Sébastien Aupetit
   */
  static class StylesheetState {

    /** The base request. */
    private final Request baseRequest;

    /** The media. */
    private final ICaseString media;

    /** The encoding. */
    private final String encoding;

    /**
     * Instantiates a new stylesheet state.
     * 
     * @param baseRequest the base request
     * @param media the media
     * @param encoding the encoding
     */
    public StylesheetState(final Request baseRequest, final ICaseString media, final String encoding) {
      this.baseRequest = baseRequest;
      this.media = media;
      this.encoding = encoding;
    }
  }

  /**
   * Merge lists.
   * 
   * @param stylesheets the stylesheets
   * @return the list
   */
  private static List<Stylesheet> mergeLists(final List<Stylesheet>[] stylesheets) {
    List<Stylesheet> result = Collections.emptyList();
    for (final List<Stylesheet> sub : stylesheets) {
      if (!sub.isEmpty()) {
        if (result.isEmpty()) {
          result = new ArrayList<>();
          result.addAll(sub);
        }
      }
    }
    return result;
  }

  /** The logger. */
  private final Logger logger = LoggerFactory.getLogger(StylesheetsCollector.class);

  /** The cache manager. */
  @Autowired
  private CacheManager cacheManager;

  /** The internal visitor. */
  private final InternalVisitor internalVisitor = new InternalVisitor();

  /**
   * Gets the stylesheets.
   * 
   * @param document the document
   * @param baseRequest the base request
   * @param media the media
   * @return the stylesheets
   * @throws RecursiveTaskExecutorException
   */
  public List<Stylesheet> getStylesheets(final Document document, final Request baseRequest, final ICaseString media)
      throws RecursiveTaskExecutorException {
    return StylesheetsCollector.mergeLists(internalVisitor.recurse(document.getChildrenCollection(),
        new StylesheetState(baseRequest, media, document.getEncoding())));
  }
}
