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
 * <http://www.gnu.org/licenses/>. $Id: CssParser.java 98 2011-11-24 12:10:32Z
 * sebtic $
 */
package org.projectsforge.swap.core.mime.css.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.MissingResourceException;
import org.apache.batik.css.parser.ParseException;
import org.apache.batik.css.parser.Parser;
import org.projectsforge.swap.core.encoding.css.CSSEncodingDetector;
import org.projectsforge.swap.core.mime.css.nodes.Rule;
import org.projectsforge.swap.core.mime.css.nodes.Stylesheet;
import org.projectsforge.swap.core.mime.css.parser.conditions.CssConditionFactory;
import org.projectsforge.swap.core.mime.css.parser.handlers.CssDocumentHandler;
import org.projectsforge.swap.core.mime.css.parser.handlers.CssErrorHandler;
import org.projectsforge.swap.core.mime.css.parser.handlers.StyleTagCssDocumentHandler;
import org.projectsforge.swap.core.mime.css.parser.selectors.CssSelectorFactory;
import org.projectsforge.utils.temporarystreams.ContentHolder;
import org.projectsforge.utils.temporarystreams.ContentInputStream;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;

/**
 * The class to parse a CSS style sheet.
 * 
 * @author Sébastien Aupetit
 */
public class CssParser {

  /** The conditionFactory. */
  private final static CssConditionFactory conditionFactory = new CssConditionFactory();

  /** The selectorFacory. */
  private final static CssSelectorFactory selectorFacory = new CssSelectorFactory();

  /**
   * Parses the property value.
   * 
   * @param value the value
   * @return the lexical unit
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws CSSParsingException the cSS parsing exception
   */
  public static LexicalUnit parsePropertyValue(final String value) throws IOException,
      CSSParsingException {

    if (value != null && !value.trim().isEmpty()) {
      // TODO : URL pas parsé correctement

      try {
        final Parser propertyValueParser = new Parser();
        propertyValueParser.setErrorHandler(new CssErrorHandler("property://" + value));
        return propertyValueParser.parsePropertyValue(value);
      } catch (CSSException | ParseException e) {
        throw new CSSParsingException(e);
      }
    } else {
      throw new CSSParsingException("Can not parse an empty property");
    }
  }

  /**
   * Parses the stylesheet from an input stream.
   * 
   * @param encodingDetector the encoding detector
   * @param in the input stream
   * @param uri the URI of the document
   * @return the stylesheet
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws CSSParsingException the cSS parsing exception
   */
  public static Stylesheet parseStylesheet(final CSSEncodingDetector encodingDetector,
      final ContentHolder contentHolder, final String uri) throws IOException, CSSParsingException {
    try {

      encodingDetector.detectEncoding(contentHolder);

      final CssErrorHandler errorHandler = new CssErrorHandler(uri);

      try {
        final CssDocumentHandler documentHandler = new CssDocumentHandler();
        documentHandler.getCSSStyleSheet().setEncodingDetector(encodingDetector);

        final ContentInputStream in = contentHolder.getInputStream();
        try {
          final InputSource source = new InputSource();
          source.setByteStream(in);
          source.setEncoding(encodingDetector.getEffectiveEncoding());

          final Parser p = new Parser();
          p.setConditionFactory(CssParser.conditionFactory);
          p.setSelectorFactory(CssParser.selectorFacory);
          p.setErrorHandler(errorHandler);
          p.setDocumentHandler(documentHandler);
          p.parseStyleSheet(source);
          return documentHandler.getCSSStyleSheet();
        } finally {
          in.close();
        }
      } catch (final MissingResourceException e) {
        errorHandler.getLogger().warn("Invalid encoding detected ({}). Fallbacking to ISO8859_1.",
            encodingDetector.getEffectiveEncoding());

        final ContentInputStream in = contentHolder.getInputStream();
        try {
          encodingDetector.setAtEncoding("ISO8859_1");
          final CssDocumentHandler documentHandler = new CssDocumentHandler();
          documentHandler.getCSSStyleSheet().setEncodingDetector(encodingDetector);

          final InputSource source = new InputSource();
          source.setByteStream(in);
          source.setEncoding("ISO8859_1");

          final Parser p = new Parser();
          p.setConditionFactory(CssParser.conditionFactory);
          p.setSelectorFactory(CssParser.selectorFacory);
          p.setErrorHandler(errorHandler);
          p.setDocumentHandler(documentHandler);
          p.parseStyleSheet(source);
          return documentHandler.getCSSStyleSheet();
        } finally {
          in.close();
        }
      }
    } catch (CSSException | ParseException e) {
      throw new CSSParsingException(e);
    }

  }

  /**
   * Parses the stylesheet from a string.
   * 
   * @param encodingDetector the encoding detector
   * @param stylesheet the stylesheet
   * @return the stylesheet
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws CSSParsingException the cSS parsing exception
   */
  public static Stylesheet parseStylesheet(final CSSEncodingDetector encodingDetector,
      final String stylesheet) throws IOException, CSSParsingException {
    try {
      final CssDocumentHandler documentHandler = new CssDocumentHandler();
      documentHandler.getCSSStyleSheet().setEncodingDetector(encodingDetector);

      final InputSource source = new InputSource();
      source.setCharacterStream(new StringReader(stylesheet));

      final Parser p = new Parser();
      p.setConditionFactory(CssParser.conditionFactory);
      p.setSelectorFactory(CssParser.selectorFacory);
      p.setErrorHandler(new CssErrorHandler("stylesheet://"
          + stylesheet.substring(0, Math.min(stylesheet.length(), 40))
          + (stylesheet.length() > 40 ? "..." : "")));
      p.setDocumentHandler(documentHandler);
      p.parseStyleSheet(source);
      return documentHandler.getCSSStyleSheet();
    } catch (CSSException | ParseException e) {
      throw new CSSParsingException(e);
    }
  }

  /**
   * Parses the style tag.
   * 
   * @param value the value
   * @return the list
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws CSSParsingException the cSS parsing exception
   */
  public static List<Rule> parseStyleTag(final String value) throws IOException,
      CSSParsingException {
    try {
      final StyleTagCssDocumentHandler documentHandler = new StyleTagCssDocumentHandler();

      final InputSource source = new InputSource();
      source.setCharacterStream(new StringReader(value));

      final Parser p = new Parser();
      p.setConditionFactory(CssParser.conditionFactory);
      p.setSelectorFactory(CssParser.selectorFacory);
      p.setErrorHandler(new CssErrorHandler("styletag://" + value));
      p.setDocumentHandler(documentHandler);
      p.parseStyleDeclaration(source);

      return documentHandler.getRules();
    } catch (CSSException | ParseException e) {
      throw new CSSParsingException(e);
    }
  }
}
