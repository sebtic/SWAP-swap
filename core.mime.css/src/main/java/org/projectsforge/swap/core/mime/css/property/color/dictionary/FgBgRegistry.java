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
 * <http://www.gnu.org/licenses/>. \$Id$
 */
package org.projectsforge.swap.core.mime.css.property.color.dictionary;

import gnu.trove.list.array.TDoubleArrayList;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.math3.util.FastMath;

/**
 * This class manages a registry of weighted color index pairs and fast
 * retrieval of information.
 * 
 * @author Sébastien Aupetit
 */
public class FgBgRegistry {

  /** The weights (foreground,background,weight). */
  private final List<TDoubleArrayList> weights = new ArrayList<>();

  /** The components. */
  private List<FgBgComponent> components;

  /** The combined component. */
  private FgBgComponent combinedComponent;

  /**
   * Optimize.
   */
  private synchronized void computeComponents() {
    if (components == null || combinedComponent == null) {

      // max size
      int maxSize = weights.size();
      for (final TDoubleArrayList backgrounds : weights) {
        maxSize = FastMath.max(maxSize, backgrounds.size());
      }

      // compute components
      components = new ArrayList<FgBgComponent>();
      final BitSet[] matrix = new BitSet[maxSize];
      for (int i = 0; i < maxSize; ++i) {
        matrix[i] = new BitSet(maxSize);
      }

      // compute the matrix of connected colors
      for (int i = weights.size() - 1; i >= 0; --i) {
        for (int j = weights.get(i).size() - 1; j >= 0; --j) {
          if (weights.get(i).get(j) > 0) {
            matrix[i].set(j);
            matrix[j].set(i);
          }
        }
      }

      // detect components
      final BitSet done = new BitSet(maxSize);
      int next;
      while ((next = done.nextClearBit(0)) != -1 && next < maxSize) {
        // mark the color as handled
        done.set(next);
        // Initially the component is empty
        final BitSet component = new BitSet(maxSize);
        // The toHandle list must contain the first entry of the component
        final BitSet toHandle = new BitSet(maxSize);
        toHandle.set(next);

        // While it remains something to handle
        int current;
        while ((current = toHandle.nextSetBit(0)) != -1) {
          // We handle the color
          toHandle.clear(current);
          // The handled color is added to the component
          component.set(current);
          // Connected colors must be handled
          toHandle.or(matrix[current]);
          // We remove already handled colors
          toHandle.andNot(component);
        }

        // on enregistre la composante
        final Set<FgBgEntry> result = new HashSet<FgBgEntry>();
        for (int id = component.nextSetBit(0); id >= 0; id = component.nextSetBit(id + 1)) {
          // id is a valid foreground
          if (id < weights.size()) {
            for (int j = weights.get(id).size() - 1; j >= 0; --j) {
              if (weights.get(id).get(j) > 0) {
                result.add(new FgBgEntry(id, j, weights.get(id).get(j)));
              }
            }
          }

          // id is a background
          for (int i = weights.size() - 1; i >= 0; --i) {
            if (id < weights.get(i).size() && weights.get(i).get(id) > 0) {
              result.add(new FgBgEntry(i, id, weights.get(i).get(id)));
            }
          }
        }
        components.add(new FgBgComponent(result));
        // mark used elements as done
        done.or(component);
      }

      final Set<FgBgEntry> result = new HashSet<FgBgEntry>();
      for (int i = weights.size() - 1; i >= 0; --i) {
        for (int j = weights.get(i).size() - 1; j >= 0; --j) {
          if (weights.get(i).get(j) > 0) {
            result.add(new FgBgEntry(i, j, weights.get(i).get(j)));
          }
        }
      }
      combinedComponent = new FgBgComponent(result);
    }
  }

  /**
   * Gets the combined component.
   * 
   * @return the combined component
   */
  public FgBgComponent getCombinedComponent() {
    computeComponents();
    return combinedComponent;
  }

  /**
   * Gets the components.
   * 
   * @return the components
   */
  public List<FgBgComponent> getComponents() {
    computeComponents();
    return components;
  }

  /**
   * Register a pair of index with the given weight. If the pair already exists,
   * then the weight is added to the existing pair.
   * 
   * @param foregroundIndex the foreground index
   * @param backgroundIndex the background index
   * @param weight the weight
   */
  public synchronized void register(final int foregroundIndex, final int backgroundIndex,
      final double weight) {
    components = null;
    combinedComponent = null;

    while (weights.size() <= foregroundIndex) {
      weights.add(new TDoubleArrayList());
    }

    final TDoubleArrayList backgrounds = weights.get(foregroundIndex);
    while (backgrounds.size() <= backgroundIndex) {
      backgrounds.add(0);
    }

    backgrounds.set(backgroundIndex, backgrounds.get(backgroundIndex) + weight);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return weights.toString();
  }

}
