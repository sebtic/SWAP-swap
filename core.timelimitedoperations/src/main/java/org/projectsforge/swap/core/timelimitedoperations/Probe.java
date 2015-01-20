package org.projectsforge.swap.core.timelimitedoperations;

/**
 * The class representing a probe.
 */
public final class Probe {
  final String name;
  final int id;
  final long threshold;
  final long weight;

  final long start;
  long stop;
  long duration;

  boolean interrupted = false;

  Probe(final String name, final int id, final long weight, final long threshold) {
    this.name = name;
    this.id = id;
    this.weight = weight;
    this.threshold = threshold;
    this.duration = 0;
    this.interrupted = false;
    this.start = System.currentTimeMillis();
  }

  /**
   * Should interrupt processing ?
   * 
   * @return true, if successful
   */
  public boolean shouldInterrupt() {
    if (!interrupted) {
      stop = System.currentTimeMillis();
      duration = stop - start;
      if (duration >= threshold) {
        interrupted = true;
      }
    }

    return interrupted;
  }

  void terminate() {
    if (!interrupted) {
      stop = System.currentTimeMillis();
      duration = stop - start;
      interrupted = true;
    }
  }

  @Override
  public String toString() {
    return name + '(' + id + ")[" + duration + "/" + threshold + ',' + weight + ',' + interrupted
        + ']';
  }
}
