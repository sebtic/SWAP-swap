/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr> This file
 * is part of SWAP. SWAP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version. SWAP is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with SWAP. If not, see
 * <http://www.gnu.org/licenses/>. $Id: AbstractElement.java 91 2011-07-18
 * 16:28:31Z sebtic $
 */
package org.projectsforge.swap.core.mime.html.nodes.elements;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.projectsforge.swap.core.encoding.html.HTMLEncoder;
import org.projectsforge.swap.core.mime.html.nodes.Node;
import org.projectsforge.utils.icasestring.ICaseString;

// TODO: Auto-generated Javadoc
/**
 * The base class for all tag.
 * 
 * @author Sébastien Aupetit
 */
public abstract class AbstractElement extends Node {

  /** The attributes. */
  private final Map<ICaseString, String> attributes = new LinkedHashMap<ICaseString, String>();

  /** Cached tag name to reduce CPU usage from introspection of the attribute. */
  private ICaseString cachedTagName = null;

  /**
   * The Constructor.
   */
  protected AbstractElement() {
  }

  /**
   * Clear attributes.
   */
  public void clearAttributes() {
    attributes.clear();
  }

  /**
   * Gets the value of an attribute.
   * 
   * @param key the key
   * @return the string
   */
  public String getAttribute(final ICaseString key) {
    return attributes.get(key);
  }

  /**
   * Put the value of an attribute.
   * 
   * @param key the key
   * @param value the value or null to remove the attribute
   * @return the previous value of the attribute
   */
  public String setAttribute(final ICaseString key, final String value) {
    if (value == null) {
      return attributes.remove(key);
    } else {
        return attributes.put(key, value);
    }
  }
  
  public String setAttributeAndDecodeUrl(final ICaseString key, final String value, String charset) {
    if (value == null) {
      return attributes.remove(key);
    } else {
      if (getAttributeType(key) == AttributeType.URL) {
        return attributes.put(key, HTMLEncoder.urlDecode(value,charset));
      } else
        return attributes.put(key, value);
    }
  }

  public enum AttributeType {
    HTML, URL
  }

  public AttributeType getAttributeType(ICaseString key) {   
      return AttributeType.HTML;
  }

  public String getEncodedAttribute(ICaseString key, String encoding) {
    if (getAttributeType(key) == AttributeType.HTML)
      return HTMLEncoder.htmlAttributeEncode(attributes.get(key), encoding);
    else
      return HTMLEncoder.urlEncode(attributes.get(key), encoding);
  }

  /**
   * Gets the attributes as a string.
   * 
   * @param needContentEscaping indicate if the special chars must be escaped of
   *          not
   * @return the attributes as string
   */
  public String getAttributesAsString(String encoding) {
    boolean first = true;
    final StringBuilder sb = new StringBuilder();

    for (final ICaseString key : attributes.keySet()) {
      if (first) {
        first = false;
      } else {
        sb.append(' ');
      }
      sb.append(key.getLowerCasedValue()).append("=\"");
      sb.append(getEncodedAttribute(key, encoding).replaceAll("\"", "&quot;"));
      sb.append('"');
    }

    return sb.toString();
  }

  /**
   * Gets the attributes count.
   * 
   * @return the attributes count
   */
  public int getAttributesCount() {
    return attributes.size();
  }

  /**
   * Gets the attributes entry set.
   * 
   * @return the attributes entry set
   */
  public Set<Map.Entry<ICaseString, String>> getAttributesEntrySet() {
    return attributes.entrySet();
  }

  /**
   * Gets the attributes key set.
   * 
   * @return the attributes key set
   */
  public Set<ICaseString> getAttributesKeySet() {
    return attributes.keySet();
  }

  /**
   * Gets the name.
   * 
   * @return the name
   */
  public ICaseString getTagName() {
    if (cachedTagName == null) {
      Field nameField;
      try {
        nameField = getClass().getField("TAGNAME");
        cachedTagName = ICaseString.class.cast(nameField.get(this));
      } catch (NoSuchFieldException | SecurityException | IllegalArgumentException
          | IllegalAccessException e) {
        throw new IllegalStateException(e);
      }
    }
    return cachedTagName;
  }

  /**
   * Test if an attribute is present.
   * 
   * @param key the attribute name
   * @return true, if present
   */
  public boolean hasAttribute(final ICaseString key) {
    return attributes.containsKey(key);
  }

  /**
   * Checks for attributes.
   * 
   * @return true, if successful
   */
  public boolean hasAttributes() {
    return !attributes.isEmpty();
  }

  /**
   * Removes the attribute.
   * 
   * @param key the key
   * @return the string
   */
  public String removeAttribute(final ICaseString key) {
    return attributes.remove(key);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getTagName() + "[" + getAttributesAsString("UTF8") + "]";
  }

}
