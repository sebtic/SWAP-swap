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
package org.projectsforge.swap.core.encoding.css;

import info.monitorenter.cpdetector.io.JChardetFacade;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import org.projectsforge.utils.temporarystreams.ContentHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is used to gather encoding information for a stylesheet and to
 * compute effective encoding according to the <a
 * href="http://www.w3.org/TR/CSS21/syndata.html#charset">specifications</a>. <br>
 * <b>Warning:</b> Byte Order Mark detection is very limited.
 * 
 * @author Sébastien Aupetit
 */
public class CSSEncodingDetector extends CSSEncoding {

  /** The Constant logger. */
  private static final Logger logger = LoggerFactory.getLogger(CSSEncodingDetector.class);

  /** The HTTP header encoding. */
  private String httpHeaderEncoding;

  /** The {@code @encoding}. */
  private String atEncoding;

  /** The link encoding. */
  private String linkEncoding;

  /** The parent encoding. */
  private String parentEncoding;

  /** The jcharset facade. */
  private final JChardetFacade jcharsetFacade = JChardetFacade.getInstance();

  public CSSEncodingDetector() {
    super("UTF-8");
  }

  /**
   * Detect encoding from content using charset markers, @encoding and a
   * statistical encoding detector.
   * 
   * @param contentHolder the content holder
   */
  public void detectEncoding(final ContentHolder contentHolder) {
    String charset;

    charset = detectPattern(contentHolder, new int[] { 0xEF, 0xBB, 0xBF, 0x40, 0x63, 0x68, 0x61, 0x72, 0x73, 0x65,
        0x74, 0x20, 0x22 }, new int[] { 0x22, 0x3B });
    if (charset != null && !charset.isEmpty()) {
      setAtEncoding(charset);
      return;
    }
    if (detectPattern(contentHolder, new int[] { 0xEF, 0xBB, 0xBF })) {
      setAtEncoding("UTF-8");
      return;
    }

    charset = detectPattern(contentHolder, new int[] { 0x40, 0x63, 0x68, 0x61, 0x72, 0x73, 0x65, 0x74, 0x20, 0x22 },
        new int[] { 0x22, 0x3B });
    if (charset != null && !charset.isEmpty()) {
      setAtEncoding(charset);
      return;
    }

    if (detectPattern(contentHolder, new int[] { 0x00, 0x00, 0xFE, 0xFF })) {
      setAtEncoding("UTF-32-BE");
      return;
    }

    if (detectPattern(contentHolder, new int[] { 0xFF, 0xFE, 0x00, 0x00 })) {
      setAtEncoding("UTF-32-LE");
      return;
    }

    if (detectPattern(contentHolder, new int[] { 0x00, 0x00, 0xFF, 0xFE })) {
      setAtEncoding("UTF-32-2143");
      return;
    }

    if (detectPattern(contentHolder, new int[] { 0xFE, 0xFF, 0x00, 0x00 })) {
      setAtEncoding("UTF-32-3412");
      return;
    }

    if (detectPattern(contentHolder, new int[] { 0xFE, 0xFF })) {
      setAtEncoding("UTF-16-BE");
      return;
    }

    if (detectPattern(contentHolder, new int[] { 0xFF, 0xFE })) {
      setAtEncoding("UTF-16-BE");
      return;
    }

    // @charset
    charset = detectPattern(contentHolder,
        new int[] { 0x40, 0x63, 0x68, 0x61, 0x72, 0x73, 0x65, 0x74, 0x20, 0x22, 0x0a }, new int[] { 0x22, 0x3b });
    if (charset != null && !charset.isEmpty()) {
      setAtEncoding(charset);
      return;
    }

    try (final BufferedReader reader = new BufferedReader(new InputStreamReader(contentHolder.getInputStream(),
        Charset.forName("US-ASCII")))) {
      String line;
      if ((line = reader.readLine()) != null) {
        line = line.trim();

        if (line.startsWith("@charset")) {
          String encoding;
          final int index = line.indexOf('"');
          if (index == -1) {
            return;
          }
          final int endIndex = line.indexOf('"', index + 1);
          if (endIndex == -1) {
            encoding = line.substring(index + 1);
          } else {
            encoding = line.substring(index + 1, endIndex);
          }
          if (encoding != null) {
            encoding = encoding.trim();
            if (!encoding.isEmpty()) {
              setAtEncoding(encoding);
            }
          }
          return;
        } else {
          if (!line.isEmpty()) {
            return;
          }
        }
      }
    } catch (final IOException e) {
      CSSEncodingDetector.logger.warn("An error occurred while reading input stream", e);
    }

    if (atEncoding == null) {
      setAtEncoding(statisticalDetector(contentHolder));
    }

  }

