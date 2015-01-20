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
package org.projectsforge.swap.core.webui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import org.springframework.web.servlet.ModelAndView;

/**
 * The base class for MVC component.
 * 
 * @author Sébastien Aupetit
 */
public abstract class AbstractMVCComponent {

  /** The parent. */
  private AbstractMVCComponent parent;

  /** The components. */
  private AbstractMVCComponent[] components = new AbstractMVCComponent[0];

  /**
   * Gets the components.
   * 
   * @return the components
   */
  public AbstractMVCComponent[] getComponents() {
    final List<AbstractMVCComponent> actives = new ArrayList<AbstractMVCComponent>();
    for (final AbstractMVCComponent component : components) {
      component.setParent(this);
      if (component.isActive()) {
        actives.add(component);
      }
    }

    Collections.sort(actives, new Comparator<AbstractMVCComponent>() {
      @Override
      public int compare(final AbstractMVCComponent o1, final AbstractMVCComponent o2) {
        if (o1.getPriority() < o2.getPriority()) {
          return 1;
        } else if (o1.getPriority() == o2.getPriority()) {
          return o1.getName().compareToIgnoreCase(o2.getName());
        } else {
          return -1;
        }
      }
    });

    return actives.toArray(new AbstractMVCComponent[0]);
  }

  /**
   * Gets the description.
   * 
   * @return the description
   */
  public abstract String getDescription();

  /**
   * Gets the inactive mav.
   * 
   * @return the inactive mav
   */
  public ModelAndView getInactiveMAV() {
    final ModelAndView mav = new ModelAndView("inactive");
    mav.addObject("rootline", getRootline());
    return mav;
  }

  /**
   * Gets the name.
   * 
   * @return the name
   */
  public abstract String getName();

  /**
   * Gets the priority (Higher is first).
   * 
   * @return the priority
   */
  public abstract int getPriority();

  /**
   * Gets the rootline.
   * 
   * @return the rootline
   */
  public final LinkedHashMap<String, String> getRootline() {
    final LinkedHashMap<String, String> rootline = new LinkedHashMap<String, String>();
    if (parent != null) {
      rootline.putAll(parent.getRootline());
    }
    rootline.put(getName(), getUrl());
    return rootline;
  }

  /**
   * Gets the url.
   * 
   * @return the url
   */
  public abstract String getUrl();

  /**
   * Checks if is active.
   * 
   * @return true, if is active
   */
  public abstract boolean isActive();

  /**
   * Sets the components.
   * 
   * @param <T>
   *          the generic type
   * @param components
   *          the new components
   */
  protected <T extends AbstractMVCComponent> void setComponents(final T[] components) {
    this.components = components;
    for (final AbstractMVCComponent component : components) {
      component.setParent(this);
    }

  }

  /**
   * Sets the parent.
   * 
   * @param parent
   *          the new parent
   */
  public final void setParent(final AbstractMVCComponent parent) {
    this.parent = parent;
  }
}
