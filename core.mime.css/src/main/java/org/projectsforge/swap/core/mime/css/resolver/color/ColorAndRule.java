package org.projectsforge.swap.core.mime.css.resolver.color;

import org.projectsforge.swap.core.mime.css.property.color.ImmutableSRGBColor;
import org.projectsforge.swap.core.mime.css.property.color.SRGBColor;
import org.projectsforge.swap.core.mime.css.resolver.CssRule;
import org.projectsforge.swap.core.mime.html.nodes.elements.AbstractElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.HTMLElement;
import org.projectsforge.utils.icasestring.ICaseString;
import org.w3c.css.sac.LexicalUnit;

public class ColorAndRule {
  public ImmutableSRGBColor color = null;

  public CssRule rule = null;

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof ColorAndRule)) {
      return false;
    }
    final ColorAndRule other = (ColorAndRule) obj;
    if (color == null) {
      if (other.color != null) {
        return false;
      }
    } else if (!color.equals(other.color)) {
      return false;
    }
    if (rule == null) {
      if (other.rule != null) {
        return false;
      }
    } else if (!rule.equals(other.rule)) {
      return false;
    }
    return true;
  }

  public void extract(final CssRule rule, final AbstractElement element, final LexicalUnit lu) {
    if (this.rule != null) {
      return;
    }
    if (lu.getLexicalUnitType() == LexicalUnit.SAC_RGBCOLOR) {
      final LexicalUnit params = lu.getParameters();
      // r,g,b
      final int r = params.getIntegerValue();
      final int g = params.getNextLexicalUnit().getNextLexicalUnit().getIntegerValue();
      final int b = params.getNextLexicalUnit().getNextLexicalUnit().getNextLexicalUnit()
          .getNextLexicalUnit().getIntegerValue();
      this.color = new ImmutableSRGBColor(r, g, b);
      this.rule = rule;
    } else if (lu.getLexicalUnitType() == LexicalUnit.SAC_INHERIT) {
      this.color = null;
      this.rule = rule;
    } else if (lu.getLexicalUnitType() == LexicalUnit.SAC_IDENT) {
      if ("transparent".equalsIgnoreCase(lu.getStringValue())) {
        // transparent color for html tag is meaningless since there is no
        // parent tag
        if (!(element instanceof HTMLElement)) {
          // TODO : actually 'transparent' is handled as inherit. Doing more
          // advanced computing would lead to far for infrequent cases
          this.color = null;
          this.rule = rule;
        }
      } else {
        this.color = SRGBColor.getColorByName(new ICaseString(lu.getStringValue()));
        if (this.color != null) {
          this.rule = rule;
        }
      }
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((color == null) ? 0 : color.hashCode());
    result = prime * result + ((rule == null) ? 0 : rule.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "[rule=" + rule + ", color=" + color + "]";
  }
}
