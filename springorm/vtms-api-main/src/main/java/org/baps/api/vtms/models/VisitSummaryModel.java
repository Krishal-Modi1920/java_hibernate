package org.baps.api.vtms.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class VisitSummaryModel {
    
    private VisitBasicInfoModel visitBasicInfoModel;
    
    private List<VisitPersonnelModel> visitPersonnelModelList;
    
    private List<VisitSummaryServiceModel> visitSummaryServiceModelList;

}
