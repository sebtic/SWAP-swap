/**
 * Copyright 2012 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr> This
 * software is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This software is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this software. If not, see
 * <http://www.gnu.org/licenses/>. $Id$
 */
package org.projectsforge.swap.handlers.mime;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Class StatisticsCollector used to store various statistics like execution
 * time. This class is thread safe but is mainly used to store things not to
 * retrieve it. Retrieval is done by {@link StatisticsCollectorInterceptor}
 * components.
 * 
 * @author Sébastien Aupetit
 */
public class StatisticsCollector {

  public static final String PROXY_ALL_TIMER_KEY = "org.projectsforge.swap.proxy.all";

  public static final String PROXY_GETRESPONSE_TIMER_KEY = "org.projectsforge.swap.proxy.getresponse";

  public static final String PROXY_MIMEHANDLER_ALL_TIMER_KEY = "org.projectsforge.swap.proxy.mimehandler.all";

  public static final String PROXY_MIMEHANDLER_DO_TIMER_KEY = "org.projectsforge.swap.proxy.mimehandler.do#";

  public static final String PROXY_RESPONSEFILTERING_ALL_TIMER_KEY = "org.projectsforge.swap.proxy.responsefiltering.all";

  public static final String PROXY_RESPONSEFILTERING_DO_TIMER_KEY = "org.projectsforge.swap.proxy.responsefiltering.do#";

  public static final String PROXY_REQUESTFILTERING_ALL_TIMER_KEY = "org.projectsforge.swap.proxy.requestfiltering.all";

  public static final String PROXY_REQUESTFILTERING_DO_TIMER_KEY = "org.projectsforge.swap.proxy.requestfiltering.do#";

  public static final String PROXY_HANDLER_HTML_ALL_TIMER_KEY = "org.projectsforge.org.swap.proxy.handlers.html.all";

  public static final String PROXY_HANDLER_HTML_DO_TIMER_KEY = "org.projectsforge.org.swap.proxy.handlers.html.do#";

  public static final String PROXY_HANDLER_HTML_AFTERDOM_TIMER_KEY = "org.projectsforge.org.swap.proxy.handlers.html.afterdom#";

  public static final String PROXY_HANDLER_HTML_SERIALIZERESPONSE_TIMER_KEY = "org.swap.proxy.handlers.html.serializeresponse";

  public static final String PROXY_HANDLER_HTML_CANCELEDTRANSFORMATION_VALUE_KEY = "org.swap.proxy.handlers.html.canceledtransformation";

  public static final String PROXY_HANDLER_HTML_TRANSFORMEDDOM_VALUE_KEY = "org.swap.proxy.handlers.html.transformeddom";

  public static final String PROXY_ORIGINALREQUEST_VALUE_KEY = "org.projectsforge.swap.proxy.originalrequest";

  public static final String PROXY_ORIGINALRESPONSE_VALUE_KEY = "org.projectsforge.swap.proxy.originalresponse";

  public static final String PROXY_REQUESTFILTERED_VALUE_KEY = "org.projectsforge.swap.proxy.requestfiltered";

  public static final String PROXY_RESPONSEFILTERED_VALUE_KEY = "org.projectsforge.swap.proxy.responsefiltered";

  public static final String PROXY_EFFECTIVERESPONSE_VALUE_KEY = "org.projectsforge.swap.proxy.effectiveresponse";

  public static final String PROXY_EFFECTIVEREQUEST_VALUE_KEY = "org.projectsforge.swap.proxy.effectiverequest";

  /** The start marks. */
  private final Map<String, Long> starts = new ConcurrentHashMap<>();

  /** The stop marks. */
  private final Map<String, Long> stops = new ConcurrentHashMap<>();

  /** The values. */
  private final Map<String, Object> values = new ConcurrentHashMap<>();

  /**
   * Get elapsed timer value.
   * 
   * @param key the key
   * @return the long
   */
  public long getElapsedTimer(final String key) {
    final Long end = stops.get(key);
    final Long begin = starts.get(key);
    if (end == null || begin == null) {
      throw new IllegalStateException("Invalid timer " + key + " [" + begin + ":" + end + "]");
    }
    return (end - begin) / 1000000;
  }

  /**
   * Gets the timer key set.
   * 
   * @return the timer key set
   */
  public Set<String> getTimerKeySet() {
    return starts.keySet();
  }

  /**
   * Gets the value.
   * 
   * @param <T> the generic type
   * @param key the key
   * @param valueClass the value class
   * @return the value
   */
  public <T> T getValue(final String key, final Class<T> valueClass) {
    return valueClass.cast(values.get(key));
  }

  /**
   * Gets the value key set.
   * 
   * @return the value key set
   */
  public Set<String> getValueKeySet() {
    return values.keySet();
  }

  /**
   * Sets the value.
   * 
   * @param key the key
   * @param value the value
   */
  public void setValue(final String key, final Object value) {
    values.put(key, value);
  }

  /**
   * Start timer.
   * 
   * @param key the key
   */
  public void startTimer(final String key) {
    if (starts.put(key, System.nanoTime()) != null) {
      throw new IllegalStateException("Timer already started");
    }
  }

  /**
   * Stop timer.
   * 
   * @param key the key
   */
  public void stopTimer(final String key) {
    if (stops.put(key, System.nanoTime()) != null) {
      throw new IllegalStateException("Timer already stopped");
    }
  }

}
