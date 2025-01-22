package org.baps.api.vtms.repositories;

import org.baps.api.vtms.models.entities.Lookup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LookupRepository extends JpaRepository<Lookup, String>, JpaSpecificationExecutor<Lookup> {
    
    Optional<Lookup> findByKey(String key);
}
