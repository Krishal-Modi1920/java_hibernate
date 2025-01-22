package org.baps.api.vtms.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Data
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@AllArgsConstructor
@NoArgsConstructor
public class VisitPublicFeedbackSummaryModel {

    private long totalRecord;

    private Long bookingProcessRating;

    private Long overallRating;
    
}
