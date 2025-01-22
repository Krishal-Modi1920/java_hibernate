package org.baps.api.vtms.services;

import org.baps.api.vtms.exceptions.DataNotFoundException;
import org.baps.api.vtms.mappers.VisitScheduleMapper;
import org.baps.api.vtms.models.VisitScheduleModel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.collections.CollectionUtils;

@SuppressFBWarnings({"EI_EXPOSE_REP2"})
@RequiredArgsConstructor
@Service
public class VisitScheduleService {

    private final VisitService visitService;

    private final VisitScheduleMapper visitScheduleMapper;

    /**
     * Retrieves a list of {@link VisitScheduleModel} objects for a given visit.
     *
     * @param visitId The unique visit identifier.
     * @param siteUUCode The unique code associated with the site.
     * @return A list of {@link VisitScheduleModel} objects representing the visit schedule(s).
     * @throws DataNotFoundException if the visit with the provided ID does not exist.
     */
    @Transactional(readOnly = true)
    public List<VisitScheduleModel> getVisitScheduleList(final String visitId, final String siteUUCode) {
        // Find the existing visit based on visitId.
        final var existingVisit = visitService.findByIdAndSiteUUCode(visitId, siteUUCode);

        final List<VisitScheduleModel> visitScheduleModelList = new ArrayList<>();

        final VisitScheduleModel visitScheduleModel = visitScheduleMapper.visitToVisitScheduleModel(existingVisit);
        visitScheduleModel.setServiceName("Visit Management");
        visitScheduleModelList.add(visitScheduleModel);

        if (CollectionUtils.isNotEmpty(existingVisit.getVisitServiceList())) {
            visitScheduleModelList.addAll(visitScheduleMapper.visitServiceListToVisitScheduleModelList(
                existingVisit.getVisitServiceList()));
        }
        return visitScheduleModelList;
    }
}
