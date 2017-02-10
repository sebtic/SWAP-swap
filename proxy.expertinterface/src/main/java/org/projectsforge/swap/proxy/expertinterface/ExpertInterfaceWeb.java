package org.projectsforge.swap.proxy.expertinterface;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Define expert interface zone API
 */
@Component
@Controller
public class ExpertInterfaceWeb {
  /** The package URL */
  public static final String PACKAGE = "org.projectsforge.swap.proxy.expertinterface";
  
  /** The Constant URL. */
  public static final String URL = "/" + ExpertInterfaceWeb.PACKAGE;
  
  /**
   * Handler of *.js file that add "Access-Control-Allow-Origin *" to response for cross site XHR requests
   * 
   * @param file
   * @param response
   */
  @RequestMapping(value = ExpertInterfaceWeb.URL + "/{file:[A-Za-z0-9-_]+}.js", method = RequestMethod.GET)
  public void handleScripts(@PathVariable String file, HttpServletResponse response) {
    try {
      if("Loader".compareToIgnoreCase(file) != 0)
      {
        // The Loader must be only loaded by script element so the webpage can't read it's content
        response.addHeader("Access-Control-Allow-Origin", "*");
      }
      response.setContentType("application/javascript;charset=UTF-8");
      // get your file as InputStream
      String filename = "/WEB-INF/resources"+URL+"/"+file+".js";
      InputStream is = ClassLoader.class.getResourceAsStream(filename);
      if(is == null)
        throw new RuntimeException("File not found : " +filename);
      // copy it to response's OutputStream
      IOUtils.copy(is, response.getOutputStream());
      response.flushBuffer();
    } catch (IOException ex) {
      throw new RuntimeException("IOError writing file to output stream");
    }
  }
  
  /**
   * Handler of *\/*.js file that add "Access-Control-Allow-Origin *" to response for cross site XHR requests
   * TODO Remove this method and fix the RequestMapping definition to add module support. 
   * Adding / to the regexp makes the regexp fails...
   * @see handleScripts
   * @param module
   * @param file
   * @param response
   */
  @RequestMapping(value = ExpertInterfaceWeb.URL + "/{module:[a-z]+}/{file:[A-Za-z0-9-_]+}.js", method = RequestMethod.GET)
  public void handleScriptsWithModule(@PathVariable String module, @PathVariable String file, HttpServletResponse response) {
	  handleScripts(module + "/" + file, response);
  }
}
