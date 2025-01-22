package org.baps.api.vtms.models;

import org.baps.api.vtms.annotations.EnumValue;
import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.enumerations.TourSlotStageEnum;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.constraints.NotBlank;

@Data
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class PreBookedTourSlotModel {

    private String tourSlotId;
    
    @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
    private LocalDateTime startDateTime;

    @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
    private LocalDateTime endDateTime;
    
    @NotBlank
    @EnumValue(enumClass = TourSlotStageEnum.class)
    private String stage;
    
    private long availableGuestSize;
}
