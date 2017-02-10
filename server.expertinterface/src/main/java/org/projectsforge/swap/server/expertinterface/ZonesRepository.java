package org.projectsforge.swap.server.expertinterface;

import java.util.List;

import org.projectsforge.swap.core.expertinterface.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ZonesRepository extends JpaRepository<Zone, Long> {
  @Query("select z from org.projectsforge.swap.expertinterface.Zone z where z.url = :url and z.timeRemoved IS NULL")
  List<Zone> findActivesByUrl(@Param("url") String url);

  List<Zone> findByUrl(@Param("url") String url);
}
