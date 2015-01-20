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
package org.projectsforge.swap.proxy.certificate;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * The class that contains a shared {@link SecureRandom} instances to reduce
 * multiple allocation of a {@link SecureRandom}.
 * 
 * @author Sébastien Aupetit
 */
@Component
public final class SharedSecureRandom {

  /** The logger. */
  Logger logger = LoggerFactory.getLogger(SharedSecureRandom.class);

  /** The secure random. */
  private SecureRandom secureRandom;

  /**
   * Gets the secure random.
   * 
   * @return the secure random
   */
  public SecureRandom getSecureRandom() {
    return secureRandom;
  }

  /**
   * Initializes the bean.
   * 
   * @throws NoSuchAlgorithmException the no such algorithm exception
   */
  @PostConstruct
  public void init() throws NoSuchAlgorithmException {
    try {
      // weaker but non blocking secure random generator than the default on
      // linux. It's not an issue for what we need.
      secureRandom = SecureRandom.getInstance("SHA1PRNG");
    } catch (final NoSuchAlgorithmException e) {
      logger.error("Can not get the secure random number generator", e);
      throw e;
    }
  }
}
