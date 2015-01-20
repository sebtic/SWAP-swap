/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr>
 * 
 * This file is part of SWAP.
 * 
 * SWAP is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * SWAP is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with SWAP. If not, see <http://www.gnu.org/licenses/>.
 * 
 * $Id$
 */
package org.projectsforge.swap.core.handlers;

/**
 * The class that manages resource access.
 * 
 * @param <TData>
 *          the generic type
 */
public class Resource<TData> {

  /** The read write lock. */
  final ReadWriteLock readWriteLock;

  /** The value. */
  TData value;

  /** The name. */
  private final String name;

  /**
   * Instantiates a new resource.
   * 
   * @param name
   *          the name
   */
  public Resource(final String name) {
    this.name = name;
    readWriteLock = new ReadWriteLock(name);
    this.value = null;
  }

  /**
   * The Constructor.
   * 
   * @param name
   *          the name
   * @param value
   *          the value
   */
  public Resource(final String name, final TData value) {
    this.name = name;
    readWriteLock = new ReadWriteLock(name);
    this.value = value;
  }

  /**
   * Gets the.
   * 
   * @return the t data
   * @throws InterruptedException
   *           the interrupted exception
   */
  public TData get() throws InterruptedException {
    readWriteLock.lockRead();
    try {
      return this.value;
    } finally {
      readWriteLock.unlockRead();
    }
  }

  /**
   * Gets the name.
   * 
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Lock read.
   * 
   * @throws InterruptedException
   *           the interrupted exception
   */
  public void lockRead() throws InterruptedException {
    readWriteLock.lockRead();
  }

  /**
   * Lock write.
   * 
   * @throws InterruptedException
   *           the interrupted exception
   */
  public void lockWrite() throws InterruptedException {
    readWriteLock.lockWrite();
  }

  /**
   * Sets the.
   * 
   * @param value
   *          the value
   * @throws InterruptedException
   *           the interrupted exception
   */
  public void set(final TData value) throws InterruptedException {
    readWriteLock.lockWrite();
    try {
      this.value = value;
    } finally {
      readWriteLock.unlockWrite();
    }
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return name + "[" + value + "]";
  }

  /**
   * Unlock read.
   * 
   * @throws InterruptedException
   *           the interrupted exception
   */
  public void unlockRead() throws InterruptedException {
    readWriteLock.unlockRead();
  }

  /**
   * Unlock write.
   * 
   * @throws InterruptedException
   *           the interrupted exception
   */
  public void unlockWrite() throws InterruptedException {
    readWriteLock.unlockWrite();
  }

}
