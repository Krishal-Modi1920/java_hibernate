package org.baps.api.vtms.services;


import org.baps.api.vtms.models.base.Status;
import org.baps.api.vtms.models.entities.VisitLocation;
import org.baps.api.vtms.repositories.VisitLocationRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
@RequiredArgsConstructor
@Service
public class VisitLocationService {

    private final VisitLocationRepository visitLocationRepository;

    /**
     * Retrieves a list of VisitLocations based on a set of VisitLocation IDs and a VisitService ID.
     *
     * @param visitLocationIds A set of VisitLocation IDs to filter the results.
     * @param visitServiceId   The VisitService ID to filter the results.
     * @return list of VisitLocation objects matching the provided IDs and Service ID,
     *      or an empty list if no matches are found or input is invalid.
     */
    @Transactional(readOnly = true)
    public List<VisitLocation> findAllByVisitLocationIdInAndVisitServiceVisitServiceId(final Set<String> visitLocationIds,
                                                                                       final String visitServiceId) {

        return visitLocationRepository.findAllByVisitLocationIdInAndVisitServiceVisitServiceIdAndVisitServiceStatusNot(
            visitLocationIds, visitServiceId, Status.DELETED);
    }

}
