/**
 * Copyright 2010 Sébastien Aupetit <sebastien.aupetit@univ-tours.fr> This file
 * is part of SHS. SWAP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version. SWAP P is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with SHS. If not, see
 * <http://www.gnu.org/licenses/>. $Id: WelcomeController.java 98 2011-11-24
 * 12:10:32Z sebtic $
 */
package org.projectsforge.swap.proxy.webui;

import org.projectsforge.swap.core.webui.AbstractMVCComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * The root web controller.
 * 
 * @author Sébastien Aupetit
 */
@Controller("webinterface.welcomecontroller")
public class WelcomeController extends AbstractMVCComponent {

  /** The Constant URL. */
  public static final String URL = "/";

  /*
   * (non-Javadoc)
   * @see org.projectsforge.shs.web.mvc.AbstractMVCComponent#getDescription()
   */
  @Override
  public String getDescription() {
    return "Welcome component";
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.shs.web.mvc.AbstractMVCComponent#getName()
   */
  @Override
  public String getName() {
    return "Welcome";
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.shs.web.mvc.AbstractMVCComponent#getPriority()
   */
  @Override
  public int getPriority() {
    return 0;
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.shs.web.mvc.AbstractMVCComponent#getUrl()
   */
  @Override
  public String getUrl() {
    return URL;
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.shs.web.mvc.AbstractMVCComponent#isActive()
   */
  @Override
  public boolean isActive() {
    return true;
  }

  /**
   * Sets the components.
   * 
   * @param components the new components
   */
  @Autowired(required = false)
  public void setComponents(final WelcomeComponent[] components) {
    super.setComponents(components);
  }

  /**
   * Handle the request.
   * 
   * @return the model and view
   */
  @RequestMapping(WelcomeController.URL)
  public ModelAndView welcome() {
    final ModelAndView mav = new ModelAndView("welcome");
    mav.addObject("components", getComponents());
    mav.addObject("rootline", getRootline());
    return mav;
  }
}
