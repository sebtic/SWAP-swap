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
 * \$Id$
 */
package org.projectsforge.swap.core.mime.css.resolver.color.model;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.projectsforge.swap.core.mime.css.resolver.color.SRGBColor;

/**
 * This class manages a registry of weighted color index pairs and fast
 * retrieval of information.
 */
public class FgBgRegistry {

  /**
   * The Class Entry.
   */
  public static abstract class Entry {

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
      if (obj instanceof Entry) {
        final Entry entry = (Entry) obj;
        return getForegroundIndex() == entry.getForegroundIndex()
            && getBackgroundIndex() == entry.getBackgroundIndex();
      }
      return false;
    }

    /**
     * Gets the background color.
     * 
     * @param colorDictionary
     *          the color dictionary
     * @return the background color
     */
    public SRGBColor getBackgroundColor(final ColorDictionary colorDictionary) {
      return colorDictionary.getColor(getBackgroundIndex());
    }

    /**
     * Gets the background entry.
     * 
     * @param colorDictionary
     *          the color dictionary
     * @return the background entry
     */
    public ColorDictionary.Entry getBackgroundEntry(final ColorDictionary colorDictionary) {
      return colorDictionary.getEntry(getBackgroundIndex());
    }

    /**
     * Gets the background index.
     * 
     * @return the background index
     */
    public abstract int getBackgroundIndex();

    /**
     * Gets the foreground color.
     * 
     * @param colorDictionary
     *          the color dictionary
     * @return the foreground color
     */
    public SRGBColor getForegroundColor(final ColorDictionary colorDictionary) {
      return colorDictionary.getColor(getForegroundIndex());
    }

    /**
     * Gets the foreground entry.
     * 
     * @param colorDictionary
     *          the color dictionary
     * @return the foreground entry
     */
    public ColorDictionary.Entry getForegroundEntry(final ColorDictionary colorDictionary) {
      return colorDictionary.getEntry(getForegroundIndex());
    }

    /**
     * Gets the foreground index.
     * 
     * @return the foreground index
     */
    public abstract int getForegroundIndex();

