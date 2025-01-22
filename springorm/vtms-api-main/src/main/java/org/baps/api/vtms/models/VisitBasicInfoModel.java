package org.baps.api.vtms.models;

import org.baps.api.vtms.constants.GeneralConstant;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class VisitBasicInfoModel {
    private String visitId;

    private String requestNumber;
    
    private String type;

    private String typeOfVisit;

    private String stage;

    @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
    private LocalDateTime startDateTime;

    @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
    private LocalDateTime endDateTime;

    private Integer totalVisitors;

    private Integer childFemaleCount;

    private Integer childMaleCount;

    private Integer adultFemaleCount;
    
    private Integer adultMaleCount;

    private Integer seniorFemaleCount;

    private Integer seniorMaleCount;

    private VisitorBasicInfoModel primaryVisitorModel;

    private VisitorBasicInfoModel secondaryVisitorModel;

    private PersonnelBasicInfoModel relationshipManagerPersonnelBasicInfoModel;

    private PersonnelBasicInfoModel guestVisitCoordinatorPersonnelBasicInfoModel;

    private PersonnelBasicInfoModel guestUsherPersonnelBasicInfoModel;
    
    private List<String> serviceNameList;

    private String tourType;

    private List<String> meetingPersonnelList;

    @DateTimeFormat(pattern = GeneralConstant.DATE_TIME_FORMAT)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = GeneralConstant.DATE_TIME_FORMAT)
    private LocalDateTime createdAt;
}
