package org.baps.api.vtms.repositories;

import org.baps.api.vtms.models.base.Status;
import org.baps.api.vtms.models.entities.VisitLocation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface VisitLocationRepository extends JpaRepository<VisitLocation, String>, JpaSpecificationExecutor<VisitLocation> {

    List<VisitLocation> findAllByVisitLocationIdInAndVisitServiceVisitServiceIdAndVisitServiceStatusNot(Set<String> visitLocationId,
                                                                                                        String visitServiceId,
                                                                                                        Status visitServiceStatus);

    List<VisitLocation> findAllByVisitLocationIdInAndVisitServiceVisitVisitId(Set<String> visitLocationIds, String visitId);
}
