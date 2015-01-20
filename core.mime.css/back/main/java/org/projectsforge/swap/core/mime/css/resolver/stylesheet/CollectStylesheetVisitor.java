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
package org.projectsforge.swap.core.mime.css.resolver.stylesheet;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import org.projectsforge.swap.core.encoding.css.CSSEncodingDetector;
import org.projectsforge.swap.core.http.CacheManager;
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
import org.projectsforge.swap.core.mime.html.nodes.elements.HEADElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.LINKElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.STYLEElement;
import org.projectsforge.utils.icasestring.ICaseString;
import org.projectsforge.utils.tasksexecutor.Task;
import org.projectsforge.utils.tasksexecutor.RecursiveTaskExecutor;
import org.projectsforge.utils.visitor.VisitingMode;
import org.projectsforge.utils.visitor.Visitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: LINK HTTP header is not handled
// (http://www.w3.org/TR/html40/present/styles.html)
// TODO: handle conditional comment
// http://reference.sitepoint.com/css/conditionalcomments
/**
 * A visitor class which collects the stylesheets of a document.
 */
public class CollectStylesheetVisitor extends Visitor<Node, CollectStylesheetVisitor.Context, Void> {

  /**
   * The Class Context.
   */
  public class Context {

    /** The encoding. */
    String encoding;

    /** Are we in head tag ?. */
    boolean inHead;

    /**
     * Instantiates a new context.
     * 
     * @param cumulator the cumulator
     * @param inHead the in head
     */
    public Context(final Context cumulator, final boolean inHead) {
      encoding = cumulator.encoding;
      this.inHead = inHead;
    }

    /**
     * Instantiates a new context.
     * 
     * @param cumulator the cumulator
     * @param encoding the encoding
     */
    public Context(final Context cumulator, final String encoding) {
      this.encoding = encoding;
      this.inHead = cumulator.inHead;
    }

    /**
     * Instantiates a new context.
     * 
     * @param encoding the encoding
     */
    public Context(final String encoding) {
      this.encoding = encoding;
      this.inHead = false;
    }
  }

  /** The logger. */
  private final Logger logger = LoggerFactory.getLogger(CollectStylesheetVisitor.class);

  /** The media. */
  private final ICaseString media;

  /** The base request. */
  private final Request baseRequest;

  /** The cache manager. */
  private final CacheManager cacheManager;

  /** The collected stylesheets. */
  private final List<Stylesheet> collectedStylesheets = Collections
      .synchronizedList(new ArrayList<Stylesheet>());

  /**
   * The constructor.
   * 
   * @param cacheManager the cache manager
   * @param baseRequest the base request
   * @param media the media
   */
  public CollectStylesheetVisitor(final CacheManager cacheManager, final Request baseRequest,
      final ICaseString media) {
    super(VisitingMode.PARALLEL);
    this.media = media;
    this.cacheManager = cacheManager;
    this.baseRequest = baseRequest;
  }

  /**
   * Collect stylesheet.
   * 
   * @param context the context
   * @param stylesheet the stylesheet
   */
  private void collectStylesheet(final Context context, final Stylesheet stylesheet) {
    // handle @import in parallel
    final List<Task> imports = new ArrayList<Task>();
    final Context subContext = new Context(context, stylesheet.getEncodingDetector()
        .getEffectiveEncoding());
    for (final ImportNode importNode : stylesheet.getImportNodes(media)) {
      imports.add(new Task() {
        @Override
        public void run() throws Throwable {
          processImport(importNode, subContext);
        }
      });
    }
    TaskExecutor.execute(imports);

    collectedStylesheets.add(stylesheet);
  }

  /**
   * Gets the collected stylesheets.
   * 
   * @return the collected stylesheets
   */
  public List<Stylesheet> getCollectedStylesheets() {
    return collectedStylesheets;
  }

