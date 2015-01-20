package org.projectsforge.swap.core.mime.html.parser;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import org.projectsforge.swap.core.mime.html.HtmlPropertyHolder;
import org.projectsforge.swap.core.mime.html.nodes.Comment;
import org.projectsforge.swap.core.mime.html.nodes.Document;
import org.projectsforge.swap.core.mime.html.nodes.Node;
import org.projectsforge.swap.core.mime.html.nodes.Text;
import org.projectsforge.swap.core.mime.html.nodes.elements.AbstractElement;
import org.projectsforge.swap.core.mime.html.nodes.elements.ElementFactory;
import org.projectsforge.utils.icasestring.ICaseString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class DOMHandler {

  private static final Logger logger = LoggerFactory.getLogger(DOMHandler.class);

  private static final Map<String, ICaseString> cachedNames = new ConcurrentHashMap<>();

  private static int CACHED_NAMES_MAX_SIZE = HtmlPropertyHolder.cachedNamesMaxSize.get();

  private static synchronized void resetCachedNames() {
    cachedNames.clear();
    for (Field field : org.projectsforge.swap.core.mime.html.nodes.elements.Attributes.class
        .getDeclaredFields()) {
      if ((field.getModifiers() & Modifier.STATIC) != 0
          && (field.getModifiers() & Modifier.PUBLIC) != 0
          && (field.getModifiers() & Modifier.FINAL) != 0) {
        try {
          Object value = field.get(null);
          if (value instanceof ICaseString) {
            ICaseString ics = (ICaseString) value;
            cachedNames.put(ics.getLowerCasedValue(), ics);
          }
        } catch (IllegalArgumentException | IllegalAccessException ex) {
          logger.warn("Can not retrieve field " + field + " by reflection. Ignoring...", ex);
        }

      }
    }
    if (CACHED_NAMES_MAX_SIZE < cachedNames.size()) {
      HtmlPropertyHolder.cachedNamesMaxSize.set(cachedNames.size() + 16);
      CACHED_NAMES_MAX_SIZE = HtmlPropertyHolder.cachedNamesMaxSize.get();
    }
  }

  static {
    resetCachedNames();

  }

  private static ICaseString getCachedName(final String name) {
    ICaseString result = cachedNames.get(name);
    if (result == null) {
      // prevent name caching to being to big in memory
      if (cachedNames.size() > CACHED_NAMES_MAX_SIZE) {
        resetCachedNames();
      }

      result = new ICaseString(name);
      ICaseString existing = cachedNames.get(result.getLowerCasedValue());
      if (existing == null)
        cachedNames.put(name, result);
      else
        cachedNames.put(name, existing);
    }
    return result;
  }

  /** The nodes stack. */
  private Stack<Node> nodesStack;

  private String encoding;
  private ElementFactory elementFactory;

  public DOMHandler(String encoding,ElementFactory elementFactory) {
    nodesStack = new Stack<Node>();
    nodesStack.push(new Document(encoding));
    this.encoding = encoding;
    this.elementFactory = elementFactory;
  }

  Logger getLogger() {
    return logger;
  }

  public void text(String text) throws SAXException {
    if (!text.isEmpty()) {
      Text node;

      final Node last = nodesStack.peek().getLastChild();
      if (last instanceof Text) {
        node = (Text) last;
      } else {
        node = new Text();
        nodesStack.peek().addChildAtEnd(node);
      }

      node.addContent(text);
    }
  }

  public void endElement() throws SAXException {
    nodesStack.pop();
  }

  public void addElement(String name) {
    final AbstractElement element = elementFactory.newElement(getCachedName(name));
    nodesStack.peek().addChildAtEnd(element);
    nodesStack.push(element);
  }

  public void addAttribute(String name, String value) {
    ((AbstractElement)nodesStack.peek()).setAttributeAndDecodeUrl(getCachedName(name), value, encoding);

  }

  public void addComment(String comment) {
    if (!comment.isEmpty()) {
      final Comment node = new Comment();
      node.setComment(comment);
      nodesStack.peek().addChildAtEnd(node);
    }
  }

  public void setDTD(String rootElement, String publicId, String systemId) {
    final Document doc = (Document) nodesStack.firstElement();
    doc.setRootElement(rootElement);
    doc.setPublicId(publicId);
    doc.setSystemId(systemId);
    // http://www.w3schools.com/tags/tag_DOCTYPE.asp
  }
  
  public Document getRootNode() {
    return (Document) nodesStack.firstElement();
  }

}
