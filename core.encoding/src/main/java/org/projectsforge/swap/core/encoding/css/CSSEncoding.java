package org.projectsforge.swap.core.encoding.css;

public class CSSEncoding {

  public final static CSSEncoding iso88591 = new CSSEncoding("ISO8859_1");

  public final static CSSEncoding utf8 = new CSSEncoding("UTF-8");

  private final String encoding;

  protected CSSEncoding(final String encoding) {
    this.encoding = encoding;
  }

  public String getEncoding() {
    return encoding;
  }

  @Override
  public String toString() {
    return getEncoding();
  }
}
