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
package org.projectsforge.swap.core.encoding.html;

import info.monitorenter.cpdetector.io.ByteOrderMarkDetector;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;
import info.monitorenter.cpdetector.io.UnknownCharset;
import info.monitorenter.cpdetector.io.UnsupportedCharset;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import jregex.Pattern;
import jregex.RETokenizer;
import org.projectsforge.swap.core.http.RequestHeaderConstants;
import org.projectsforge.swap.core.http.Response;
import org.projectsforge.utils.temporarystreams.ContentHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to gather encoding information for a HTML document and to
 * compute effective encoding. <br>
 * <b>Warning:</b> Byte Order Mark detection is very limited.
 * 
 * @author Sébastien Aupetit
 */
public class HTMLEncodingDetector {

  /** body tag and what is before it. */
  private static final Pattern pattern1 = new Pattern("(?s)^.*<[bB][oO][dD][yY][^>]*>");

  /** script block. */
  private static final Pattern pattern2 = new Pattern("<\\s*script[^>]*>(.*?)<\\s*/\\s*script>");

  /** noscript block. */
  private static final Pattern pattern3 = new Pattern("<\\s*noscript[^>]*>(.*?)<\\s*/\\s*noscript>");

  /** comment block. */
  private static final Pattern pattern4 = new Pattern("<!\\s*--.*?--\\s*>");

  /** any tag. */
  private static final Pattern pattern5 = new Pattern("<[^>]*?>");

  /** numecical entities. */
  private static final Pattern pattern6 = new Pattern("&#[^;]*?;");

  /** litteral entities. */
  private static final Pattern pattern7 = new Pattern("&[a-zA-Z]*?;");

  /** white spaces. */
  private static final Pattern pattern8 = new Pattern("\\s");

  /**
   * Patterns to remove what is before {@code<body>}, comments, script blocks,
   * noscript blocks, html tags, html entities and comments.
   */
  private static final Pattern[] tagPatterns = new Pattern[] { HTMLEncodingDetector.pattern1,
      HTMLEncodingDetector.pattern2, HTMLEncodingDetector.pattern3, HTMLEncodingDetector.pattern4,
      HTMLEncodingDetector.pattern5, HTMLEncodingDetector.pattern6, HTMLEncodingDetector.pattern7,
      HTMLEncodingDetector.pattern8 };

  /** The Constant byteOrderMarkDetector. */
  private static final ByteOrderMarkDetector byteOrderMarkDetector = new ByteOrderMarkDetector();

  /** The Constant unicodeDetector. */
  private static final UnicodeDetector unicodeDetector = UnicodeDetector.getInstance();

  /** The Constant jcharsetFacade. */
  private static final JChardetFacade jcharsetFacade = JChardetFacade.getInstance();

  /** The Constant logger. */
  private static final Logger logger = LoggerFactory.getLogger(HTMLEncodingDetector.class);

  /** The Constant parsingDetector. */
  private static final ParsingDetector parsingDetector = new ParsingDetector(false);

  private static boolean isValidCharset(final String charset) {
    try {
      return Charset.isSupported(charset);
    } catch (final IllegalArgumentException ex) {
      return false;
    }
  }

  /**
   * Detect encoding using HTTP Content-Type (CT), HTML meta tag (M),
   * statistical detection (S) or default ISO-8851-1 (D).
   * 
   * @param response the response
   * @param statisticalDetectionFirst if true, the detection order is S, M, CT,
   *          D. If false, the detection order is M, CT, S, D.
   * @return the string
   */
  public String detectEncoding(final Response response, final boolean statisticalDetectionFirst) {
    String encoding;

    if (statisticalDetectionFirst) {

      encoding = detectStatistical(response);
      if (encoding == null || "void".equals(encoding) || !HTMLEncodingDetector.isValidCharset(encoding)) {
        encoding = detectFromHtmlMetaTag(response);
      }
      if (encoding == null || "void".equals(encoding) || !HTMLEncodingDetector.isValidCharset(encoding)) {
        encoding = detectFromHttpContentType(response);
      }
      if (encoding == null || "void".equals(encoding) || !HTMLEncodingDetector.isValidCharset(encoding)) {
        encoding = "ISO-8859-1";
      }
    } else {
      encoding = detectFromHtmlMetaTag(response);

      if (encoding == null || "void".equals(encoding) || !HTMLEncodingDetector.isValidCharset(encoding)) {
        encoding = detectFromHttpContentType(response);
      }
      if (encoding == null || "void".equals(encoding) || !HTMLEncodingDetector.isValidCharset(encoding)) {
        encoding = detectStatistical(response);
      }
      if (encoding == null || "void".equals(encoding) || !HTMLEncodingDetector.isValidCharset(encoding)) {
        encoding = "ISO-8859-1";
      }
    }
    return encoding;
  }

