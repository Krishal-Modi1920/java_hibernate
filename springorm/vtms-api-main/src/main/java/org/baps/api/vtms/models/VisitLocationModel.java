package org.baps.api.vtms.models;

import org.baps.api.vtms.annotations.DateTimeRange;
import org.baps.api.vtms.annotations.EnumValue;
import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.enumerations.LocationTagEnum;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@DateTimeRange(startDateTimeField = "startDateTime", endDateTimeField = "endDateTime", locationTagEnum = "locationTagEnum")
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class VisitLocationModel {

    private String visitLocationId;

    @NotBlank
    private String locationId;

    private String locationName;

    @NotNull
    @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
    private LocalDateTime startDateTime;

    @NotNull
    @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
    private LocalDateTime endDateTime;

    @NotBlank
    @EnumValue(enumClass = LocationTagEnum.class)
    private String locationTagEnum;

    @Size(max = 64)
    private String interviewPackage;

    @Valid
    private VisitPersonnelModel interviewVolunteerVisitPersonnelModel;
}
