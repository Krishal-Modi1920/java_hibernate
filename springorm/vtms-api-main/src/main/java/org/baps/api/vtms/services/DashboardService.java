package org.baps.api.vtms.services;

import org.baps.api.vtms.enumerations.VisitTypeEnum;
import org.baps.api.vtms.models.VisitPublicFeedbackSummaryModel;
import org.baps.api.vtms.repositories.VisitPublicFeedbackRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.lang3.ObjectUtils;


@SuppressFBWarnings({"EI_EXPOSE_REP2"})
@RequiredArgsConstructor
@Service
public class DashboardService {
    
    private final VisitPublicFeedbackRepository visitPublicFeedbackRepository;

    public VisitPublicFeedbackSummaryModel getPreBookedVisitFeedbackSummary(final String siteUUCode, final LocalDateTime startDateTime,
            final LocalDateTime endDateTime) {
        
        final VisitPublicFeedbackSummaryModel visitPublicFeedbackModel =  
                visitPublicFeedbackRepository.findVisitPublicFeedbackCountByFilter(siteUUCode, startDateTime, endDateTime,
                        VisitTypeEnum.TOUR);
        
        final long totalRecord = visitPublicFeedbackModel.getTotalRecord();

        final Long bookingRating = visitPublicFeedbackModel.getBookingProcessRating();

        if (ObjectUtils.isNotEmpty(bookingRating)) {
            visitPublicFeedbackModel.setBookingProcessRating(bookingRating / totalRecord);
        }

        final Long overallRating = visitPublicFeedbackModel.getOverallRating();

        if (ObjectUtils.isNotEmpty(overallRating)) {
            visitPublicFeedbackModel.setOverallRating(overallRating / totalRecord);
        }
        
        return visitPublicFeedbackModel;
    }
}
