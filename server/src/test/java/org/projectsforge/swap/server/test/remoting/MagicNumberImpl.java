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
package org.projectsforge.swap.server.test.remoting;

import java.util.concurrent.Semaphore;
import org.projectsforge.swap.proxy.test.remoting.MagicNumber;
import org.springframework.stereotype.Component;

/**
 * The Class MagicNumberImpl.
 * 
 * @author Sébastien Aupetit
 */
@Component
public class MagicNumberImpl implements MagicNumber {

  /** The semaphore. */
  public Semaphore semaphore = new Semaphore(0);

  /* (non-Javadoc)
   * @see org.projectsforge.swap.proxy.test.remoting.MagicNumber#getMagicNumber()
   */
  @Override
  public int getMagicNumber() {
    semaphore.release();
    return 1234;
  }

}
