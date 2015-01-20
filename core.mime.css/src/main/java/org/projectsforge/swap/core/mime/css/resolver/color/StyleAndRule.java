package org.projectsforge.swap.core.mime.css.resolver.color;

import org.projectsforge.swap.core.mime.css.resolver.CssRule;
import org.projectsforge.swap.core.mime.html.nodes.elements.AbstractElement;
import org.w3c.css.sac.LexicalUnit;

public class StyleAndRule {
  public CssRule rule;

  public BorderStyle style;

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof StyleAndRule)) {
      return false;
    }
    final StyleAndRule other = (StyleAndRule) obj;
    if (rule == null) {
      if (other.rule != null) {
        return false;
      }
    } else if (!rule.equals(other.rule)) {
      return false;
    }
    if (style != other.style) {
      return false;
    }
    return true;
  }

  public void extract(final CssRule rule, final AbstractElement element, final LexicalUnit lu) {
    if (this.rule != null) {
      return;
    }
    if (lu.getLexicalUnitType() == LexicalUnit.SAC_IDENT) {
      final String value = lu.getStringValue().toLowerCase();
      for (final BorderStyle s : BorderStyle.values()) {
        if (s.cssName.equals(value)) {
          this.rule = rule;
          this.style = s;
          break;
        }
      }
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((rule == null) ? 0 : rule.hashCode());
    result = prime * result + ((style == null) ? 0 : style.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "[rule=" + rule + ", style=" + (style == null ? null : style.cssName) + "]";
  }
}