  /**
   * Detect meta.
   * 
   * @param response the response
   * @return the string
   */
  private String detectFromHtmlMetaTag(final Response response) {
    final ContentHolder content = response.getContent();
    if (content != null) {
      try (final InputStream in = response.getContent().getInputStream()) {
        final Charset charset = HTMLEncodingDetector.parsingDetector.detectCodepage(in, in.available());
        if (charset != null) {
          return charset.name();
        }
      } catch (final IOException e) {
        HTMLEncodingDetector.logger.warn("An error occurred while getting response content input stream for "
            + response, e);
        return null;
      }
    }
    return null;
  }

  /**
   * Detect http.
   * 
   * @param response the response
   * @return the string
   */
  private String detectFromHttpContentType(final Response response) {
    String encoding = response.getHeaders().getFirstValue(RequestHeaderConstants.CONTENT_TYPE);

    if (encoding != null) {
      final int index = encoding.indexOf("charset=");
      if (index != -1) {
        encoding = encoding.substring(index + "charset=".length());
      }
      if (encoding.isEmpty()) {
        encoding = null;
      }
    }
    return encoding;
  }

  /**
   * Detect statistical.
   * 
   * @param response the response
   * @return the string
   */
  private String detectStatistical(final Response response) {
    try {
      final ContentHolder ch = response.getContent();
      if (ch == null) {
        return null;
      }
      String content = ch.getContentAsStringBuilder("US-ASCII").toString();
      // extract text content from the document
      for (final Pattern pattern : HTMLEncodingDetector.tagPatterns) {
        HTMLEncodingDetector.logger.debug("Pattern {}", pattern);
        final RETokenizer tokenizer = pattern.tokenizer(content);
        final StringBuilder sb = new StringBuilder(content.length());
        while (tokenizer.hasMore()) {
          sb.append(tokenizer.nextToken());
        }
        content = sb.toString();
      }

      final byte[] contentBytes = content.getBytes();

      {
        // Byte order mark detector
        final Charset charset = HTMLEncodingDetector.byteOrderMarkDetector.detectCodepage(new ByteArrayInputStream(
            contentBytes), contentBytes.length);
        if (charset != null) {
          if (charset != UnknownCharset.getInstance()) {
            if (charset instanceof UnsupportedCharset) {
              HTMLEncodingDetector.logger.warn("Found an illegal charset: {}. Ignoring", charset);
            } else {
              return charset.name();
            }
          }
        }
      }

      {
        // Unicode detector
        final Charset charset = HTMLEncodingDetector.unicodeDetector.detectCodepage(new ByteArrayInputStream(
            contentBytes), contentBytes.length);
        if (charset != null) {
          if (charset != UnknownCharset.getInstance()) {
            if (charset instanceof UnsupportedCharset) {
              HTMLEncodingDetector.logger.warn("Found an illegal charset: {}. Ignoring", charset);
            } else {
              return charset.name();
            }
          }
        }
      }

      {
        // JCharsetDetector detector
        final Charset charset = HTMLEncodingDetector.jcharsetFacade.detectCodepage(new ByteArrayInputStream(
            contentBytes), contentBytes.length);
        if (charset != null) {
          if (charset != UnknownCharset.getInstance()) {
            if (charset instanceof UnsupportedCharset) {
              HTMLEncodingDetector.logger.warn("Found an illegal charset: {}. Ignoring", charset);
            } else {
              return charset.name();
            }
          }
        }
      }
    } catch (final Exception e) {
      // ignore
    }

    return null;
  }
}