  /**
   * Detect pattern.
   * 
   * @param contentHolder the content holder
   * @param start the start
   * @return true, if successful
   */
  private boolean detectPattern(final ContentHolder contentHolder, final int[] start) {
    int b;
    try (InputStream in = contentHolder.getInputStream()) {
      // start with...
      for (final int element : start) {
        b = in.read();
        if (b == -1) {
          return false;
        }
        if (b != element) {
          return false;
        }
      }
      return true;
    } catch (final IOException e) {
      CSSEncodingDetector.logger.warn("An error occurred while reading input stream", e);
      return false;
    }
  }

  /**
   * Detect pattern.
   * 
   * @param contentHolder the content holder
   * @param start the start
   * @param end the end
   * @return the string
   */
  private String detectPattern(final ContentHolder contentHolder, final int[] start, final int[] end) {
    try {
      int b;
      final StringBuilder name = new StringBuilder();
      final int[] last = new int[end.length];

      try (final InputStream in = contentHolder.getInputStream()) {

        // start with...
        for (final int element : start) {
          b = in.read();
          if (b == -1) {
            return null;
          }
          if (b != element) {
            return null;
          }
        }

        // fill buffer
        for (int i = 0; i < end.length; ++i) {
          b = in.read();
          if (b == -1) {
            return null;
          }
          last[i] = b;
        }

        while (true) {
          // test matching
          boolean match = true;
          for (int i = 0; i < end.length; ++i) {
            if (last[i] != end[i]) {
              match = false;
            }
          }

          if (match) {
            return name.toString().trim();
          } else {
            // shift
            name.append((char) last[0]);
            for (int i = 1; i < end.length; ++i) {
              last[i - 1] = last[i];
            }
            last[end.length - 1] = in.read();
            if (last[end.length - 1] == -1) {
              return null;
            }
          }
        }
      }
    } catch (final IOException e) {
      CSSEncodingDetector.logger.warn("An error occurred while reading input stream", e);
      return null;
    }

  }

  /**
   * Gets the effective encoding using specification priorities.
   * 
   * @return the effective encoding
   */
  @Override
  public String getEncoding() {
    if (httpHeaderEncoding != null) {
      return httpHeaderEncoding;
    } else if (atEncoding != null) {
      return atEncoding;
    } else if (linkEncoding != null) {
      return linkEncoding;
    } else if (parentEncoding != null) {
      return parentEncoding;
    } else {
      return super.getEncoding();
    }
  }

  /**
   * Sets the {@code @encoding}.
   * 
   * @param encoding the new at encoding
   */
  private void setAtEncoding(final String encoding) {
    if (encoding != null) {
      if (Charset.isSupported(encoding)) {
        if (atEncoding == null) {
          this.atEncoding = encoding;
        } else {
          CSSEncodingDetector.logger
              .warn(
                  "Multiple @charset defined for stylesheet: previous {}, new {}. Ignoring new definition according to specification",
                  atEncoding, encoding);
        }
      } else {
        CSSEncodingDetector.logger.warn("Ignoring unsupported charset {}", encoding);
      }
    }
  }

  /**
   * Sets the http header encoding.
   * 
   * @param encoding the new http header encoding
   */
  public void setHttpHeaderEncoding(final String encoding) {
    if (encoding != null) {
      if (Charset.isSupported(encoding)) {
        this.httpHeaderEncoding = encoding;
      } else {
        CSSEncodingDetector.logger.warn("Ignoring unsupported charset {}", encoding);
      }
    }
  }

  /**
   * Sets the link encoding.
   * 
   * @param encoding the new link encoding
   */
  public void setLinkEncoding(final String encoding) {
    if (encoding != null) {
      if (Charset.isSupported(encoding)) {
        this.linkEncoding = encoding;
      } else {
        CSSEncodingDetector.logger.warn("Ignoring unsupported charset {}", encoding);
      }
    }
  }

  /**
   * Sets the parent encoding.
   * 
   * @param encoding the new parent encoding
   */
  public void setParentEncoding(final String encoding) {
    if (encoding != null) {
      if (Charset.isSupported(encoding)) {
        this.parentEncoding = encoding;
      } else {
        CSSEncodingDetector.logger.warn("Ignoring unsupported charset {}", encoding);
      }
    }
  }

  /**
   * Statistical detector.
   * 
   * @param contentHolder the content holder
   * @return the string
   */
  private String statisticalDetector(final ContentHolder contentHolder) {
    Charset charset = null;
    try (final InputStream in = contentHolder.getInputStream()) {
      charset = jcharsetFacade.detectCodepage(in, in.available());
    } catch (final IOException e) {
      CSSEncodingDetector.logger.warn("An error occurred while reading input stream", e);
      return null;
    }
    if (charset != null) {
      return charset.toString();
    } else {
      return null;
    }
  }

}
