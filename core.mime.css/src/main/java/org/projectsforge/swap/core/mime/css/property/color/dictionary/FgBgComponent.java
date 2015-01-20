/**
 * Copyright 2012 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr>
 * 
 * This software is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software. If not, see <http://www.gnu.org/licenses/>.
 * 
 * $Id$
 */
package org.projectsforge.swap.core.mime.css.property.color.dictionary;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * The Class FgBgComponent.
 * 
 * @author Sébastien Aupetit
 */
public class FgBgComponent {

  /** The entries. */
  private final List<FgBgEntry> entries;

  /**
   * Instantiates a new fg bg component.
   * 
   * @param entries the entries
   */
  public FgBgComponent(final Set<FgBgEntry> entries) {
    this.entries = new ArrayList<>(entries);
  }

  /**
   * Gets the entries.
   * 
   * @return the entries
   */
  public List<FgBgEntry> getEntries() {
    return entries;
  }

  /**
   * Gets the marginal background weight.
   * 
   * @param backgroundIndex the background index
   * @return the marginal background weight
   */
  public double getMarginalBackgroundWeight(final int backgroundIndex) {
    double sum = 0;
    for (final FgBgEntry entry : entries) {
      if (entry.getBackgroundIndex() == backgroundIndex) {
        sum += entry.getWeight();
      }
    }
    return sum;
  }

  /**
   * Gets the marginal foreground weight.
   * 
   * @param foregroundIndex the foreground index
   * @return the marginal foreground weight
   */
  public double getMarginalForegroundWeight(final int foregroundIndex) {
    double sum = 0;
    for (final FgBgEntry entry : entries) {
      if (entry.getForegroundIndex() == foregroundIndex) {
        sum += entry.getWeight();
      }
    }
    return sum;
  }

  /**
   * Gets the marginal weight.
   * 
   * @return the marginal weight
   */
  public double getMarginalWeight() {
    double sum = 0;
    for (final FgBgEntry entry : entries) {
      sum += entry.getWeight();
    }
    return sum;
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getEntries().toString();
  }
}
