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
package org.projectsforge.swap.core.timelimitedoperations;

import gnu.trove.list.array.TIntArrayList;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class that manage a set of probes.
 */
public class ProbeManager {

  /** The probes by id. */
  final List<Probe> probesById = new ArrayList<>();

  /** The probes by name. */
  final Map<String, Integer> probesByName = new HashMap<>();

  /** The Constant logger. */
  private static final Logger logger = LoggerFactory.getLogger(ProbeManager.class);
  /** The start time. */
  private final long startTime;

  /** The probe parameters manager. */
  private final ProbeParametersManager probeParametersManager;

  /**
   * Instantiates a new probe manager.
   * 
   * @param probeParametersManager
   *          the probe parameters manager
   */
  ProbeManager(final ProbeParametersManager probeParametersManager) {
    this.probeParametersManager = probeParametersManager;
    this.startTime = System.currentTimeMillis();
  }

  /**
   * Compute pathes from successors relationship.
   * 
   * @param path
   *          the path
   * @param successors
   *          the successors
   * @param pathes
   *          the pathes
   */
  private void computePathes(final TIntArrayList path, final BitSet[] successors,
      final List<TIntArrayList> pathes) {
    final BitSet successor = successors[path.get(path.size() - 1)];

    if (successor.isEmpty()) {
      pathes.add(new TIntArrayList(path));
    } else {
      for (int succ = successor.nextSetBit(0); succ >= 0; succ = successor.nextSetBit(succ + 1)) {
        path.add(succ);
        computePathes(path, successors, pathes);
        path.removeAt(path.size() - 1);
      }
    }
  }

  /**
   * Gets the probe associated with the given name.
   * 
   * @param name
   *          the name
   * @return the probe
   */
  public synchronized Probe getProbe(final String name) {
    final Integer id = probesByName.get(name);

    if (id == null) {
      final Probe probe = new Probe(name, probesById.size(),
          probeParametersManager.getProbeWeight(name),
          probeParametersManager.getProbeThreshold(name));
      probesById.add(probe);
      probesByName.put(name, probe.id);
      return probe;
    } else {
      ProbeManager.logger.warn("Probe {} already used", name);
      return probesById.get(id);
    }
  }

  /**
   * Update probes parameters.
   * 
   * @return the duration
   */
  public long updateProbesParameters() {
    final long stopTime = System.currentTimeMillis();
    final long duration = stopTime - startTime;

    final int probeCount = probesById.size();

    // step 1 : terminate probing
    for (final Probe probe : probesById) {
      probe.terminate();
    }

    // Step 2 : compute candidate successors
    final BitSet[] candidateSuccessors = new BitSet[probeCount];
    for (int i = probeCount - 1; i >= 0; --i) {
      final BitSet candidates = new BitSet(probeCount);
      final long probeStop = probesById.get(i).stop;
      for (final Probe candidate : probesById) {
        if (probeStop <= candidate.start && i != candidate.id) {
          candidates.set(candidate.id);
        }
      }
      candidateSuccessors[i] = candidates;
    }

    // Step 3 : remove successors of successors
    final BitSet[] successors = new BitSet[probeCount];
    for (int i = probeCount - 1; i >= 0; --i) {
      final BitSet successor = new BitSet(probeCount);
      successor.or(candidateSuccessors[i]);
      // remove the successors of successors
      for (int succ = candidateSuccessors[i].nextSetBit(0); succ >= 0; succ = candidateSuccessors[i]
          .nextSetBit(succ + 1)) {
        successor.andNot(candidateSuccessors[succ]);
      }
      successors[i] = successor;
    }

    // Step 4 : compute root probe (i.e. which are not successors of anything)
    final BitSet rootProbes = new BitSet(probeCount);
    rootProbes.set(0, probeCount, true);
    for (int i = probeCount - 1; i >= 0; --i) {
      rootProbes.andNot(successors[i]);
    }

    // Step 5 : compute all pathes
    final List<TIntArrayList> pathes = new ArrayList<>();
    for (int rootProbe = rootProbes.nextSetBit(0); rootProbe >= 0; rootProbe = rootProbes
        .nextSetBit(rootProbe + 1)) {
      final TIntArrayList path = new TIntArrayList();
      path.add(rootProbe);
      computePathes(path, successors, pathes);
    }

    probeParametersManager.update(this, duration, pathes, probesById);
    return duration;
  }
}
