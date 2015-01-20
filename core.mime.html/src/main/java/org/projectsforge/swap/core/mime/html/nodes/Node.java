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
 * <http://www.gnu.org/licenses/>. $Id: Node.java 125 2012-04-04 14:59:59Z
 * sebtic $
 */
package org.projectsforge.swap.core.mime.html.nodes;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.projectsforge.utils.icasestring.ICaseString;
import org.projectsforge.utils.icasestring.ICaseStringKeyCollections;

/**
 * The base class of each node of the HTML tree.
 * 
 * @author Sébastien Aupetit
 */
public abstract class Node {

  /** The parent. */
  private Node parent;

  /** The first child. */
  private Node firstChild;

  /** The last child. */
  private Node lastChild;

  /** The previous. */
  private Node previous;

  /** The next. */
  private Node next;

  /** The annotations. */
  private final Map<ICaseString, Object> annotations = ICaseStringKeyCollections
      .caseInsensitiveMap(new TreeMap<ICaseString, Object>());

  /**
   * Adds the child at end.
   * 
   * @param child the child
   */
  public void addChildAtEnd(final Node child) {
    if (child.parent != null) {
      throw new IllegalArgumentException("Child already have a parent node");
    }
    if (child.previous != null) {
      throw new IllegalArgumentException("Child already have a previous node");
    }
    if (child.next != null) {
      throw new IllegalArgumentException("Child already have a next node");
    }

    if (lastChild == null) {
      // there is no children
      firstChild = child;
      lastChild = child;
      child.parent = this;
    } else {
      // chain at end
      child.previous = lastChild;
      lastChild.next = child;
      // chain with parent (this node)
      child.parent = this;
      lastChild = child;
    }
  }

  /**
   * Adds the child at start.
   * 
   * @param child the child
   */
  public void addChildAtStart(final Node child) {
    if (child.parent != null) {
      throw new IllegalArgumentException("Child already have a parent node");
    }
    if (child.previous != null) {
      throw new IllegalArgumentException("Child already have a previous node");
    }
    if (child.next != null) {
      throw new IllegalArgumentException("Child already have a next node");
    }

    if (firstChild == null) {
      firstChild = child;
      lastChild = child;
      child.parent = this;
    } else {
      // chain at start
      child.next = firstChild;
      firstChild.previous = child;;
      // chain with parent (this node)
      child.parent = this;
      firstChild = child;
    }
  }

  /**
   * Adds the node after.
   * 
   * @param node the node
   */
  public void addNodeAfter(final Node node) {
    if (node.parent != null) {
      throw new IllegalArgumentException("Child already have a parent node");
    }
    if (node.previous != null) {
      throw new IllegalArgumentException("Child already have a previous node");
    }
    if (node.next != null) {
      throw new IllegalArgumentException("Child already have a next node");
    }

    node.next = next;
    node.previous = this;
    if (next != null) {
      next.previous = node;
    }
    next = node;

    node.parent = parent;
    if (node.next == null) {
      parent.lastChild = node;
    }
  }

  /**
   * Adds the node before.
   * 
   * @param node the node
   */
  public void addNodeBefore(final Node node) {
    if (node.parent != null) {
      throw new IllegalArgumentException("Child already have a parent node");
    }
    if (node.previous != null) {
      throw new IllegalArgumentException("Child already have a previous node");
    }
    if (node.next != null) {
      throw new IllegalArgumentException("Child already have a next node");
    }

    if (previous != null) {
      // chain node before
      previous.next = node;
      node.next = this;
      node.previous = previous;
      previous = node;

      // chain with parent
      node.parent = parent;
      if (node.previous == null) {
        parent.firstChild = node;
      }
    } else {
      // add in first position
      node.next = this;
      previous = node;
      node.parent = parent;
      parent.firstChild = node;
    }
  }

  /**
   * Detach from parent.
   */
  public void detachFromParent() {
    // remove from parent children list
    if (parent != null) {
      if (parent.firstChild == this) {
        parent.firstChild = next;
      }
      if (parent.lastChild == this) {
        parent.lastChild = previous;
      }
    }

    if (previous != null) {
      previous.next = next;
    }
    if (next != null) {
      next.previous = previous;
    }

    next = null;
    previous = null;
    parent = null;
  }

