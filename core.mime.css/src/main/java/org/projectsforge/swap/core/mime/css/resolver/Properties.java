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
 * <http://www.gnu.org/licenses/>. $Id$
 */
package org.projectsforge.swap.core.mime.css.resolver;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.projectsforge.utils.icasestring.ICaseString;
import org.projectsforge.utils.icasestring.ICaseStringKeyCollections;

/**
 * The class used to store computed properties by {@link RuleSetProcessor}.
 * 
 * @author Sébastien Aupetit
 */
public class Properties {

  /** The map. */
  private final Map<ICaseString, Object> map = new TreeMap<ICaseString, Object>();

  /**
   * Clear.
   */
  public void clear() {
    map.clear();
  }

  /**
   * Contains key.
   * 
   * @param key the key
   * @return true, if successful
   */
  public boolean containsKey(final ICaseString key) {
    return map.containsKey(key);
  }

  /**
   * Entry set.
   * 
   * @return the sets the
   */
  public Set<java.util.Map.Entry<ICaseString, Object>> entrySet() {
    return map.entrySet();
  }

  /**
   * Gets the.
   * 
   * @param key the key
   * @return the object
   */
  public Object get(final ICaseString key) {
    return map.get(key);
  }

  /**
   * Gets the.
   * 
   * @param <T> the generic type
   * @param name the name
   * @param theClass the the class
   * @return the t
   */
  public <T> T get(final ICaseString name, final Class<T> theClass) {
    return theClass.cast(map.get(name));
  }

  /**
   * Checks if is empty.
   * 
   * @return true, if is empty
   */
  public boolean isEmpty() {
    return map.isEmpty();
  }

  /**
   * Key set.
   * 
   * @return the sets the
   */
  public Set<ICaseString> keySet() {
    return ICaseStringKeyCollections.caseInsensitiveSet(map.keySet());
  }

  /**
   * Put.
   * 
   * @param key the key
   * @param value the value
   * @return the object
   */
  public Object put(final ICaseString key, final Object value) {
    return map.put(key, value);
  }

  /**
   * Put all.
   * 
   * @param m the m
   */
  public void putAll(final Map<? extends ICaseString, ? extends Object> m) {
    map.putAll(m);
  }

  /**
   * Removes the.
   * 
   * @param key the key
   * @return the object
   */
  public Object remove(final ICaseString key) {
    return map.remove(key);
  }

  /**
   * Size.
   * 
   * @return the int
   */
  public int size() {
    return map.size();
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return map.toString();
  }

  /**
   * Values.
   * 
   * @return the collection
   */
  public Collection<Object> values() {
    return map.values();
  }

}