    /**
     * Gets the weight.
     * 
     * @return the weight
     */
    public abstract double getWeight();

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
      return getForegroundIndex() * 31 + getBackgroundIndex();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return "[" + getForegroundIndex() + ":" + getBackgroundIndex() + "=>" + getWeight() + "]";
    }
  }

  /**
   * The Class SimpleEntry.
   */
  private static class SimpleEntry extends Entry {

    /** The foreground index. */
    private final int foregroundIndex;

    /** The background index. */
    private final int backgroundIndex;

    /** The weight. */
    private double weight;

    /**
     * Instantiates a new simple entry.
     * 
     * @param foregroundIndex
     *          the foreground index
     * @param backgroundIndex
     *          the background index
     */
    public SimpleEntry(final int foregroundIndex, final int backgroundIndex) {
      this.foregroundIndex = foregroundIndex;
      this.backgroundIndex = backgroundIndex;
      this.weight = 0.0;
    }

    /**
     * Adds the weight.
     * 
     * @param weight
     *          the weight
     */
    public void addWeight(final double weight) {
      this.weight += weight;
    }

    /*
     * (non-Javadoc)
     * @see org.projectsforge.swap.plugins.cssimprover.model.FgBgRegistry.Entry#
     * getBackgroundIndex()
     */
    @Override
    public int getBackgroundIndex() {
      return backgroundIndex;
    }

    /*
     * (non-Javadoc)
     * @see org.projectsforge.swap.plugins.cssimprover.model.FgBgRegistry.Entry#
     * getForegroundIndex()
     */
    @Override
    public int getForegroundIndex() {
      return foregroundIndex;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.projectsforge.swap.plugins.cssimprover.model.FgBgRegistry.Entry#getWeight
     * ()
     */
    @Override
    public double getWeight() {
      return weight;
    }
  }

  /** The entries as a self map. */
  private Map<Entry, SimpleEntry> mapentries = new HashMap<Entry, SimpleEntry>();

  /** The optimized. */
  private boolean optimized = false;

  /** The entries. */
  private List<Entry> entries;

  /** The max foreground index. */
  private int maxForegroundIndex = -1;

  /** The max background index. */
  private int maxBackgroundIndex = -1;

  /** The cached weight sum by background. */
  private Double[] cachedWeightSumByBackground;

  /** The cached weight sum by foreground. */
  private Double[] cachedWeightSumByForeground;

  /** The cached weight sum. */
  private Double cachedWeightSum;

  /** The components. */
  private List<List<Integer>> components;

  /**
   * Compute connexe components.
   * 
   * @return the list< list< integer>>
   */
  public List<List<Integer>> computeConnexeComponents() {
    optimize();

    if (components == null) {
      components = new ArrayList<List<Integer>>();

      final int max = Math.max(maxForegroundIndex, maxBackgroundIndex);
      final BitSet[] matrix = new BitSet[max];
      for (int i = 0; i < max; ++i) {
        matrix[i] = new BitSet(max);
      }

      // compute the matrix of linked indexes (undirected)
      for (final Entry entry : entries) {
        matrix[entry.getForegroundIndex()].set(entry.getBackgroundIndex());
        matrix[entry.getBackgroundIndex()].set(entry.getForegroundIndex());
      }

      // detect components
      final BitSet done = new BitSet(max);
      int next;
      while ((next = done.nextClearBit(0)) != -1) {
        // mark as done
        done.set(next);
        // la composante est vide
        final BitSet component = new BitSet(max);
        // la liste a traiter contient le premier indice
        final BitSet toHandle = new BitSet(max);
        toHandle.set(next);

        // tant qu'il y a des indices à traiter
        int current;
        while ((current = toHandle.nextSetBit(0)) != -1) {
          // on retire l'indice de la liste a traiter
          toHandle.clear(current);
          // on ajoute l'indice à la composante
          component.set(current); // ajoute a la composante
          // on ajoute les incides joignables dans la liste a traiter
          toHandle.or(matrix[current]);
          // on retire de la liste a traiter ceux qui sont dejà dans la
          // composante
          // (donc déjà traité)
          toHandle.andNot(component);
        }

        // on enregistre la composante
        final List<Integer> result = new ArrayList<Integer>(component.cardinality()); // slight
                                                                                      // memory
                                                                                      // optimization
        int resultid = 0;
        for (int id = component.nextSetBit(0); id >= 0; id = component.nextSetBit(id + 1)) {
          result.add(resultid++);
        }
        components.add(result);
      }
    }
    return components;
  }

  /**
   * Gets the entries.
   * 
   * @return the entries
   */
  public List<Entry> getEntries() {
    optimize();
    return entries;
  }

  /**
   * Gets the sum of weights of the entries.
   * 
   * @return the sum
   */
  public double getWeightSum() {
    optimize();
    if (cachedWeightSum == null) {
      double sum = 0;
      for (final Entry entry : entries) {
        sum += entry.getWeight();
      }
      cachedWeightSum = sum;
      return sum;
    } else {
      return cachedWeightSum;
    }
  }

  /**
   * Gets the sum of weights of background entries.
   * 
   * @param backgroundIndex
   *          the background index
   * @return the weight sum by background
   */
  public double getWeightSumByBackground(final int backgroundIndex) {
    optimize();
    if (cachedWeightSumByBackground[backgroundIndex] == null) {
      double sum = 0.0;

      for (final Entry entry : entries) {
        if (entry.getBackgroundIndex() == backgroundIndex) {
          sum += entry.getWeight();
        }
      }
      cachedWeightSumByBackground[backgroundIndex] = sum;
      return sum;
    } else {
      return cachedWeightSumByBackground[backgroundIndex];
    }
  }

  /**
   * Gets the sum of weights of foreground entries.
   * 
   * @param foregroundIndex
   *          the foreground index
   * @return the weight sum by foreground
   */
  public double getWeightSumByForeground(final int foregroundIndex) {
    optimize();
    if (cachedWeightSumByForeground[foregroundIndex] == null) {
      double sum = 0.0;

      for (final Entry entry : entries) {
        if (entry.getForegroundIndex() == foregroundIndex) {
          sum += entry.getWeight();
        }
      }
      cachedWeightSumByForeground[foregroundIndex] = sum;
      return sum;
    } else {
      return cachedWeightSumByForeground[foregroundIndex];
    }
  }

  /**
   * Optimize.
   */
  private synchronized void optimize() {
    if (!optimized) {
      entries = new ArrayList<Entry>(mapentries.keySet());
      mapentries = null; // allow gc to do its work on useless data

      cachedWeightSumByBackground = new Double[maxBackgroundIndex + 1];
      cachedWeightSumByForeground = new Double[maxForegroundIndex + 1];
      optimized = true;
    }
  }

  /**
   * Register a pair of index with the given weight. If the pair already exists,
   * then the weight is added to the existing pair.
   * 
   * @param foregroundIndex
   *          the foreground index
   * @param backgroundIndex
   *          the background index
   * @param weight
   *          the weight
   */
  public void register(final int foregroundIndex, final int backgroundIndex, final double weight) {
    if (optimized) {
      throw new IllegalStateException(
          "The registry has already been optimized for retreival. Further registration are forbidden");
    }

    final SimpleEntry entry = new SimpleEntry(foregroundIndex, backgroundIndex);

    SimpleEntry current = mapentries.get(entry);
    if (current == null) {
      current = entry;
      mapentries.put(current, current);
    }
    current.addWeight(weight);

    maxForegroundIndex = Math.max(maxForegroundIndex, foregroundIndex);
    maxBackgroundIndex = Math.max(maxBackgroundIndex, backgroundIndex);
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getEntries().toString();
  }

}
