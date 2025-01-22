package org.baps.api.vtms.models;

import org.baps.api.vtms.annotations.DateTimeRange;
import org.baps.api.vtms.constants.GeneralConstant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DateTimeRange(startDateTimeField = "startDateTime", endDateTimeField = "endDateTime")
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class VisitModel {
    private String visitId;

    private String requestNumber;

    private String type;

    @Size(max = 128)
    private String typeOfVisit;

    @Size(max = 36)
    private String stage;

    @NotNull
    @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
    private LocalDateTime startDateTime;

    @NotNull
    @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
    private LocalDateTime endDateTime;

    private Integer totalVisitors;

    @Min(0)
    @Max(999)
    private Integer childFemaleCount;

    @Min(0)
    @Max(999)
    private Integer childMaleCount;

    @Min(0)
    @Max(999)
    private Integer adultFemaleCount;

    @Min(0)
    @Max(999)
    private Integer adultMaleCount;

    @Min(0)
    @Max(999)
    private Integer seniorFemaleCount;

    @Min(0)
    @Max(999)
    private Integer seniorMaleCount;
    
    @Size(max = 512)
    @Pattern(regexp = "(?s).*", message = "{visit_model.requesterNotes.invalid}")
    private String requesterNotes;

    @NotNull
    @Size(min = 1)
    private List<ServiceTemplateBasicInfoModel> requestedServices;

    @Size(max = 512)
    @Pattern(regexp = "(?s).*", message = "{visit_model.visitorComments.invalid}")
    private String visitorComments;

    @NotNull
    @Valid
    private VisitorModel primaryVisitorModel;

    @Valid
    private VisitorModel secondaryVisitorModel;

    @NotNull
    @Valid
    private PersonnelBasicInfoModel relationshipManagerPersonnelBasicInfoModel;

    private PersonnelBasicInfoModel guestVisitCoordinatorPersonnelBasicInfoModel;

    @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
    private LocalDateTime createdAt;

    private VisitTabModel visitTabModel;

    @Size(max = 128)
    private String interviewerName;

    @Size(max = 12)
    private String interviewerPhoneNumber;
    
    @NotBlank
    private String pointOfContact;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String tourType;
    
    private String tourSlotId;
    
    private String publicFeedbackId;
}