  /**
   * Process import.
   * 
   * @param importNode the import node
   * @param context the context
   */
  private void processImport(final ImportNode importNode, final Context context) {
    try {
      final Request request = cacheManager.newContextualRequest(baseRequest, importNode.getUri());
      final Response response = request.refreshResponse(cacheManager.getHttpClient());

      if (response.getStatusCode() == 200) {
        try {
          final CSSEncodingDetector detector = new CSSEncodingDetector();
          detector.setHttpHeaderEncoding(response.getHeaders().getHeaderValue(
              ResponseHeaderConstants.CONTENT_ENCODING));
          detector.setParentEncoding(context.encoding);
          collectStylesheet(context,
              CssParser.parseStylesheet(detector, response.getContent(), request.getURI()));
        } catch (final CSSParsingException e) {
          logger.warn("Cannot parse response for {} : {}", request.getURI(), e.getMessage());
        }
      } else {
        logger.warn("Error {} while retrieving {}", response.getStatusCode(), request.getURI());
      }
    } catch (final MalformedURLException e) {
      logger.warn("Can not get resource {} from {}", importNode.getUri(), baseRequest.getURI());
    } catch (final IOException e) {
      logger.warn("Can not get resource " + importNode.getUri() + " from " + baseRequest.getURI(),
          e);
    }
  }

  // convert body tag stylizing attributes into CSS stylesheet
  /**
   * Visit.
   * 
   * @param element the element
   * @param context the context
   */
  public void visit(final BODYElement element, final Context context) {
    final String alinkAttribute = element.getAttributes().get(Attributes.ALINK);
    final String linkAttribute = element.getAttributes().get(Attributes.LINK);
    final String vlinkAttribute = element.getAttributes().get(Attributes.VLINK);

    final StringBuilder sb = new StringBuilder();

    if (alinkAttribute != null) {
      sb.append("a:active { color: ").append(alinkAttribute).append("; }\n");
      element.getAttributes().remove(Attributes.ALINK);
    }
    if (linkAttribute != null) {
      sb.append("a:link { color: ").append(linkAttribute).append("; }\n");
      element.getAttributes().remove(Attributes.LINK);
    }
    if (vlinkAttribute != null) {
      sb.append("a:visited { color: ").append(vlinkAttribute).append("; }\n");
      element.getAttributes().remove(Attributes.VLINK);
    }

    // Warning: BGCOLOR and TEXT attributes must not be converted here for
    // proper handling by RuleSetResolver
    // Warning: STYLE attributes is handled as usual by RuleSetResolver

    if (sb.length() > 0) {
      try {
        final CSSEncodingDetector detector = new CSSEncodingDetector();
        detector.setParentEncoding(context.encoding);
        collectStylesheet(context, CssParser.parseStylesheet(detector, sb.toString()));
      } catch (final CSSParsingException e) {
        logger.warn("Cannot parse content {} : {}", sb, e.getMessage());
      } catch (final RuntimeException e) {
        logger.warn("Can not parse stylesheet", e);
      } catch (final IOException e) {
        logger.warn("Can not parse stylesheet", e);
      }
    }

    // recurse
    recurse(element.getChildren(), context);
  }

  /**
   * Visit.
   * 
   * @param document the document
   */
  public void visit(final Document document) {
    recurse(document.getChildren(), new Context(document.getEncoding()));
  }

  /**
   * Visit.
   * 
   * @param element the element
   * @param context the context
   */
  public void visit(final HEADElement element, final Context context) {
    recurse(element.getChildren(), new Context(context, true));
  }

