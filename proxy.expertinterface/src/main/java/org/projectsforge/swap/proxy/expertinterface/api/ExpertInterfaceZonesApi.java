package org.projectsforge.swap.proxy.expertinterface.api;

import java.util.Arrays;
import java.util.List;

import org.projectsforge.swap.core.expertinterface.Zone;
import org.projectsforge.swap.core.expertinterface.ZoneForm;
import org.projectsforge.swap.core.expertinterface.ZoneImage;
import org.projectsforge.swap.core.expertinterface.ZoneLink;
import org.projectsforge.swap.core.expertinterface.ZoneTable;
import org.projectsforge.swap.expertinterface.remoting.Zones;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Define expert interface zone API
 */
@Component
@Controller
public class ExpertInterfaceZonesApi extends ExpertInterfaceApiComponent {

  /** The logger. */
  private final Logger logger = LoggerFactory.getLogger(ExpertInterfaceZonesApi.class);
  
  /** The list of methods */
  private static final List<String> METHODS = Arrays.asList("list");

  /** The current package */
  private static final String PACKAGE = "org.projectsforge.swap.expertinterface.http.api";

  /** The Constant URL. */
  public static final String URL = ExpertInterfaceApi.URL + "/zones";

  /** The environment. */
  @Autowired
  private Zones zones;

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.web.config.ConfigurableDescriptor#getDescription
   * ()
   */
  @Override
  public String getDescription() {
    return "Zones managements for ExpertInterface";
  }

  /*
   * (non-Javadoc)
   * @see
   * org.projectsforge.swap.core.web.config.ConfigurableDescriptor#getName()
   */
  @Override
  public String getName() {
    return "Zones API";
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
    return ExpertInterfaceZonesApi.URL;
  }

  /**
   * The GET handler.
   *
   * @return the model and view
   */
  @RequestMapping(value = ExpertInterfaceZonesApi.URL, method = RequestMethod.GET)
  public ModelAndView handleDefault() {
    if (!isActive()) {
      return getInactiveMAV();
    }

    final ModelAndView mav = new ModelAndView(PACKAGE + "/view");

    mav.addObject("methods", METHODS);
    mav.addObject("rootline", getRootline());
    return mav;
  }

  /**
   * Handle /listactives GET request.
   * Return the list of zones in the requested page
   *
   * @return the model and view
   * @throws JsonProcessingException
   */
  @RequestMapping(value = ExpertInterfaceZonesApi.URL+"/listactives", method = RequestMethod.POST)
  @ResponseBody
  public Object handleListActives(@RequestParam String url) {
    if (!isActive()) {
      return getInactiveResponeEntity();
    }

    List<Zone> zones = getRepository().findActivesByUrl(url);

    return preparedResponse(zones);
  }

  /**
   * Handle /list GET request.
   * Return the list of zones in the requested page
   *
   * @return the model and view
   * @throws JsonProcessingException
   */
  @RequestMapping(value = ExpertInterfaceZonesApi.URL+"/list", method = RequestMethod.POST)
  @ResponseBody
  public Object handleList(@RequestParam String url) {
    if (!isActive()) {
      return getInactiveResponeEntity();
    }
    
    List<Zone> zones = getRepository().findByUrl(url);

    return preparedResponse(zones);
  }
  
  @RequestMapping(value = ExpertInterfaceZonesApi.URL+"/set", method = RequestMethod.POST)
  @ResponseBody
  public Object handleSet(@RequestParam("zone") String zoneInJson, @RequestParam String type) 
  {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    try {
      Zone zone = null;
      // TODO : Autodetect zones/type
      switch(type){
      case "image":
        zone = mapper.readValue(zoneInJson, ZoneImage.class);
        break;
      case "link":
        zone = mapper.readValue(zoneInJson, ZoneLink.class);
        break;
      case "form":
        zone = mapper.readValue(zoneInJson, ZoneForm.class);
        break;
      case "table":
        zone = mapper.readValue(zoneInJson, ZoneTable.class);
        break;
      case "default":
        zone = mapper.readValue(zoneInJson, Zone.class);
        break;
      default:
        throw new InvalidZoneTypeException(type);
      }
      
      if(!zone.prepare())
        throw new InvalidZoneDataException();
      zone.setId(zones.save(zone));
      
      ObjectMapper outmapper = new ObjectMapper();
      ObjectNode rootNode = outmapper.createObjectNode();
      rootNode.put("zoneid", zone.getId());
      return preparedResponse(outmapper.writeValueAsString(rootNode));
    } catch (Exception e) {
      logger.debug("set zone failed", e);
      ObjectMapper outmapper = new ObjectMapper();
      try {
        return preparedResponse(outmapper.writeValueAsString(e));
      } catch (JsonProcessingException e1) {
        return preparedResponse("{\"fatalerror\": true}");
      }
    }
    
  }

  @RequestMapping(value = ExpertInterfaceZonesApi.URL+"/remove", method = RequestMethod.POST)
  @ResponseBody
  public Object handleRemove(@RequestParam String zoneid) {
    try {
      Zones repository = getRepository();
      try{
        Long id = Long.valueOf(zoneid);
        if(repository.remove(id)) {
          return preparedResponse("Removed");
        }
      } catch (NumberFormatException exception) {
        return preparedResponse("Zone id invalid");
      }
      return preparedResponse("Zone not found");
    } catch(Exception w) {
      return preparedResponse(w.getMessage());
    }
  }

  private Zones getRepository() {
    return zones;
  }

  /**
   * Prepare a responseBody to allow XHR cross domain
   *
   * @param responseBody
   * @return A special ResponseEntity set to allow XHR cross domain
   */
  private ResponseEntity<Object> preparedResponse(Object responseBody) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Access-Control-Allow-Origin", "*");

    return new ResponseEntity<Object>(responseBody, headers, HttpStatus.OK);
  }

  /**
   *
   * @return A special ResponseEntity set to allow XHR cross domain
   */
  private Object getInactiveResponeEntity() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Access-Control-Allow-Origin", "*");

    return new ResponseEntity<String>("{\"inactive\":true}", headers, HttpStatus.SERVICE_UNAVAILABLE);
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
