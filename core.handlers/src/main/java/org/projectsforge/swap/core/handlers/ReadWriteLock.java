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
package org.projectsforge.swap.core.handlers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The a reentrant, unlockable ReadWriteLock which allows the promotion of read
 * lock into write lock and write lock into read lock without interblocking.
 */
public class ReadWriteLock {

  /** The reading threads. */
  private final Map<Thread, Integer> readingThreads = new HashMap<Thread, Integer>();

  /** The write accesses. */
  private int writeAccesses = 0;

  /** The write requests. */
  private int writeRequests = 0;

  /** The writing thread. */
  private Thread writingThread = null;

  /** The logger. */
  private final Logger logger;

  /**
   * Instantiates a new read write lock.
   * 
   * @param name the name
   */
  public ReadWriteLock(final String name) {
    logger = LoggerFactory.getLogger(ReadWriteLock.class.getName() + "." + name);
  }

  /**
   * Can grant read access.
   * 
   * @param callingThread the calling thread
   * @return true, if successful
   */
  private boolean canGrantReadAccess(final Thread callingThread) {
    if (isWriter(callingThread)) {
      return true;
    }
    if (hasWriter()) {
      return false;
    }
    if (isReader(callingThread)) {
      return true;
    }
    if (hasWriteRequests()) {
      return false;
    }
    return true;
  }

  /**
   * Can grant write access.
   * 
   * @param callingThread the calling thread
   * @return true, if successful
   */
  private boolean canGrantWriteAccess(final Thread callingThread) {
    if (isOnlyReader(callingThread)) {
      return true;
    }
    if (hasReaders()) {
      return false;
    }
    if (writingThread == null) {
      return true;
    }
    if (!isWriter(callingThread)) {
      return false;
    }
    return true;
  }

  /**
   * Gets the read access count.
   * 
   * @param callingThread the calling thread
   * @return the read access count
   */
  private int getReadAccessCount(final Thread callingThread) {
    final Integer accessCount = readingThreads.get(callingThread);
    if (accessCount == null) {
      return 0;
    }
    return accessCount.intValue();
  }

  /**
   * Checks for readers.
   * 
   * @return true, if successful
   */
  private boolean hasReaders() {
    return readingThreads.size() > 0;
  }

  /**
   * Checks for writer.
   * 
   * @return true, if successful
   */
  private boolean hasWriter() {
    return writingThread != null;
  }

  /**
   * Checks for write requests.
   * 
   * @return true, if successful
   */
  private boolean hasWriteRequests() {
    return this.writeRequests > 0;
  }

  /**
   * Checks if is only reader.
   * 
   * @param callingThread the calling thread
   * @return true, if is only reader
   */
  private boolean isOnlyReader(final Thread callingThread) {
    return readingThreads.size() == 1 && readingThreads.get(callingThread) != null;
  }

  /**
   * Checks if is reader.
   * 
   * @param callingThread the calling thread
   * @return true, if is reader
   */
  private boolean isReader(final Thread callingThread) {
    return readingThreads.get(callingThread) != null;
  }

  /**
   * Checks if is writer.
   * 
   * @param callingThread the calling thread
   * @return true, if is writer
   */
  private boolean isWriter(final Thread callingThread) {
    return writingThread == callingThread;
  }

  /**
   * Lock read.
   * 
   * @throws InterruptedException the interrupted exception
   */
  public synchronized void lockRead() throws InterruptedException {
    final Thread callingThread = Thread.currentThread();
    while (!canGrantReadAccess(callingThread)) {
      wait();
    }

    readingThreads.put(callingThread, (getReadAccessCount(callingThread) + 1));
  }

  /**
   * Lock write.
   * 
   * @throws InterruptedException the interrupted exception
   */
  public synchronized void lockWrite() throws InterruptedException {
    writeRequests++;
    final Thread callingThread = Thread.currentThread();
    while (!canGrantWriteAccess(callingThread)) {
      wait();
    }
    writeRequests--;
    writeAccesses++;
    writingThread = callingThread;
  }

  /**
   * Unlock all pending locks.
   * 
   * @param threads the threads
   */
  public synchronized void unlockAllPendingLocks(final Set<Thread> threads) {
    for (final Thread t : readingThreads.keySet()) {
      if (threads.contains(t)) {
        logger.warn("Read lock currently locked by {}. Forcing unlocking.", t);
        readingThreads.remove(t);
      }
    }
    if (writingThread != null) {
      if (threads.contains(writingThread)) {
        logger.warn("Write lock currentlty locked by {}", writingThread);
        writingThread = null;
      }
    }
    notifyAll();
  }

  /**
   * Unlock all pending locks.
   * 
   * @param threads the threads
   */
  public synchronized void unlockAllPendingLocks(final Thread thread) {
    for (final Thread t : readingThreads.keySet()) {
      if (thread == t) {
        logger.warn("Read lock currently locked by {}. Forcing unlocking.", t);
        readingThreads.remove(t);
      }
    }
    if (writingThread != null) {
      if (thread == writingThread) {
        logger.warn("Write lock currentlty locked by {}", writingThread);
        writingThread = null;
      }
    }
    notifyAll();
  }

  /**
   * Unlock read.
   */
  public synchronized void unlockRead() {
    final Thread callingThread = Thread.currentThread();
    if (!isReader(callingThread)) {
      throw new IllegalMonitorStateException("Calling Thread does not"
          + " hold a read lock on this ReadWriteLock");
    }
    final int accessCount = getReadAccessCount(callingThread);
    if (accessCount == 1) {
      readingThreads.remove(callingThread);
    } else {
      readingThreads.put(callingThread, (accessCount - 1));
    }
    notifyAll();
  }

  /**
   * Unlock write.
   * 
   * @throws InterruptedException the interrupted exception
   */
  public synchronized void unlockWrite() throws InterruptedException {
    if (!isWriter(Thread.currentThread())) {
      throw new IllegalMonitorStateException("Calling Thread does not"
          + " hold the write lock on this ReadWriteLock");
    }

    writeAccesses--;
    if (writeAccesses == 0) {
      writingThread = null;
    }
    notifyAll();
  }
}
