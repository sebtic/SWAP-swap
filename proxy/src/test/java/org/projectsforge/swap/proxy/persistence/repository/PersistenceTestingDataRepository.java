package org.projectsforge.swap.proxy.persistence.repository;

import org.projectsforge.swap.proxy.persistence.data.Data;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersistenceTestingDataRepository extends JpaRepository<Data, Long> {

}
