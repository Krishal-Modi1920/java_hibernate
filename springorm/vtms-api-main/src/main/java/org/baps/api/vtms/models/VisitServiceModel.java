package org.baps.api.vtms.models;

import org.baps.api.vtms.annotations.DateTimeRange;
import org.baps.api.vtms.annotations.EnumValue;
import org.baps.api.vtms.annotations.ValidBaseVisitServiceModel;
import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.enumerations.ServiceTypeEnum;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@DateTimeRange(startDateTimeField = "startDateTime", endDateTimeField = "endDateTime")
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
@ValidBaseVisitServiceModel(serviceTag = ServiceTypeEnum.SERVICE)
public class VisitServiceModel implements BaseVisitServiceModel {

    private String visitServiceId;

    @NotBlank
    private String serviceTemplateId;

    private String serviceTemplateName;

    @Valid
    @NotNull
    private VisitPersonnelModel coordinator;

    @Valid
    private PersonnelBasicInfoModel meetingPersonnel;

    @NotNull
    @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
    private LocalDateTime startDateTime;

    @NotNull
    @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
    private LocalDateTime endDateTime;

    @NotNull
    private JsonNode metadata;

    @NotBlank
    @EnumValue(enumClass = ServiceTypeEnum.class)
    private String serviceType;

    @Valid
    private List<VisitLocationModel> visitLocationModelList;

}
