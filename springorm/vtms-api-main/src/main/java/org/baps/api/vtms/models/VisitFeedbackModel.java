package org.baps.api.vtms.models;

import org.baps.api.vtms.constants.GeneralConstant;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class VisitFeedbackModel {

    private String visitFeedbackId;
    
    @Valid
    @NotNull
    @Size(min = 1)
    private List<FeedBackRatingModel> feedBackRatingModelListForGeneralFeedBack;
    
    @Valid
    @NotNull
    private List<FeedBackRatingModel> feedBackRatingModelListForTourGuide;
    
    @Size(max = 512)
    private String comment;

    private VisitBasicInfoModel visitBasicInfoModel;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
    private LocalDateTime createdAt;
    
    private boolean feedbackExists;
    
    private boolean tourGuideExists;
}
