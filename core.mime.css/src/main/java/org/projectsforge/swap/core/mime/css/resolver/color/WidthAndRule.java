package org.projectsforge.swap.core.mime.css.resolver.color;

import org.projectsforge.swap.core.mime.css.LexicalUnitUtil;
import org.projectsforge.swap.core.mime.css.resolver.CssRule;
import org.projectsforge.swap.core.mime.html.nodes.elements.AbstractElement;
import org.w3c.css.sac.LexicalUnit;

public class WidthAndRule {
  public CssRule rule;

  public String width;

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof WidthAndRule)) {
      return false;
    }
    final WidthAndRule other = (WidthAndRule) obj;
    if (rule == null) {
      if (other.rule != null) {
        return false;
      }
    } else if (!rule.equals(other.rule)) {
      return false;
    }
    if (width == null) {
      if (other.width != null) {
        return false;
      }
    } else if (!width.equals(other.width)) {
      return false;
    }
    return true;
  }

  public void extract(final CssRule rule, final AbstractElement element, final LexicalUnit lu) {
    if (this.rule != null) {
      return;
    }
    switch (lu.getLexicalUnitType()) {
      case LexicalUnit.SAC_IDENT:
        this.rule = rule;
        this.width = lu.getStringValue().toLowerCase();
        break;
      case LexicalUnit.SAC_DIMENSION:
      case LexicalUnit.SAC_REAL:
      case LexicalUnit.SAC_INTEGER:
      case LexicalUnit.SAC_EM:
      case LexicalUnit.SAC_EX:
      case LexicalUnit.SAC_PERCENTAGE:
      case LexicalUnit.SAC_PIXEL:
      case LexicalUnit.SAC_INCH:
      case LexicalUnit.SAC_CENTIMETER:
      case LexicalUnit.SAC_MILLIMETER:
      case LexicalUnit.SAC_POINT:
      case LexicalUnit.SAC_PICA:
        this.rule = rule;
        this.width = LexicalUnitUtil.toString(lu);
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((rule == null) ? 0 : rule.hashCode());
    result = prime * result + ((width == null) ? 0 : width.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "[rule=" + rule + ", width=" + width + "]";
  }
}