  // TODO: current implementation only considers persistent and preferred
  // stylesheets (not alternative)
  /**
   * Visit.
   * 
   * @param element the element
   * @param context the context
   */
  public void visit(final LINKElement element, final Context context) {
    String relAttribute = element.getAttributes().get(LINKElement.REL);
    String titleAttribute = element.getAttributes().get(LINKElement.TITLE);
    String hrefAttribute = element.getAttributes().get(LINKElement.HREF);
    String typeAttribute = element.getAttributes().get(LINKElement.TYPE);
    String mediaAttribute = element.getAttributes().get(LINKElement.MEDIA);
    String charsetAttribute = element.getAttributes().get(LINKElement.CHARSET);

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
        return;
      }
    }

    // check media
    if (mediaAttribute != null) {
      boolean found = false;
      final StringTokenizer tokenizer = new StringTokenizer(mediaAttribute, " ,");

      while (!found && tokenizer.hasMoreTokens()) {
        final String token = tokenizer.nextToken();
        found = "all".equalsIgnoreCase(token.trim()) || media.get().equalsIgnoreCase(token.trim());
      }
      if (!found) {
        return;
      }
    }

    // only keep persistent and preferred stylesheets
    final boolean persistent = (titleAttribute == null || titleAttribute.isEmpty())
        && (relAttribute == null);
    final boolean preferred = relAttribute != null && "stylesheet".equalsIgnoreCase(relAttribute);

    if (!preferred && !persistent) {
      return;
    }

    // retrieve stylesheet
    try {
      final Request br = cacheManager.newContextualRequest(baseRequest, hrefAttribute);
      final Response response = br.refreshResponse(cacheManager.getHttpClient());
      if (response.getStatusCode() == 200) {
        final CSSEncodingDetector detector = new CSSEncodingDetector();
        detector.setHttpHeaderEncoding(response.getHeaders().getHeaderValue(
            ResponseHeaderConstants.CONTENT_ENCODING));
        detector.setParentEncoding(context.encoding);
        detector.setLinkEncoding(charsetAttribute);
        try {
          collectStylesheet(context,
              CssParser.parseStylesheet(detector, response.getContent(), br.getURI()));
        } catch (final CSSParsingException e) {
          logger.warn("Cannot parse response for {} : {}", br.getURI(), e.getMessage());
        }
      } else {
        logger.warn("Error {} while retrieving {}", response.getStatusCode(), br.getURI());
      }

    } catch (IOException | RuntimeException e) {
      logger.warn("Can not get resource " + hrefAttribute + " from " + baseRequest.getURI(), e);
    }
  }

  /**
   * Visit.
   * 
   * @param node the node
   * @param context the context
   */
  public void visit(final Node node, final Context context) {
    recurse(node.getChildren(), context);
  }

  /**
   * Visit.
   * 
   * @param element the element
   * @param context the context
   */
  public void visit(final STYLEElement element, final Context context) {
    final String typeAttribute = element.getAttributes().get(STYLEElement.TYPE);
    final String mediaAttribute = element.getAttributes().get(STYLEElement.MEDIA);

    // check we are in head
    if (!context.inHead) {
      return;
    }

    // check type
    if (!"text/css".equalsIgnoreCase(typeAttribute)) {
      return;
    }

    // check media
    if (mediaAttribute != null) {
      boolean found = false;
      final StringTokenizer tokenizer = new StringTokenizer(mediaAttribute, " ,");

      while (!found && tokenizer.hasMoreTokens()) {
        final String token = tokenizer.nextToken();
        found = "all".equalsIgnoreCase(token.trim()) || media.get().equalsIgnoreCase(token.trim());
      }
      if (!found) {
        return;
      }
    }

    // check text sub nodes (using getAllChildren allows to transparently manage
    // hidden content (in comment)
    try {
      final List<Text> texts = new ArrayList<Text>();
      element.getChildrenToLeaves(Text.class, texts);
      for (final Text text : texts) {
        if (!text.getContent().trim().isEmpty()) {
          final CSSEncodingDetector detector = new CSSEncodingDetector();
          detector.setParentEncoding(context.encoding);
          try {
            collectStylesheet(context, CssParser.parseStylesheet(detector, text.getContent()));
          } catch (final CSSParsingException e) {
            logger.warn("Cannot parse content {} : {}", text.getContent(), e.getMessage());
          }
        }
      }
    } catch (final RuntimeException e) {
      logger.debug("<style> content can not be parsed", e);
    } catch (final IOException e) {
      logger.debug("<style> content can not be parsed", e);
    }

  }

}
