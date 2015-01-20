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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.MissingResourceException;
import javax.annotation.Nullable;
import org.apache.batik.css.parser.ParseException;
import org.apache.batik.css.parser.Parser;
import org.projectsforge.swap.core.encoding.css.CSSEncoding;
import org.projectsforge.swap.core.encoding.css.CSSEncodingDetector;
import org.projectsforge.swap.core.mime.css.nodes.Rule;
import org.projectsforge.swap.core.mime.css.nodes.Stylesheet;
import org.projectsforge.swap.core.mime.css.parser.conditions.CssConditionFactory;
import org.projectsforge.swap.core.mime.css.parser.handlers.CssDocumentHandler;
import org.projectsforge.swap.core.mime.css.parser.handlers.CssErrorHandler;
import org.projectsforge.swap.core.mime.css.parser.handlers.StyleTagCssDocumentHandler;
import org.projectsforge.swap.core.mime.css.parser.selectors.CssSelectorFactory;
import org.projectsforge.utils.temporarystreams.ContentHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.InputSource;
import org.w3c.css.sac.LexicalUnit;
import com.phloc.commons.io.IInputStreamProvider;
import com.phloc.css.ECSSVersion;
import com.phloc.css.decl.CascadingStyleSheet;
import com.phloc.css.decl.visit.CSSVisitor;
import com.phloc.css.parser.TokenMgrError;
import com.phloc.css.reader.CSSReader;
import com.phloc.css.reader.errorhandler.LoggingCSSParseErrorHandler;
import com.phloc.css.writer.CSSWriter;
import com.phloc.css.writer.CSSWriterSettings;

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

  private final static StylesheetCleaner stylesheetCleaner = new StylesheetCleaner();

  /**
   * Parses the property value.
   * 
   * @param value the value
   * @return the lexical unit
   * @throws CSSParsingException the cSS parsing exception
   */
  public static LexicalUnit parsePropertyValue(final String value) throws CSSParsingException {

    if (value != null) {
      // TODO : URL pas parsé correctement

      final Logger logger = LoggerFactory.getLogger(CssParser.class.getCanonicalName() + "." + "property://" + value);

      try {
        final Parser propertyValueParser = new Parser();
        propertyValueParser.setErrorHandler(new CssErrorHandler(logger));
        return propertyValueParser.parsePropertyValue(value);
      } catch (CSSException | ParseException | IOException e) {
        throw new CSSParsingException(e);
      }
    } else {
      throw new CSSParsingException("Can not parse an empty property");
    }
  }

  /**
   * Parses the style tag.
   * 
   * @param value the value
   * @return the list
   * @throws CSSParsingException the cSS parsing exception
   */
  public static List<Rule> parseStyleAttribute(final String value) throws CSSParsingException {

    final Logger logger = LoggerFactory.getLogger(CssParser.class.getCanonicalName() + "." + "styletag://" + value);

    try {
      final StyleTagCssDocumentHandler documentHandler = new StyleTagCssDocumentHandler();

      final InputSource source = new InputSource();
      source.setCharacterStream(new StringReader(value));

      final Parser p = new Parser();
      p.setConditionFactory(CssParser.conditionFactory);
      p.setSelectorFactory(CssParser.selectorFacory);
      p.setErrorHandler(new CssErrorHandler(logger));
      p.setDocumentHandler(documentHandler);
      p.parseStyleDeclaration(source);

      return documentHandler.getRules();
    } catch (CSSException | ParseException | IOException e) {
      throw new CSSParsingException(e);
    }
  }

  private static Stylesheet parseStylesheet(final ContentHolder contentHolder, final CSSEncoding encoding,
      final Logger logger) throws IOException {

    InputSource input;
    try {
      // Use com.phloc.css CSS reader to eventually correct the CSS then
      // batik-css
      // to analyse it
      // 1 - Read CSS with eventually some errors
      final CascadingStyleSheet css = CSSReader.readFromStream(new IInputStreamProvider() {
        @Override
        @Nullable
        public InputStream getInputStream() {
          try {
            return contentHolder.getInputStream();
          } catch (final IOException e) {
            return null;
          }
        }
      }, Charset.forName(encoding.getEncoding()), ECSSVersion.CSS21, new LoggingCSSParseErrorHandler());

      CSSVisitor.visitCSS(css, CssParser.stylesheetCleaner);

      // 2 - Write the CSS
      final CSSWriterSettings settings = new CSSWriterSettings(ECSSVersion.CSS30, false);
      final CSSWriter writer = new CSSWriter(settings);
      final String formattedCss = writer.getCSSAsString(css);
      input = new InputSource(new StringReader(formattedCss));
      input.setEncoding(encoding.getEncoding());
    } catch (final Exception | TokenMgrError ex) {
      input = new InputSource(new InputStreamReader(contentHolder.getInputStream()));
      input.setEncoding(encoding.getEncoding());
    }

    final CssDocumentHandler documentHandler = new CssDocumentHandler();
    documentHandler.getCSSStyleSheet().setEncoding(encoding);

    final Parser p = new Parser();
    p.setConditionFactory(CssParser.conditionFactory);
    p.setSelectorFactory(CssParser.selectorFacory);
    p.setErrorHandler(new CssErrorHandler(logger));
    p.setDocumentHandler(documentHandler);
    p.parseStyleSheet(input);
    return documentHandler.getCSSStyleSheet();
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
  public static Stylesheet parseStylesheet(final CSSEncoding encoding, final String stylesheet)
      throws CSSParsingException {

    final Logger logger = LoggerFactory.getLogger(CssParser.class.getCanonicalName() + "." + "stylesheet://"
        + stylesheet.substring(0, Math.min(stylesheet.length(), 40)) + (stylesheet.length() > 40 ? "..." : ""));

    try {
      String formattedCss;
      try {
        // Use com.phloc.css CSS reader to eventually correct the CSS
        final CascadingStyleSheet css = CSSReader.readFromString(stylesheet, Charset.forName(encoding.getEncoding()),
            ECSSVersion.CSS30, new LoggingCSSParseErrorHandler());
        final CSSWriterSettings settings = new CSSWriterSettings(ECSSVersion.CSS30, false);
        final CSSWriter writer = new CSSWriter(settings);
        formattedCss = writer.getCSSAsString(css);
      } catch (final TokenMgrError | Exception ex) {
        formattedCss = stylesheet;
      }

      // batik-css analysis
      final CssDocumentHandler documentHandler = new CssDocumentHandler();
      documentHandler.getCSSStyleSheet().setEncoding(encoding);

      final InputSource source = new InputSource();
      source.setCharacterStream(new StringReader(formattedCss));

      final Parser p = new Parser();
      p.setConditionFactory(CssParser.conditionFactory);
      p.setSelectorFactory(CssParser.selectorFacory);
      p.setErrorHandler(new CssErrorHandler(logger));
      p.setDocumentHandler(documentHandler);
      p.parseStyleSheet(source);
      return documentHandler.getCSSStyleSheet();

    } catch (CSSException | ParseException | IOException e) {
      throw new CSSParsingException(e);
    }
  }

  /**
   * Parses the stylesheet from an input stream.
   * 
   * @param encodingDetector the encoding detector
   * @param contentHolder the content holder
   * @param uri the URI of the document
   * @return the stylesheet
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws CSSParsingException the cSS parsing exception
   */
  public static Stylesheet parseStylesheet(final CSSEncodingDetector encodingDetector,
      final ContentHolder contentHolder, final String uri) throws CSSParsingException {

    try {
      final Logger logger = LoggerFactory.getLogger(CssParser.class.getCanonicalName() + "." + uri);

      try {
        return CssParser.parseStylesheet(contentHolder, encodingDetector, logger);
      } catch (final MissingResourceException e) {
        if (logger.isWarnEnabled()) {
          logger.warn("Invalid encoding detected ({}). Fallbacking to ISO8859_1.", encodingDetector.getEncoding());
        }

        return CssParser.parseStylesheet(contentHolder, CSSEncoding.iso88591, logger);
      }
    } catch (CSSException | ParseException | IOException e) {
      throw new CSSParsingException(e);
    }

  }
}
