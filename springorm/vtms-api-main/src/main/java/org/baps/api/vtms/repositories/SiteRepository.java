package org.baps.api.vtms.repositories;

import org.baps.api.vtms.models.entities.Site;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SiteRepository extends JpaRepository<Site, String>, JpaSpecificationExecutor<Site> {
    
    Optional<Site> findByUuCode(String uuCode);
}
