package org.projectsforge.swap.proxy.webui.information;

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
public class InformationController extends WelcomeComponent {

  /** The Constant URL. */
  public static final String URL = WelcomeController.URL + "info";

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.web.MainModuleDescriptor#getDescription()
   */
  @Override
  public String getDescription() {
    return "Allows to retrieve informations from various components of the proxy.";
  }

  /*
   * (non-Javadoc)
   * @see org.projectsforge.swap.core.web.MainModuleDescriptor#getName()
   */
  @Override
  public String getName() {
    return "Informations on components";
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
    return InformationController.URL;
  }

  /**
   * Config.
   * 
   * @return the model and view
   */
  @RequestMapping(InformationController.URL)
  public ModelAndView handle() {
    if (!isActive()) {
      return getInactiveMAV();
    }
    final ModelAndView mav = new ModelAndView("info");
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
  public void setComponents(final InformationComponent[] components) {
    super.setComponents(components);
  }

}