  /**
   * Gets the annotation.
   * 
   * @param <T> the generic type
   * @param key the key
   * @param contentClass the content class
   * @return the annotation
   */
  public <T> T getAnnotation(final ICaseString key, final Class<T> contentClass) {
    return contentClass.cast(annotations.get(key));
  }

  /**
   * Gets the annotation recursively.
   * 
   * @param <T> the generic type
   * @param key the key
   * @param contentClass the content class
   * @return the annotation recursively
   */
  public <T> T getAnnotationRecursively(final ICaseString key, final Class<T> contentClass) {
    Node current = this;
    while (current != null) {
      final Object result = annotations.get(key);
      if (result != null) {
        return contentClass.cast(result);
      } else {
        current = current.parent;
      }
    }
    return null;
  }

  /**
   * Gets the annotation values recursively.
   * 
   * @param <T> the generic type
   * @param key the key
   * @param contentClass the content class
   * @return the annotation values (last is for root)
   */
  public <T> List<T> getAnnotationRecursivelyToRoot(final ICaseString key,
      final Class<T> contentClass) {
    final List<T> result = new ArrayList<>();
    Node current = this;
    while (current != null) {
      final Object value = annotations.get(key);
      if (value != null) {
        result.add(contentClass.cast(result));
      } else {
        current = current.parent;
      }
    }
    return result;
  }

  /**
   * Gets the children collection.
   * 
   * @return the children collection
   */
  public Collection<Node> getChildrenCollection() {
    return new AbstractCollection<Node>() {
      @Override
      public boolean add(final Node node) {
        addChildAtEnd(node);
        return true;
      }

      @Override
      public boolean isEmpty() {
        return getFirstChild() == null;
      }

      @Override
      public Iterator<Node> iterator() {
        return new Iterator<Node>() {
          private Node current = getFirstChild();

          @Override
          public boolean hasNext() {
            return current != null;
          }

          @Override
          public Node next() {
            final Node old = current;
            current = current.getNext();
            return old;
          }

          @Override
          public void remove() {
            final Node old = current;
            current = current.getNext();
            old.detachFromParent();
          }
        };
      }

      @Override
      public int size() {
        int count = 0;
        Node node = getFirstChild();
        while (node != null) {
          count++;
          node = node.getNext();
        }
        return count;
      }
    };
  }

  /**
   * Gets the children collection.
   * 
   * @param <T> the generic type
   * @param selector the selector
   * @return the children collection
   */
  public <T extends Node> Collection<T> getChildrenCollection(final Class<T> selector) {
    return new AbstractCollection<T>() {
      @Override
      public boolean add(final T node) {
        addChildAtEnd(node);
        return true;
      }

      @Override
      public boolean isEmpty() {
        return getFirstChild(selector) == null;
      }

      @Override
      public Iterator<T> iterator() {
        return new Iterator<T>() {
          T current = getFirstChild(selector);

          @Override
          public boolean hasNext() {
            return current != null;
          }

          @Override
          public T next() {
            final T old = current;
            current = current.getNext(selector);
            return old;
          }

          @Override
          public void remove() {
            final T old = current;
            current = current.getNext(selector);
            old.detachFromParent();
          }
        };
      }

      @Override
      public int size() {
        int count = 0;
        T node = getFirstChild(selector);
        while (node != null) {
          count++;
          node = node.getNext(selector);
        }
        return count;
      }
    };
  }

  /**
   * Gets the children to leaves.
   * 
   * @return the children to leaves
   */
  public List<Node> getChildrenToLeaves() {
    final List<Node> result = new ArrayList<>();
    Node node = getFirstChild();
    while (node != null) {
      result.add(node);
      result.addAll(node.getChildrenToLeaves());
      node = node.getNext();
    }
    return result;
  }

  /**
   * Gets the children to leaves.
   * 
   * @param <T> the generic type
   * @param selector the selector
   * @return the children to leaves
   */
  public <T extends Node> List<T> getChildrenToLeaves(final Class<T> selector) {
    final List<T> result = new ArrayList<>();
    Node node = getFirstChild();
    while (node != null) {
      if (selector.isInstance(node)) {
        result.add(selector.cast(node));
      }
      result.addAll(node.getChildrenToLeaves(selector));
      node = node.getNext();
    }
    return result;
  }

  /**
   * Gets the first child.
   * 
   * @return the first child
   */
  public Node getFirstChild() {
    return firstChild;
  }

