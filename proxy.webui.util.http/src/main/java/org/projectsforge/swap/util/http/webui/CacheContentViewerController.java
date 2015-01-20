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
 * <http://www.gnu.org/licenses/>. $Id$
 */
package org.projectsforge.swap.util.http.webui;

import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.projectsforge.swap.core.http.CacheManager;
import org.projectsforge.swap.core.http.Response;
import org.projectsforge.swap.core.http.Util;
import org.projectsforge.swap.proxy.webui.information.InformationComponent;
import org.projectsforge.swap.proxy.webui.information.InformationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * The Class CacheContentViewerController.
 * 
 * @author Sébastien Aupetit
 */
@Component
@Controller
public class CacheContentViewerController extends InformationComponent {

  /** The Constant URL. */
  public static final String URL = InformationController.URL + "/org.projectsforge.swap.core.http.cache";

  /** The cache manager. */
  @Autowired
  private CacheManager cacheManager;

  /** The snapshot. */
  private List<Response> snapshot = Collections.emptyList();

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.web.config.ConfigurableDescriptor#getDescription
   * ()
   */
  @Override
  public String getDescription() {
    return "View the content of the cache manager";
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.web.config.ConfigurableDescriptor#getName()
   */
  @Override
  public String getName() {
    return "Cache management viewer";
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.web.config.ConfigurableDescriptor#getPriority()
   */
  @Override
  public int getPriority() {
    return Integer.MAX_VALUE - 1;
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.web.config.ConfigurableDescriptor#getUrl()
   */
  @Override
  public String getUrl() {
    return CacheContentViewerController.URL;
  }

  /**
   * Handle clear snapshot.
   * 
   * @return the model and view
   */
  @RequestMapping(value = CacheContentViewerController.URL + "/clearsnapshot", method = RequestMethod.GET)
  public synchronized ModelAndView handleClearSnapshot() {
    if (!isActive()) {
      return getInactiveMAV();
    }

    snapshot = Collections.emptyList();
    return handleDefault();
  }

  /**
   * The GET handler.
   * 
   * @return the model and view
   */
  @RequestMapping(value = CacheContentViewerController.URL, method = RequestMethod.GET)
  public ModelAndView handleDefault() {
    if (!isActive()) {
      return getInactiveMAV();
    }

    final ModelAndView mav = new ModelAndView("org.projectsforge.swap.core.http.cache/view");

    mav.addObject("url", CacheContentViewerController.URL);
    mav.addObject("rootline", getRootline());
    return mav;
  }

  /**
   * Handle download from snapshot.
   * 
   * @param request the request
   * @param response the response
   * @param id the id
   * @return the model and view
   * @throws Exception the exception
   */
  @RequestMapping(value = CacheContentViewerController.URL + "/downloadfromsnapshot", method = RequestMethod.GET)
  public synchronized ModelAndView handleDownloadFromSnapshot(final HttpServletRequest request,
      final HttpServletResponse response, @RequestParam int id) throws Exception {
    if (!isActive()) {
      return getInactiveMAV();
    }

    id--;

    if (snapshot == null || id < 0 || id >= snapshot.size()) {
      response.sendError(HttpStatus.NOT_FOUND.value());
    } else {
      final Response ar = snapshot.get(id);
      response.setContentType(ar.getMime());
      final String path = new URI(ar.getRequest().getURL().toExternalForm()).getPath();
      final int index = path.lastIndexOf('/');
      String filename;
      if (index == -1) {
        filename = path;
      } else {
        filename = path.substring(index + 1);
      }

      response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

      try (final InputStream is = ar.getContent().getInputStream()) {
        Util.dumpTo(is, response.getOutputStream());
      }
      response.flushBuffer();
    }
    return null;
  }

  /**
   * Handle take snapshot.
   * 
   * @return the model and view
   */
  @RequestMapping(value = CacheContentViewerController.URL + "/takesnapshot", method = RequestMethod.GET)
  public synchronized ModelAndView handleTakeSnapshot() {
    if (!isActive()) {
      return getInactiveMAV();
    }

    snapshot = cacheManager.getSnapshot();
    return handleViewSnapshot();
  }

  /**
   * Handle view snapshot.
   * 
   * @return the model and view
   */
  @RequestMapping(value = CacheContentViewerController.URL + "/viewsnapshot", method = RequestMethod.GET)
  public synchronized ModelAndView handleViewSnapshot() {
    if (!isActive()) {
      return getInactiveMAV();
    }

    if (snapshot == null) {
      snapshot = cacheManager.getSnapshot();
    }

    final ModelAndView mav = new ModelAndView("org.projectsforge.swap.core.http.cache/snapshot");
    mav.addObject("url", CacheContentViewerController.URL);
    mav.addObject("snapshot", snapshot);
    mav.addObject("rootline", getRootline());
    return mav;
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.web.mvc.AbstractMVCComponent#isActive()
   */
  @Override
  public boolean isActive() {
    return true;
  }

}
