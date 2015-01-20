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
package org.projectsforge.swap.core.timelimitedoperations;

import gnu.trove.list.array.TIntArrayList;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import org.apache.commons.math3.util.FastMath;
import org.projectsforge.swap.core.environment.Environment;
import org.projectsforge.swap.core.environment.impl.EnvironmentPropertyHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ProbeParametersManager manages probes parameters over the time.
 */
public class ProbeParametersManager {

  /**
   * The Class ProbeParameters.
   */
  public class ProbeParameters {

    /** The name. */
    final String name;

    /** The threshold. */
    long threshold;

    /** The weight. */
    final long weight;

    /**
     * Instantiates a new probe parameters.
     * 
     * @param probeParameters the probe parameters
     */
    public ProbeParameters(final ProbeParameters probeParameters) {
      this.name = probeParameters.name;
      this.threshold = probeParameters.threshold;
      this.weight = probeParameters.weight;
    }

    /**
     * Instantiates a new probe parameters.
     * 
     * @param definition the definition
     */
    public ProbeParameters(final String definition) {
      final int index = definition.indexOf('=');
      this.name = definition.substring(0, index);
      this.threshold = Long.parseLong(definition.substring(index + 1));
      this.weight = getProbeWeight(name);
    }

    /**
     * Instantiates a new probe parameters.
     * 
     * @param name the name
     * @param threshold the threshold
     * @param weight the weight
     */
    public ProbeParameters(final String name, final long threshold, final long weight) {
      this.name = name;
      this.threshold = threshold;
      this.weight = weight;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return name + '=' + threshold;
    }
  }

  /** The Constant logger. */
  private static final Logger logger = LoggerFactory.getLogger(ProbeParametersManager.class);

  /** The Constant maxDuration. */
  private final long maxDuration;

  /** The perturbation radius. */
  private final int perturbationRadius;

  /** The probe parameters. */
  private final Map<String, ProbeParameters> probeParameters = new TreeMap<>();

  /** The best duration. */
  private long bestDuration;

  /** The random. */
  private final Random random = new Random();

  /** The qualifier. */
  final String qualifier;

  /** The store. */
  private final File store;

  /**
   * Instantiates a new probe parameters manager.
   * 
   * @param environment the environment
   * @param qualifier the qualifier
   */
  public ProbeParametersManager(final Environment environment, final String qualifier) {
    this.qualifier = qualifier;
    // TODO CHANGE THIS
    // maxDuration = environment.getLongProperty("probes." + qualifier +
    // ".maxDuration");
    maxDuration = 60;
    // perturbationRadius = environment.getIntProperty("probes." + qualifier +
    // ".perturbationRadius");
    perturbationRadius = 5;
    store = new File(EnvironmentPropertyHolder.configurationDirectory.get(), "probes." + qualifier + ".conf");

    try (final BufferedReader reader = new BufferedReader(new FileReader(store))) {
      String line;
      bestDuration = Long.parseLong(reader.readLine());

      while ((line = reader.readLine()) != null) {
        final ProbeParameters pp = new ProbeParameters(line);
        probeParameters.put(pp.name, pp);
      }
      ProbeParametersManager.logger.debug("Probe parameters loaded from {}", store);
    } catch (final IOException e) {
      probeParameters.clear();
      bestDuration = 0;
    }
  }

  /**
   * Get the current probe parameters.
   * 
   * @return the probe parameters
   */
  public synchronized Map<String, ProbeParameters> getProbeParameters() {
    final Map<String, ProbeParameters> result = new TreeMap<>();
    for (final ProbeParameters pp : probeParameters.values()) {
      result.put(pp.name, new ProbeParameters(pp));
    }
    return result;
  }

  /**
   * Gets the probe threshold.
   * 
   * @param name the name
   * @return the probe threshold
   */
  synchronized long getProbeThreshold(final String name) {
    final ProbeParameters pp = probeParameters.get(name);
    if (pp == null) {
      return 1;
    } else {
      return FastMath.max(1,
          FastMath.min(maxDuration, pp.threshold + random.nextInt(perturbationRadius * 2 + 1) - perturbationRadius));
    }

  }

  /**
   * Gets the probe weight.
   * 
   * @param name the name
   * @return the probe weight
   */
  synchronized long getProbeWeight(final String name) {
    final ProbeParameters pp = probeParameters.get(name);
    if (pp != null) {
      return pp.weight;
    } else {
      ProbeParametersManager.logger.info("Assuming default weight (1) for probe {}.{}", qualifier, name);
      return Long.parseLong(System.getProperty("probe." + qualifier + '.' + name + ".weight", "1"));
    }
  }

  /**
   * Create a new probe manager.
   * 
   * @return the probe manager
   */
  public ProbeManager newProbeManager() {
    return new ProbeManager(this);
  }

  /**
   * Save the probe parameters.
   */
  private void save() {
    try (final PrintWriter out = new PrintWriter(store)) {
      out.println(bestDuration);
      for (final ProbeParameters pp : probeParameters.values()) {
        out.println(pp.toString());
      }
      ProbeParametersManager.logger.debug("Probe parameters saved to {}", store);
    } catch (final IOException e) {
      ProbeParametersManager.logger.warn("Can not save probe parameters to " + store, e);
    }
  }

  /**
   * Update the parameters according to the probe manager and the processing
   * duration.
   * 
   * @param probeManager the probe manager
   * @param duration the duration
   * @param pathes the pathes
   * @param probesById the probes by id
   */
  synchronized void update(final ProbeManager probeManager, final long duration, final List<TIntArrayList> pathes,
      final List<Probe> probesById) {

    if (probeManager.probesById.isEmpty()) {
      return;
    }

    // Step 1 : updates probeParameters
    for (final Probe probe : probeManager.probesById) {
      ProbeParameters pp = probeParameters.get(probe.name);

      if (pp == null) {
        // add ProbeParameters if the probe was added
        pp = new ProbeParameters(probe.name, probe.threshold, probe.weight);
        probeParameters.put(pp.name, pp);
      }

    }

    // Step 2 : compute best and new fitness
    double bestFitness = 0;
    double newFitness = 0;

    for (final TIntArrayList path : pathes) {
      long sumWeight = 0;
      for (int i = 0; i < path.size(); ++i) {
        sumWeight += probesById.get(path.get(i)).weight;
      }

      for (int i = 0; i < path.size(); ++i) {
        final Probe probe = probesById.get(path.get(i));
        final double coefficient = sumWeight / (double) (probe.weight * path.size());
        final double newVal = maxDuration - coefficient * probe.duration;
        final double bestVal = maxDuration - coefficient * probeParameters.get(probe.name).threshold;

        newFitness += newVal * newVal;
        bestFitness += bestVal * bestVal;
      }
    }

    System.err.println("new fitness: " + newFitness + ", best fitness: " + bestFitness + ", duration: " + duration);
    if (maxDuration < bestDuration && duration <= bestDuration || duration <= maxDuration
        && (newFitness < bestFitness || newFitness == bestFitness && duration > bestDuration)) {
      for (final Probe probe : probesById) {
        final ProbeParameters pp = probeParameters.get(probe.name);
        pp.threshold = FastMath.max(1, probe.threshold);
      }
      bestDuration = duration;
      save();
    }

    // // remove useless probes
    // final Set<String> inactiveProbes = new
    // HashSet<>(probeParameters.keySet());
    // inactiveProbes.removeAll(probeManager.probes.keySet());
    // for (final String inactiveProbe : inactiveProbes) {
    // probeParameters.remove(inactiveProbe);
    // }
    //

  }
}
