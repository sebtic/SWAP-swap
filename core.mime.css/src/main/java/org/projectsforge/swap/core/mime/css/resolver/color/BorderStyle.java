package org.projectsforge.swap.core.mime.css.resolver.color;


public enum BorderStyle {
  NONE("none"), HIDDEN("hidden"), DOTTED("dotted"), DASHED("dotted"), SOLID("solid"), DOUBLE(
      "double"), GROOVE("groove"), RIDGE("ridge"), INSET("inset"), OUTSET("outset");

  public String cssName;

  BorderStyle(final String cssName) {
    this.cssName = cssName;
  }

}
