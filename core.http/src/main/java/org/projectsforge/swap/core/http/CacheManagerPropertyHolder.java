package org.projectsforge.swap.core.http;

import org.projectsforge.utils.propertyregistry.IntegerProperty;
import org.projectsforge.utils.propertyregistry.PropertyHolder;

public class CacheManagerPropertyHolder implements PropertyHolder {

  public static final IntegerProperty maxAgeBeforeForcedRefresh = new IntegerProperty(
      "httpCacheManager.maxAgeBeforeForcedRefresh", 10 * 60 * 1000);

  public static final IntegerProperty maxAgeForForcedExpiration = new IntegerProperty(
      "httpCacheManager.maxAgeForForcedExpiration", 2 * 60 * 1000);

}
