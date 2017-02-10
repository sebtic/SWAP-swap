package org.projectsforge.swap.proxy.webui.api;

import org.projectsforge.swap.proxy.webui.WelcomeComponent;
import org.projectsforge.swap.proxy.webui.WelcomeController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * The web controller used to manage information controllers.
 */
@Controller
@Component
public class ApiController extends WelcomeComponent {

  /** The Constant URL. */
  public static final String URL = WelcomeController.URL + "api";

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.web.MainModuleDescriptor#getDescription()
   */
  @Override
  public String getDescription() {
    return "Allows to retrieve api call from various components of the proxy.";
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.web.MainModuleDescriptor#getName()
   */
  @Override
  public String getName() {
    return "Api of components";
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.web.MainModuleDescriptor#getPriority()
   */
  @Override
  public int getPriority() {
    return Integer.MAX_VALUE - 1;
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.web.MainModuleDescriptor#getUrl()
   */
  @Override
  public String getUrl() {
    return ApiController.URL;
  }

  /**
   * Config.
   * 
   * @return the model and view
   */
  @RequestMapping(ApiController.URL)
  public ModelAndView handle() {
    if (!isActive()) {
      return getInactiveMAV();
    }
    final ModelAndView mav = new ModelAndView("api");
    mav.addObject("components", getComponents());
    mav.addObject("rootline", getRootline());
    return mav;
  }

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
  public void setComponents(final ApiComponent[] components) {
    super.setComponents(components);
  }

}
