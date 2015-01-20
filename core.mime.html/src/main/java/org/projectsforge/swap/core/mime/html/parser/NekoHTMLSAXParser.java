package org.projectsforge.swap.core.mime.html.parser;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.cyberneko.html.parsers.SAXParser;

class NekoHTMLSAXParser extends SAXParser {

    private DOMHandler domHandler;

    /**
     * Instantiates a new hTMLSAX parser.
     */
    public NekoHTMLSAXParser(DOMHandler domHandler) {
      super();
      setContentHandler(new SAXHTMLContentHandler(domHandler,false));
      this.domHandler = domHandler;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.apache.xerces.parsers.AbstractSAXParser#comment(org.apache.xerces
     * .xni.XMLString, org.apache.xerces.xni.Augmentations)
     */
    @Override
    public void comment(final XMLString text, final Augmentations augs) throws XNIException {
      super.comment(text, augs);
      domHandler.addComment(text.toString().trim());
    }

    /*
     * (non-Javadoc)
     * @see
     * org.apache.xerces.parsers.AbstractSAXParser#doctypeDecl(java.lang.String,
     * java.lang.String, java.lang.String, org.apache.xerces.xni.Augmentations)
     */
    @Override
    public void doctypeDecl(final String rootElement, final String publicId, final String systemId,
        final Augmentations augs) throws XNIException {
      super.doctypeDecl(rootElement, publicId, systemId, augs);
      domHandler.setDTD(rootElement, publicId, systemId);
    }

  }