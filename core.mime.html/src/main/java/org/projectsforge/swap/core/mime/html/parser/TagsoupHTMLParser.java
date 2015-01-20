package org.projectsforge.swap.core.mime.html.parser;

import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

class TagsoupHTMLParser extends Parser {

    private DOMHandler domHandler;

    /**
     * Instantiates a new tagsoup html parser.
     * @throws SAXNotSupportedException 
     * @throws SAXNotRecognizedException 
     */
    public TagsoupHTMLParser(DOMHandler domHandler) throws SAXNotRecognizedException, SAXNotSupportedException {
      super();
      setContentHandler(new SAXHTMLContentHandler(domHandler,false));
      this.domHandler = domHandler;
      // patched schema for correct noscript handling
      setProperty(Parser.schemaProperty, new TagSoupHTMLSchema());
    }

    /*
     * (non-Javadoc)
     * @see org.ccil.cowan.tagsoup.Parser#comment(char[], int, int)
     */
    @Override
    public void comment(final char[] ch, final int start, final int length) throws SAXException {
      domHandler.addComment(new String(ch, start, length));
    }

    /*
     * (non-Javadoc)
     * @see org.ccil.cowan.tagsoup.Parser#startDTD(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public void startDTD(final String name, final String publicId, final String systemId)
        throws SAXException {
      domHandler.setDTD(name, publicId, systemId);
    }
  }