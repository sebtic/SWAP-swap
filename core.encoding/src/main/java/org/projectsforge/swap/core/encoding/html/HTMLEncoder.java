package org.projectsforge.swap.core.encoding.html;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriUtils;

public class HTMLEncoder {
  private static Logger logger = LoggerFactory.getLogger(HTMLEncoder.class);

  public static String htmlAttributeEncode(final String value, final String charset) {
    CharsetEncoder encoder = null;
    final StringBuilder sb = new StringBuilder();
    final int len = value.length();
    for (int i = 0; i < len; i++) {
      final char c = value.charAt(i);

      if (c == '"') {
        // handle quote
        sb.append("&quot;");
      } else if (0x20 <= c && c <= 0x7F) {
        // direct char
        sb.append(c);
      } else {
        if (encoder == null) {
          encoder = Charset.forName(charset).newEncoder();
        }

        if (encoder.canEncode(c)) {
          sb.append(c);
        } else {
          // other char are escaped by numerical values
          sb.append("&#").append(Integer.toString(c, 10)).append(';');
        }
      }
    }

    return sb.toString();
  }

  public static String htmlContentEncode(final String value, final String charset) {
    CharsetEncoder encoder = null;
    final StringBuilder sb = new StringBuilder();
    final int len = value.length();
    for (int i = 0; i < len; i++) {
      final char c = value.charAt(i);

      if (0x20 <= c && c <= 0x7F) {
        // direct char
        sb.append(c);
      } else {
        if (encoder == null) {
          encoder = Charset.forName(charset).newEncoder();
        }

        if (encoder.canEncode(c)) {
          sb.append(c);
        } else {
          // other char are escaped by numerical values
          sb.append("&#").append(Integer.toString(c, 10)).append(';');
        }
      }
    }

    return sb.toString();
  }

  public static String urlDecode(final String url, final String charset) {
    try {
      return UriUtils.decode(url, charset);
    } catch (UnsupportedEncodingException | IllegalArgumentException ex) {
      HTMLEncoder.logger.debug("Can not decode URI " + url + " with charset " + charset, ex);
      return url;
    }
  }

  @SuppressWarnings("deprecation")
  public static String urlEncode(final String url, final String charset) {
    try {
      return UriUtils.encodeUri(url, charset);
    } catch (final UnsupportedEncodingException ex) {
      HTMLEncoder.logger.debug("Can not encode URI " + url + " with charset " + charset, ex);
      return url;
    }
  }

}
