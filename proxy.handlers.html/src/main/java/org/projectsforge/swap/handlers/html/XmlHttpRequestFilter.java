package org.projectsforge.swap.handlers.html;

import org.projectsforge.swap.core.handlers.Handler;
import org.projectsforge.swap.core.handlers.HandlerContext;
import org.projectsforge.swap.core.http.Header;
import org.projectsforge.swap.core.http.Mime;
import org.projectsforge.swap.core.http.Response;
import org.projectsforge.swap.handlers.mime.ResponseFilter;
import org.projectsforge.swap.handlers.mime.StatisticsCollector;

/**
 * Filter used to filter Ajax requests. They are detected with the X-Requested-With: XMLHttpRequest HTTP header.
 * @author Sébastien Aupetit
 *
 */
@Handler(singleton = true)
@Mime(mime = "text/html")
public class XmlHttpRequestFilter implements ResponseFilter {
  
  public final static String XRequestedWith = "X-Requested-With";
  public final static String XMLHttpRequest = "XMLHttpRequest";
  




  @Override
  public boolean filter(HandlerContext<ResponseFilter> context,
      StatisticsCollector statisticsCollector, Response response) throws Exception {

    for( Header header : response.getRequest().getHeaders().getHeaders(XRequestedWith))
    {
     if (XMLHttpRequest.equalsIgnoreCase(header.getValue()))
     {
       response.getRequest().setCacheable(false);
       return false;
     }
    }
    
    return true;
  }

}
