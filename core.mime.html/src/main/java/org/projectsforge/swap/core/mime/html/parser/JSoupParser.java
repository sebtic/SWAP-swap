package org.projectsforge.swap.core.mime.html.parser;

import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.TextNode;
import org.jsoup.nodes.XmlDeclaration;
import org.xml.sax.SAXException;

class JSoupParser {
    private DOMHandler domHandler;

    public JSoupParser(DOMHandler domHandler) {
      this.domHandler = domHandler;
    }
        
    private void recurse(org.jsoup.nodes.Node node) throws SAXException {
        if(node instanceof TextNode) {
          TextNode textNode = (TextNode) node;
          domHandler.text(StringEscapeUtils.unescapeHtml(textNode.getWholeText()));
        } else if(node instanceof DataNode) {
          DataNode dataNode = (DataNode) node;
          domHandler.text(StringEscapeUtils.unescapeHtml(dataNode.getWholeData()));
        } else if(node instanceof XmlDeclaration) {
          XmlDeclaration xmlNode = (XmlDeclaration) node;
          domHandler.text(StringEscapeUtils.unescapeHtml(xmlNode.getWholeDeclaration()));
        } else if(node instanceof org.jsoup.nodes.DocumentType) {
          org.jsoup.nodes.DocumentType doctypeNode = (org.jsoup.nodes.DocumentType) node;
          domHandler.setDTD(doctypeNode.attr("name"),doctypeNode.attr("publicId"),doctypeNode.attr("systemId"));
        } else if(node instanceof org.jsoup.nodes.Comment) {
          org.jsoup.nodes.Comment commentNode = (org.jsoup.nodes.Comment) node;
          domHandler.addComment(commentNode.getData());
        } else {
          domHandler.addElement(node.nodeName());
          for( Attribute attr : node.attributes()) {
            domHandler.addAttribute(attr.getKey(), StringEscapeUtils.unescapeHtml(attr.getValue()));
          }
          for(org.jsoup.nodes.Node child: node.childNodes()) {
            recurse(child);
          }
          domHandler.endElement();    
        }
    }
  
    public void parse(InputStream in, String encoding) throws IOException, SAXException {
      /* NOTE should not be produced anymore
       * if (encoding.equals("void") ) {
        // Workaround to prevent sending an invalid encoding to the parser
        encoding = null;
      }*/
      org.jsoup.nodes.Document doc = Jsoup.parse(in, encoding, "");
      for(org.jsoup.nodes.Node node: doc.childNodes()) {
        recurse(node);
      }
    }
    
  }