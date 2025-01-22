package org.baps.api.vtms.models;

import org.baps.api.vtms.constants.GeneralConstant;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class VisitScheduleModel {

    private String visitServiceId;

    private String serviceName;
    
    private List<VisitPersonnelModel> visitPersonnelModelList;
    
    @NotNull
    @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
    private LocalDateTime startDateTime;

    @NotNull
    @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
    private LocalDateTime endDateTime;
    
    private List<VisitLocationModel> visitLocationModelList;
}
