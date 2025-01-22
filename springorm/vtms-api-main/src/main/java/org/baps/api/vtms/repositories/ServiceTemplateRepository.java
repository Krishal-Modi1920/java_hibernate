package org.baps.api.vtms.repositories;


import org.baps.api.vtms.enumerations.ServiceTypeEnum;
import org.baps.api.vtms.models.entities.ServiceTemplate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ServiceTemplateRepository extends JpaRepository<ServiceTemplate, String>, JpaSpecificationExecutor<ServiceTemplate> {

    Optional<ServiceTemplate> findByServiceTemplateIdAndServiceTypeEnumAndSiteUuCode(String id,
                                                                                     ServiceTypeEnum serviceTypeEnum,
                                                                                     String siteUUCode);

    Optional<ServiceTemplate> findFirstByServiceTypeEnumAndSiteUuCode(ServiceTypeEnum serviceTypeEnum, String siteUUCode);

    List<ServiceTemplate> findByServiceTemplateIdInAndSiteUuCode(Set<String> serviceTemplateIds, String siteUUCode);
}
