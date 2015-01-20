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
package org.projectsforge.swap.core.mime.css.resolver;

import org.projectsforge.swap.core.mime.html.nodes.elements.AbstractElement;

/**
 * The class used to store the current state of an {@link AbstractElement}. The
 * states are optimized for fast operation and instances are reused to be kind
 * with the garbage collector.
 */
public class State {
  /** The Constant VISITED_MASK. */
  private static final int VISITED_MASK = 1;

  /** The Constant HOVER_MASK. */
  private static final int HOVER_MASK = 2;

  /** The Constant FOCUS_MASK. */
  private static final int FOCUS_MASK = 4;

  /** The Constant ACTIVE_MASK. */
  private static final int ACTIVE_MASK = 8;

  /** The Constant MAX_VALUE. */
  public static final int MAX_VALUE = State.ACTIVE_MASK + State.FOCUS_MASK + State.HOVER_MASK
      + State.VISITED_MASK;

  /** The active. */
  public final boolean active;

  /** The focused. */
  public final boolean focused;

  /** The hovered. */
  public final boolean hovered;

  /** The visited. */
  public final boolean visited;

  /** The hash. */
  private final int hash;

  // reuse states
  /** The Constant states. */
  private static final State[] states;

  static {
    states = new State[State.MAX_VALUE + 1];
    for (int value = 0; value <= State.MAX_VALUE; ++value) {
      final boolean visited = (value & State.VISITED_MASK) != 0;
      final boolean hovered = (value & State.HOVER_MASK) != 0;
      final boolean focused = (value & State.FOCUS_MASK) != 0;
      final boolean active = (value & State.ACTIVE_MASK) != 0;
      State.states[value] = new State(active, focused, hovered, visited, value);
    }
  }

  /**
   * Gets the state.
   * 
   * @param value
   *          the value
   * @return the state
   */
  public static State getState(final int value) {
    return State.states[value];
  }

  /**
   * Instantiates a new state.
   * 
   * @param active
   *          the active
   * @param focused
   *          the focused
   * @param hovered
   *          the hovered
   * @param visited
   *          the visited
   * @param hash
   *          the hash
   */
  private State(final boolean active, final boolean focused, final boolean hovered,
      final boolean visited, final int hash) {
    this.active = active;
    this.focused = focused;
    this.hovered = hovered;
    this.visited = visited;
    this.hash = hash;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (obj instanceof State) {
      final State other = (State) obj;
      return active == other.active && focused == other.focused && hovered == other.hovered
          && visited == other.visited;
    } else {
      return false;
    }
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    return hash;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "State [active=" + active + ", focused=" + focused + ", hovered=" + hovered
        + ", visited=" + visited + "]";
  }

}
