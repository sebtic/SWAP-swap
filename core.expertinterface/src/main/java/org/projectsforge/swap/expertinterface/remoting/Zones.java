package org.projectsforge.swap.expertinterface.remoting;

import java.util.List;

import org.projectsforge.swap.core.expertinterface.Zone;
import org.projectsforge.swap.core.remoting.RemoteInterface;

/**
 * Interface for zone management
 * 
 * @author Vincent Rouillé
 */
@RemoteInterface
public interface Zones {
  /**
   * Find all zones that matches the given url
   * 
   * @param url
   * @return
   */
  List<Zone> findByUrl(String url);
  
  /**
   * Find all actives zones that matches the given url
   * 
   * @param url
   * @return
   */
  List<Zone> findActivesByUrl(String url);
  
  /**
   * Find the zone with the given id
   * 
   * @param zoneid
   * @return
   */
  Zone findOne(Long zoneid);
  
  /**
   * Save the given zone and return its id
   * 
   * @param zone
   * @return
   */
  Long save(Zone zone);
  
  /**
   * Remove the requested zone
   * 
   * @param zoneid
   * @return
   */
  boolean remove(Long zoneid);
}
