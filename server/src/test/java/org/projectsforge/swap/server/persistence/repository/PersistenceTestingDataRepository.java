package org.projectsforge.swap.server.persistence.repository;

import org.projectsforge.swap.server.persistence.data.Data;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersistenceTestingDataRepository extends JpaRepository<Data, Long> {

}
