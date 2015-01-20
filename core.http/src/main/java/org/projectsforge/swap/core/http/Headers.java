/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr> This file
 * is part of SHS. SWAP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version. SWAP P is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with SHS. If not, see
 * <http://www.gnu.org/licenses/>. $Id: Headers.java 98 2011-11-24 12:10:32Z
 * sebtic $
 */
package org.projectsforge.swap.core.http;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * A class to manage HTTP headers.
 * 
 * @author Sébastien Aupetit
 */
public class Headers implements Iterable<Header> {

  /** The headers. */
  private final List<Header> headers = new ArrayList<>();

  /**
   * Adds the.
   * 
   * @param name the name
   * @param value the value
   */
  public void add(final String name, final String value) {
    headers.add(new Header(name, value));
  }

  /**
   * Clear.
   */
  public void clear() {
    headers.clear();
  }

  /**
   * Copy.
   * 
   * @param other the other
   */
  public void copy(final Headers other) {
    headers.clear();
    for (final Header header : other.headers) {
      add(header.getName(), header.getValue());
    }
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj instanceof Headers) {
      final Headers other = (Headers) obj;
      return other.headers.containsAll(headers) && headers.containsAll(other.headers);
    }

    return false;
  }

  /**
   * Gets the first.
   * 
   * @param name the name
   * @return the first
   */
  public Header getFirst(final String name) {
    for (final Header header : headers) {
      if (name.equalsIgnoreCase(header.getName())) {
        return header;
      }
    }
    return null;
  }

  /**
   * Gets the first value.
   * 
   * @param name the name
   * @return the first value
   */
  public String getFirstValue(final String name) {
    for (final Header header : headers) {
      if (name.equalsIgnoreCase(header.getName())) {
        return header.getValue();
      }
    }
    return null;
  }

  /**
   * Gets the headers.
   * 
   * @return the headers
   */
  public List<Header> getHeaders() {
    return headers;
  }

  /**
   * Gets the headers.
   * 
   * @param name the name
   * @return the headers
   */
  public Collection<Header> getHeaders(final String name) {
    return new AbstractCollection<Header>() {
      private int size = -1;

      @Override
      public Iterator<Header> iterator() {
        return new Iterator<Header>() {
          private int cur = indexOf(name, 0);

          private int old = -1;

          @Override
          public boolean hasNext() {
            return cur != -1;
          }

          @Override
          public Header next() {
            final Header header = headers.get(cur);
            old = cur;
            cur = indexOf(name, cur + 1);
            return header;
          }

          @Override
          public void remove() {
            headers.remove(old);
            old = -1;
            if (cur != -1) {
              cur = cur - 1;
            }
          }
        };
      }

      @Override
      public int size() {
        if (size == -1) {
          size = 0;
          for (final Header header : headers) {
            if (name.equalsIgnoreCase(header.getName())) {
              size++;
            }
          }
        }
        return size;
      }
    };
  }

  /**
   * Gets the names.
   * 
   * @return the names
   */
  public Set<String> getNames() {
    final Set<String> names = new TreeSet<>();
    for (final Header header : headers) {
      names.add(header.getName());
    }
    return names;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    int sum = 0;
    for (final Header header : headers) {
      sum += header.hashCode();
    }
    return sum;
  }

  /**
   * Checks for header.
   * 
   * @param name the name
   * @return true, if successful
   */
  public boolean hasHeader(final String name) {
    return getFirst(name) != null;
  }

  /**
   * Index of.
   * 
   * @param name the name
   * @param start the start
   * @return the int
   */
  public int indexOf(final String name, final int start) {
    for (int i = start; i < headers.size(); ++i) {
      if (name.equalsIgnoreCase(headers.get(i).getName())) {
        return i;
      }
    }
    return -1;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Iterable#iterator()
   */
  @Override
  public Iterator<Header> iterator() {
    return headers.iterator();
  }

  /**
   * Removes the all.
   * 
   * @param names the names
   */
  public void removeAll(final Collection<String> names) {
    for (final String name : names) {
      removeAll(name);
    }
  }

  /**
   * Removes the all.
   * 
   * @param name the name
   */
  public void removeAll(final String name) {
    final Iterator<Header> it = headers.iterator();

    while (it.hasNext()) {
      if (name.equalsIgnoreCase(it.next().getName())) {
        it.remove();
      }
    }
  }

  /**
   * Removes the first.
   * 
   * @param name the name
   */
  public void removeFirst(final String name) {
    final Iterator<Header> it = headers.iterator();

    while (it.hasNext()) {
      if (name.equalsIgnoreCase(it.next().getName())) {
        it.remove();
        break;
      }
    }
  }

  /**
   * Sets the.
   * 
   * @param name the name
   * @param value the value
   */
  public void set(final String name, final String value) {
    removeAll(name);
    add(name, value);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    for (final Header header : headers) {
      sb.append(header).append('\n');
    }
    return sb.toString();
  }
}
