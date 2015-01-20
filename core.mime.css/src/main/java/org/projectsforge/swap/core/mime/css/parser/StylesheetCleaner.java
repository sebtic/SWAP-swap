package org.projectsforge.swap.core.mime.css.parser;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import com.phloc.commons.annotations.OverrideOnDemand;
import com.phloc.css.decl.CSSDeclaration;
import com.phloc.css.decl.CSSStyleRule;
import com.phloc.css.decl.visit.DefaultCSSVisitor;

public class StylesheetCleaner extends DefaultCSSVisitor {

  @Override
  @OverrideOnDemand
  public void onBeginStyleRule(@Nonnull final CSSStyleRule styleRule) {
    final List<CSSDeclaration> toBeRemoved = new ArrayList<>();
    for (final CSSDeclaration declaration : styleRule.getAllDeclarations()) {
      if (declaration.getProperty().startsWith("-")) {
        toBeRemoved.add(declaration);
      }
    }
    for (final CSSDeclaration declaration : toBeRemoved) {
      styleRule.removeDeclaration(declaration);
    }
  }
}
