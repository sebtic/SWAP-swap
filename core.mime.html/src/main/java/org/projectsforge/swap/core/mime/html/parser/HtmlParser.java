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
 * <http://www.gnu.org/licenses/>. $Id: HtmlParser.java 125 2012-04-04 14:59:59Z
 * sebtic $
 */
package org.projectsforge.swap.core.mime.html.parser;

import java.io.IOException;
import java.io.InputStream;
import org.projectsforge.swap.core.environment.Environment;
import org.projectsforge.swap.core.mime.html.HtmlPropertyHolder;
import org.projectsforge.swap.core.mime.html.nodes.Document;
import org.projectsforge.swap.core.mime.html.nodes.elements.ElementFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The class used to parse HTML document using Tagsoup or NekoHtml.
 * 
 * @author Sébastien Aupetit
 */
public class HtmlParser {

  /** The environment. */
  @Autowired
  private Environment environment;

  /** The element factory. */
  @Autowired
  private ElementFactory elementFactory;

  /**
   * Instantiates a new html parser.
   */
  public HtmlParser() {
  }

  /**
   * Parses the content of a HTML document.
   * 
   * @param encoding the encoding
   * @param in the in
   * @throws SAXException the sAX exception
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public Document parse(final String encoding, final InputStream in) throws SAXException,
      IOException {  
    
    DOMHandler domHandler = new DOMHandler(encoding, elementFactory);

    final InputSource inputSource = new org.xml.sax.InputSource(in);
    inputSource.setEncoding(encoding);

    switch (HtmlPropertyHolder.htmlParser.get()) {
      case JSOUP:
        JSoupParser jSoupParser = new JSoupParser(domHandler);
        jSoupParser.parse(in, encoding);
        break;
      case TAGSOUP:
        final TagsoupHTMLParser tagsoupParser = new TagsoupHTMLParser(domHandler);
        tagsoupParser.parse(inputSource);
        break;
      case NEKOHTML:
        final NekoHTMLSAXParser nakoParser = new NekoHTMLSAXParser(domHandler);
        nakoParser.setFeature(
            "http://cyberneko.org/html/features/scanner/ignore-specified-charset", true);
        nakoParser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
        nakoParser.setFeature("http://cyberneko.org/html/features/balance-tags", true);
        nakoParser.setFeature("http://cyberneko.org/html/features/parse-noscript-content", false);
        nakoParser.setProperty("http://cyberneko.org/html/properties/default-encoding", encoding);
        // parser.setProperty("http://cyberneko.org/html/properties/filters",
        // new XMLDocumentFilter[] { new Writer() });
        nakoParser.parse(inputSource);
        break;
      default:
        throw new IllegalArgumentException("Unknown parser type");
    }
    return domHandler.getRootNode();
  }
}
