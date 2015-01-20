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
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.projectsforge.swap.core.mime.css.resolver.PropertyResolver.Processor;
import org.projectsforge.utils.icasestring.ICaseString;
import org.projectsforge.utils.icasestring.ICaseStringKeyCollections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class used to store computed properties by {@link Processor}.
 */
public class Properties implements Map<ICaseString, Object> {

  /** The logger. */
  private static Logger logger = LoggerFactory.getLogger(Properties.class);

  /** The map. */
  private final Map<ICaseString, Object> map = new HashMap<ICaseString, Object>();

  /** The Constant CSS_COLOR. */
  public static final ICaseString CSS_COLOR = new ICaseString("css:color");

  /** The Constant CSS_RULE_COLOR. */
  public static final ICaseString CSS_RULE_COLOR = new ICaseString("css-rule:color");

  /** The Constant CSS_BACKGROUNDCOLOR. */
  public static final ICaseString CSS_BACKGROUNDCOLOR = new ICaseString("css:background-color");

  /** The Constant CSS_RULE_BACKGROUNDCOLOR. */
  public static final ICaseString CSS_RULE_BACKGROUNDCOLOR = new ICaseString(
      "css-rule:background-color");

  /** The Constant CSS_FONT. */
  public static final ICaseString CSS_FONT = new ICaseString("css:font");

  /*
   * (non-Javadoc)
   * @see java.util.Map#clear()
   */
  @Override
  public void clear() {
    map.clear();
  }

  /*
   * (non-Javadoc)
   * @see java.util.Map#containsKey(java.lang.Object)
   */
  @Override
  public boolean containsKey(final Object key) {
    if (key instanceof ICaseString) {
      return map.containsKey(key);
    } else if (key instanceof String) {
      Properties.logger.warn(
          "containsKey called with a String ({}) instead of a ICaseString on {}", key, map);
      Properties.logger.warn("Current stack trace is:", new Exception());
      return map.containsKey(new ICaseString((String) key));
    } else {
      Properties.logger.error(
          "containsKey called with {} instead of a ICaseString on {}. It's certainly an error",
          key, map);
      Properties.logger.error("Current stack trace is:", new Exception());
      return map.containsKey(key);
    }
  }

  /*
   * (non-Javadoc)
   * @see java.util.Map#containsValue(java.lang.Object)
   */
  @Override
  public boolean containsValue(final Object value) {
    return map.containsValue(value);
  }

  /*
   * (non-Javadoc)
   * @see java.util.Map#entrySet()
   */
  @Override
  public Set<java.util.Map.Entry<ICaseString, Object>> entrySet() {
    return map.entrySet();
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

  /*
   * (non-Javadoc)
   * @see java.util.Map#get(java.lang.Object)
   */
  @Override
  public Object get(final Object key) {
    if (key instanceof ICaseString) {
      return map.get(key);
    } else if (key instanceof String) {
      Properties.logger.warn("get called with a String ({}) instead of a ICaseString on {}", key,
          map);
      Properties.logger.warn("Current stack trace is:", new Exception());
      return map.get(new ICaseString((String) key));
    } else {
      Properties.logger.error(
          "get called with {} instead of a ICaseString on {}. It's certainly an error", key, map);
      Properties.logger.error("Current stack trace is:", new Exception());
      return map.get(key);
    }
  }

  /*
   * (non-Javadoc)
   * @see java.util.Map#isEmpty()
   */
  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }

  /*
   * (non-Javadoc)
   * @see java.util.Map#keySet()
   */
  @Override
  public Set<ICaseString> keySet() {
    return ICaseStringKeyCollections.caseInsensitiveSet(map.keySet());
  }

  /*
   * (non-Javadoc)
   * @see java.util.Map#put(java.lang.Object, java.lang.Object)
   */
  @Override
  public Object put(final ICaseString key, final Object value) {
    return map.put(key, value);
  }

  /*
   * (non-Javadoc)
   * @see java.util.Map#putAll(java.util.Map)
   */
  @Override
  public void putAll(final Map<? extends ICaseString, ? extends Object> m) {
    map.putAll(m);
  }

  /*
   * (non-Javadoc)
   * @see java.util.Map#remove(java.lang.Object)
   */
  @Override
  public Object remove(final Object key) {
    if (key instanceof ICaseString) {
      return map.remove(key);
    } else if (key instanceof String) {
      Properties.logger.warn("remove called with a String ({}) instead of a ICaseString on {}",
          key, map);
      Properties.logger.warn("Current stack trace is:", new Exception());
      return map.remove(new ICaseString((String) key));
    } else {
      Properties.logger
          .error("remove called with {} instead of a ICaseString on {}. It's certainly an error",
              key, map);
      Properties.logger.error("Current stack trace is:", new Exception());
      return map.remove(key);
    }
  }

  /*
   * (non-Javadoc)
   * @see java.util.Map#size()
   */
  @Override
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

  /*
   * (non-Javadoc)
   * @see java.util.Map#values()
   */
  @Override
  public Collection<Object> values() {
    return map.values();
  }

}
