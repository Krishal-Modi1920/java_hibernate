package org.baps.api.vtms.repositories;

import org.baps.api.vtms.models.entities.Location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, String>, JpaSpecificationExecutor<Location> {
    
    List<Location> findByLocationIdInAndServiceLocationListServiceTemplateServiceTemplateId(List<String> locationIdList,
            String serviceTemplateId);
}
