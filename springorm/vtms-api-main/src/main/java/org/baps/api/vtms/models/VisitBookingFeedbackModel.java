package org.baps.api.vtms.models;

import lombok.Data;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.constraints.Size;

@Data
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class VisitBookingFeedbackModel {

    private String visitPublicFeedbackId;

    private Integer bookingProcessRating;

    private Integer overallRating;
    
    @Size(max = 512)
    private String comment;
}
