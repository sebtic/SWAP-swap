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
 * $Id: VelocityResourceLoader.java 84 2011-06-09 10:09:03Z sebtic $
 */
package org.projectsforge.swap.core.webui.velocity;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

/**
 * The resource loader used by velocity to load resources from classpath
 * directories WEB-INF/velocity/.
 */
public class VelocityResourceLoader extends ResourceLoader {

  /*
   * (non-Javadoc)
   * @see
   * org.apache.velocity.runtime.resource.loader.ResourceLoader#getLastModified
   * (org.apache.velocity.runtime.resource.Resource)
   */
  @Override
  public long getLastModified(final Resource resource) {
    return 0L;
  }

  /*
   * (non-Javadoc)
   * @see
   * org.apache.velocity.runtime.resource.loader.ResourceLoader#getResourceStream
   * (java.lang.String)
   */
  @Override
  public InputStream getResourceStream(final String name) throws ResourceNotFoundException {
    try {
      final Enumeration<URL> urls = Thread.currentThread().getContextClassLoader()
          .getResources("WEB-INF/velocity/" + name);

      if (urls.hasMoreElements()) {
        final URL url = urls.nextElement();
        if (urls.hasMoreElements()) {
          log.warn("Multiple candidates found for resource " + name);
        }

        return url.openStream();
      } else {
        throw new ResourceNotFoundException("VelocityResourceLoader: resource '" + name
            + "' not found by the class loader");
      }

    } catch (final IOException e) {
      throw new ResourceNotFoundException("VelocityResourceLoader: resource '" + name
          + "' not found by the class loader", e);
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * org.apache.velocity.runtime.resource.loader.ResourceLoader#init(org.apache
   * .commons.collections.ExtendedProperties)
   */
  @Override
  public void init(final ExtendedProperties extendedproperties) {
    if (log.isTraceEnabled()) {
      log.trace("VelocityResourceLoader : initialization complete.");
    }
  }

  /*
   * (non-Javadoc)
   * @see
   * org.apache.velocity.runtime.resource.loader.ResourceLoader#isSourceModified
   * (org.apache.velocity.runtime.resource.Resource)
   */
  @Override
  public boolean isSourceModified(final Resource resource) {
    return false;
  }
}
