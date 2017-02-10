package org.projectsforge.swap.proxy.expertinterface.api;

import org.projectsforge.swap.proxy.webui.api.ApiComponent;
import org.projectsforge.swap.proxy.webui.api.ApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Component
@Controller
public class ExpertInterfaceApi extends ApiComponent {

  /** The current package */
  private static final String PACKAGE = "org.projectsforge.swap.expertinterface.http.api";

  /** The Constant URL. */
  public static final String URL = ApiController.URL + "/" + PACKAGE;

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.web.config.ConfigurableDescriptor#getDescription
   * ()
   */
  @Override
  public String getDescription() {
    return "Api description of the expert interface";
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.web.config.ConfigurableDescriptor#getName()
   */
  @Override
  public String getName() {
    return "Api expert interface";
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
    return ExpertInterfaceApi.URL;
  }

  /**
   * The GET handler.
   * 
   * @return the model and view
   */
  @RequestMapping(value = ExpertInterfaceApi.URL, method = RequestMethod.GET)
  public ModelAndView handleDefault() {
    if (!isActive()) {
      return getInactiveMAV();
    }

    final ModelAndView mav = new ModelAndView(PACKAGE + "/view");

    mav.addObject("components", getComponents());
    mav.addObject("rootline", getRootline());
    return mav;
  }
  
  /**
   * Sets the components.
   * 
   * @param components the new components
   */
  @Autowired(required = false)
  public void setComponents(final ExpertInterfaceApiComponent[] components) {
    super.setComponents(components);
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
