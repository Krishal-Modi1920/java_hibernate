package org.baps.api.vtms.models;

import org.baps.api.vtms.annotations.DateTimeRange;
import org.baps.api.vtms.annotations.UniquePersonnelIdForRoleId;
import org.baps.api.vtms.annotations.ValidBaseVisitServiceModel;
import org.baps.api.vtms.annotations.ValidVisitTourModel;
import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.enumerations.ServiceTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@DateTimeRange(startDateTimeField = "startDateTime", endDateTimeField = "endDateTime", message = "{visit_tour.timing.between.visit.timing}")
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@ValidBaseVisitServiceModel(serviceTag = ServiceTypeEnum.TOUR)
@ValidVisitTourModel
public class VisitTourModel implements BaseVisitServiceModel {

    private String visitTourId;

    @NotBlank
    private String serviceTemplateId;

    private String serviceTemplateName;

    //@NotNull
    @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
    private LocalDateTime startDateTime;

    //@NotNull
    @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
    private LocalDateTime endDateTime;

    //@Valid
    //@NotNull
    private VisitPersonnelModel tourCoordinator;

    @UniquePersonnelIdForRoleId
    private List<VisitPersonnelModel> tourGuideList;

    //@Valid
    //@NotEmpty
    private List<VisitLocationModel> visitLocationModelList;

    @NotNull
    private String tourType;

    @Override
    public String getServiceType() {
        return ServiceTypeEnum.TOUR.name();
    }

}
