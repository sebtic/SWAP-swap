package org.projectsforge.swap.core.mime.html.parser;

import org.ccil.cowan.tagsoup.HTMLSchema;
import org.ccil.cowan.tagsoup.Schema;

class TagSoupHTMLSchema extends HTMLSchema {

    /**
     * Instantiates a new tag soup html schema.
     */
    public TagSoupHTMLSchema() {
      super();
      // replace noscript handling such that it behave like a script tag
      elementType("noscript", Schema.M_PCDATA, Schema.M_ANY & ~Schema.M_ROOT, Schema.F_CDATA);
      parent("noscript", "html");
    }
  }