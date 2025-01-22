package org.baps.api.vtms.models;

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
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class PreBookedVisitModel {

    private String visitId;

    private String requestNumber;

    @Size(max = 36)
    private String stage;

    @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
    private LocalDateTime startDateTime;

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

    @Size(max = 512)
    private String visitorComments;

    @NotNull
    @Valid
    private PreBookedVisitorModel primaryVisitorModel;

    @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
    private LocalDateTime createdAt;
}
