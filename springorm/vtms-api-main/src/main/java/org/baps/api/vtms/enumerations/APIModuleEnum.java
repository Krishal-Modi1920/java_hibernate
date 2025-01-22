package org.baps.api.vtms.enumerations;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Getter
@RequiredArgsConstructor
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public enum APIModuleEnum {

    UPDATE_VISIT,

    CREATE_OR_UPDATE_VISIT_TEAM,

    CREATE_VISIT_SERVICE_OR_MEETING,

    UPDATE_VISIT_SERVICE_OR_MEETING,

    DELETE_VISIT_SERVICE_OR_MEETING,

    CREATE_VISIT_TOUR,

    UPDATE_VISIT_TOUR,

    UPDATE_VISIT_INTERVIEW_SETUP,

    DELETE_VISIT_INTERVIEW_SETUP,

    CREATE_VISIT_DOCUMENT,

    DELETE_VISIT_DOCUMENT;

    private List<VisitStageEnum> allowVisitStageList;

    static {
        
        //        allowVisitStageList  
        
        UPDATE_VISIT.allowVisitStageList = List.of(VisitStageEnum.PENDING, VisitStageEnum.ACCEPTED, VisitStageEnum.COMPLETED,
                VisitStageEnum.CHECK_IN, VisitStageEnum.CANCELLED, VisitStageEnum.EXPIRED, VisitStageEnum.DECLINED,
                VisitStageEnum.CLOSED, VisitStageEnum.NOSHOW);

        CREATE_OR_UPDATE_VISIT_TEAM.allowVisitStageList = List.of(VisitStageEnum.PENDING,
                VisitStageEnum.ACCEPTED, VisitStageEnum.CHECK_IN, VisitStageEnum.COMPLETED, VisitStageEnum.NOSHOW);

        CREATE_VISIT_SERVICE_OR_MEETING.allowVisitStageList = List.of(VisitStageEnum.ACCEPTED, VisitStageEnum.CHECK_IN, 
                VisitStageEnum.COMPLETED, VisitStageEnum.NOSHOW);

        UPDATE_VISIT_SERVICE_OR_MEETING.allowVisitStageList = List.of(VisitStageEnum.ACCEPTED, VisitStageEnum.CHECK_IN, 
                VisitStageEnum.COMPLETED, VisitStageEnum.NOSHOW);

        DELETE_VISIT_SERVICE_OR_MEETING.allowVisitStageList = List.of(VisitStageEnum.ACCEPTED, VisitStageEnum.CHECK_IN, 
                VisitStageEnum.COMPLETED, VisitStageEnum.NOSHOW);

        CREATE_VISIT_TOUR.allowVisitStageList = List.of(VisitStageEnum.ACCEPTED, VisitStageEnum.CHECK_IN,
                VisitStageEnum.COMPLETED, VisitStageEnum.NOSHOW);

        UPDATE_VISIT_TOUR.allowVisitStageList = List.of(VisitStageEnum.ACCEPTED, VisitStageEnum.CHECK_IN, 
                VisitStageEnum.COMPLETED, VisitStageEnum.NOSHOW);

        UPDATE_VISIT_INTERVIEW_SETUP.allowVisitStageList = List.of(VisitStageEnum.ACCEPTED, VisitStageEnum.CHECK_IN,
                VisitStageEnum.COMPLETED, VisitStageEnum.NOSHOW);

        DELETE_VISIT_INTERVIEW_SETUP.allowVisitStageList = List.of(VisitStageEnum.ACCEPTED, VisitStageEnum.CHECK_IN,
                VisitStageEnum.COMPLETED, VisitStageEnum.NOSHOW);

        CREATE_VISIT_DOCUMENT.allowVisitStageList = List.of(VisitStageEnum.ACCEPTED, VisitStageEnum.COMPLETED,
                VisitStageEnum.CHECK_IN, VisitStageEnum.CANCELLED, VisitStageEnum.EXPIRED, VisitStageEnum.DECLINED,
                VisitStageEnum.CLOSED, VisitStageEnum.NOSHOW);

        DELETE_VISIT_DOCUMENT.allowVisitStageList = List.of(VisitStageEnum.ACCEPTED, VisitStageEnum.COMPLETED,
                VisitStageEnum.CHECK_IN, VisitStageEnum.CANCELLED, VisitStageEnum.EXPIRED, VisitStageEnum.DECLINED,
                VisitStageEnum.CLOSED, VisitStageEnum.NOSHOW);
    }
}
