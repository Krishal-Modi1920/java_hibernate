package org.baps.api.vtms.models;

import org.baps.api.vtms.constants.GeneralConstant;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Data
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class TourDaySlotModel {
    
    @DateTimeFormat(pattern = GeneralConstant.DATE_FORMAT_YYYY_MM_DD)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_FORMAT_YYYY_MM_DD)
    private LocalDate tourSlotDate;

    private List<TourSlotModel> tourSlotModelList;
}
