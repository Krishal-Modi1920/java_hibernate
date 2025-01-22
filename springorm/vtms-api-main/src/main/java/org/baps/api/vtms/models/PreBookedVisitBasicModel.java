package org.baps.api.vtms.models;

import org.baps.api.vtms.annotations.DateTimeRange;
import org.baps.api.vtms.constants.GeneralConstant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@DateTimeRange(startDateTimeField = "startDateTime", endDateTimeField = "endDateTime")
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class PreBookedVisitBasicModel {

    private String visitId;

    private String requestNumber;

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

    @NotBlank
    private String tourSlotId;

    @Min(1)
    @Max(10000)
    @NotNull
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

    @NotNull
    @Valid
    private VisitorModel primaryVisitorModel;

    private PersonnelBasicInfoModel tourGuidePersonnelBasicInfoModel;

    @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
    private LocalDateTime createdAt;
}
