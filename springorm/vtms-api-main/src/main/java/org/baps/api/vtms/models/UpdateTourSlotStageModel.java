package org.baps.api.vtms.models;

import org.baps.api.vtms.annotations.EnumValue;
import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.enumerations.TourSlotStageEnum;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class UpdateTourSlotStageModel {

    @NotBlank
    @EnumValue(enumClass = TourSlotStageEnum.class)
    private String stage;

    @NotNull
    @DateTimeFormat(pattern = GeneralConstant.DATE_FORMAT_YYYY_MM_DD)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_FORMAT_YYYY_MM_DD)
    private LocalDate startDate;

    @NotNull
    @DateTimeFormat(pattern = GeneralConstant.DATE_FORMAT_YYYY_MM_DD)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_FORMAT_YYYY_MM_DD)
    private LocalDate endDate;

    @NotNull
    @DateTimeFormat(pattern = GeneralConstant.TIME_FORMAT_24)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.TIME_FORMAT_24)
    private LocalTime startTime;

    @NotNull
    @DateTimeFormat(pattern = GeneralConstant.TIME_FORMAT_24)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.TIME_FORMAT_24)
    private LocalTime endTime;

}
