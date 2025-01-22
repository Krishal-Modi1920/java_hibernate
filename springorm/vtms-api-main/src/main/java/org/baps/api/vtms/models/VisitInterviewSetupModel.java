package org.baps.api.vtms.models;

import lombok.Data;

import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Data
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class VisitInterviewSetupModel {

    @Valid
    @NotNull
    private VisitPersonnelModel interviewCoordinatorVisitPersonnelModel;

    @Valid
    private List<VisitServiceBasicInfoModel> visitServiceBasicInfoModelList;
}