  /**
   * Gets the first child.
   * 
   * @param <T> the generic type
   * @param selector the selector
   * @return the first child
   */
  public <T extends Node> T getFirstChild(final Class<T> selector) {
    Node node = firstChild;
    while (node != null) {
      if (selector.isInstance(node)) {
        return selector.cast(node);
      } else {
        node = node.next;
      }
    }
    return null;
  }

  /**
   * Gets the first children to leaves.
   * 
   * @param <T> the generic type
   * @param selector the selector
   * @return the first children to leaves
   */
  public <T extends Node> T getFirstChildrenToLeaves(final Class<T> selector) {
    Node node = getFirstChild();
    while (node != null) {
      if (selector.isInstance(node)) {
        return selector.cast(node);
      }
      final T n = node.getFirstChildrenToLeaves(selector);
      if (n != null) {
        return n;
      }
      node = node.getNext();
    }
    return null;
  }

  /**
   * Gets the last child.
   * 
   * @return the last child
   */
  public Node getLastChild() {
    return lastChild;
  }

  /**
   * Gets the last child.
   * 
   * @param <T> the generic type
   * @param selector the selector
   * @return the last child
   */
  public <T extends Node> Node getLastChild(final Class<T> selector) {
    Node node = lastChild;
    while (node != null) {
      if (selector.isInstance(node)) {
        return selector.cast(node);
      } else {
        node = node.previous;
      }
    }
    return null;
  }

  /**
   * Gets the next.
   * 
   * @return the next
   */
  public Node getNext() {
    return next;
  }

  /**
   * Gets the next.
   * 
   * @param <T> the generic type
   * @param selector the selector
   * @return the next
   */
  public <T extends Node> T getNext(final Class<T> selector) {
    Node node = next;
    while (node != null) {
      if (selector.isInstance(node)) {
        return selector.cast(node);
      } else {
        node = node.next;
      }
    }
    return null;
  }

  /**
   * Gets the parent.
   * 
   * @return the parent
   */
  public Node getParent() {
    return parent;
  }

  /**
   * Gets the parent.
   * 
   * @param <T> the generic type
   * @param selector the selector
   * @return the parent
   */
  public <T extends Node> T getParent(final Class<T> selector) {
    Node node = parent;
    while (node != null) {
      if (selector.isInstance(node)) {
        return selector.cast(node);
      } else {
        node = node.parent;
      }
    }
    return null;
  }

  /**
   * Gets the parents to root.
   * 
   * @return the parents to root
   */
  public List<Node> getParentsToRoot() {
    final List<Node> result = new ArrayList<>();
    Node node = parent;
    while (node != null) {
      result.add(node);
      node = node.parent;
    }
    return result;
  }

  /**
   * Gets the parents to root.
   * 
   * @param <T> the generic type
   * @param selector the selector
   * @return the parents to root
   */
  public <T extends Node> List<T> getParentsToRoot(final Class<T> selector) {
    final List<T> result = new ArrayList<>();
    Node node = parent;
    while (node != null) {
      if (selector.isInstance(node)) {
        result.add(selector.cast(node));
      } else {
        node = node.parent;
      }
    }
    return result;
  }

  /**
   * Gets the previous.
   * 
   * @return the previous
   */
  public Node getPrevious() {
    return previous;
  }

  /**
   * Gets the previous.
   * 
   * @param <T> the generic type
   * @param selector the selector
   * @return the previous
   */
  public <T extends Node> T getPrevious(final Class<T> selector) {
    Node node = next;
    while (node != null) {
      if (selector.isInstance(node)) {
        return selector.cast(node);
      } else {
        node = node.next;
      }
    }
    return null;
  }

  /**
   * Gets the root node.
   * 
   * @return the root node
   */
  public Node getRootNode() {
    Node current = this;
    while (current.parent != null) {
      current = current.parent;
    }
    return current;
  }

  /**
   * Checks for children.
   * 
   * @return true, if successful
   */
  public boolean hasChildren() {
    return firstChild != null;
  }

  /**
   * Removes the annotation.
   * 
   * @param key the key
   */
  public void removeAnnotation(final ICaseString key) {
    annotations.remove(key);
  }

  /**
   * Sets the annotation.
   * 
   * @param key the key
   * @param value the value
   */
  public void setAnnotation(final ICaseString key, final Object value) {
    annotations.put(key, value);
  }
}
