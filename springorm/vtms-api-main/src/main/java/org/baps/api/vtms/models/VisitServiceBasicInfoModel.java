package org.baps.api.vtms.models;

import lombok.Data;

import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Data
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class VisitServiceBasicInfoModel {

    private String visitServiceId;

    private String serviceTemplateName;

    private List<VisitPersonnelModel> visitPersonnelModelList;
    
    private List<VisitLocationModel> visitLocationBasicInfoModelList;
}
