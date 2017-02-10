package org.projectsforge.swap.server.expertinterface;

import java.util.Date;
import java.util.List;

import org.projectsforge.swap.core.environment.Environment;
import org.projectsforge.swap.core.expertinterface.Zone;
import org.projectsforge.swap.expertinterface.remoting.Zones;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Component;

@Component
@EnableJpaRepositories
public class ZonesImpl implements Zones {

  /** The environment. */
  @Autowired
  private Environment environment;

  /** The zone repository */
  @Autowired
  private ZonesRepository zones;

  @Override
  public List<Zone> findByUrl(String url) {
    List<Zone> list = zones.findByUrl(url);
    return list;
  }

  @Override
  public Zone findOne(Long zoneid) {
    return zones.findOne(zoneid);
  }

  @Override
  public Long save(Zone zone) {
    Zone savedZone = zones.save(zone);
    return savedZone.getId();
  }

  @Override
  public boolean remove(Long zoneid) {
    Zone zone = zones.findOne(Long.valueOf(zoneid));
    if (zone != null) {
      zone.setTimeRemoved(new Date());
      zones.save(zone);
      return true;
    }
    return false;
  }

  @Override
  public List<Zone> findActivesByUrl(String url) {
    List<Zone> list = zones.findActivesByUrl(url);
    return list;
  }

}
