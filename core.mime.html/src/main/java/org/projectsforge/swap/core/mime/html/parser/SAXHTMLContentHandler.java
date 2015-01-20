package org.projectsforge.swap.core.mime.html.parser;

import org.apache.commons.lang.StringEscapeUtils;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class SAXHTMLContentHandler implements ContentHandler {

  private DOMHandler domHandler;

  private boolean needEntitiesDecoding;

  public SAXHTMLContentHandler(DOMHandler domHandler, boolean needEntitiesDecoding) {
    this.domHandler = domHandler;
    this.needEntitiesDecoding = needEntitiesDecoding;
  }

  /*
   * (non-Javadoc)
   * @see org.xml.sax.ContentHandler#characters(char[], int, int)
   */
  @Override
  public void characters(final char[] ch, final int start, final int length) throws SAXException {
    if (needEntitiesDecoding)
      domHandler.text(StringEscapeUtils.unescapeHtml(new String(ch, start, length)));
    else
      domHandler.text(new String(ch, start, length));
  }

  /*
   * (non-Javadoc)
   * @see org.xml.sax.ContentHandler#endDocument()
   */
  @Override
  public void endDocument() throws SAXException {
  }

  /*
   * (non-Javadoc)
   * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
   * java.lang.String, java.lang.String)
   */
  @Override
  public void endElement(final String uri, final String localName, final String qName)
      throws SAXException {
    domHandler.endElement();
  }

  /*
   * (non-Javadoc)
   * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
   */
  @Override
  public void endPrefixMapping(final String prefix) throws SAXException {
  }

  /*
   * (non-Javadoc)
   * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
   */
  @Override
  public void ignorableWhitespace(final char[] ch, final int start, final int length)
      throws SAXException {
    domHandler.getLogger().warn("ignorableWhitespace '{}' found", new String(ch, start, length));
  }

  /*
   * (non-Javadoc)
   * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void processingInstruction(final String target, final String data) throws SAXException {
  }

  /*
   * (non-Javadoc)
   * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
   */
  @Override
  public void setDocumentLocator(final Locator locator) {
  }

  /*
   * (non-Javadoc)
   * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
   */
  @Override
  public void skippedEntity(final String name) throws SAXException {
  }

  /*
   * (non-Javadoc)
   * @see org.xml.sax.ContentHandler#startDocument()
   */
  @Override
  public void startDocument() throws SAXException {
  }

  /*
   * (non-Javadoc)
   * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
   * java.lang.String, java.lang.String, org.xml.sax.Attributes)
   */
  @Override
  public void startElement(final String uri, final String localName, final String qName,
      final Attributes atts) {
    domHandler.addElement(qName);
    for (int i = 0; i < atts.getLength(); ++i) {
      if (needEntitiesDecoding)
        domHandler.addAttribute(atts.getLocalName(i),
            StringEscapeUtils.unescapeHtml(atts.getValue(i)));
      else
        domHandler.addAttribute(atts.getLocalName(i), atts.getValue(i));
    }
  }

  /*
   * (non-Javadoc)
   * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String,
   * java.lang.String)
   */
  @Override
  public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
  }
}
