package org.baps.api.vtms.enumerations;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Getter
@RequiredArgsConstructor
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public enum VisitStageEnum {
    PENDING(true, 1),
    ACCEPTED(true, 2),
    CHECK_IN(true, 3),
    NOSHOW(true, 4),
    EXPIRED(false, 5),
    COMPLETED(true, 6),
    DECLINED(false, 7),
    CANCELLED(false, 8),
    CLOSED(false, 9);

    private final boolean change;
    
    private final int order;
    
    private List<VisitStageEnum> tourPreviousStageList;
    private List<VisitStageEnum> visitPreviousStageList;
    
    static {
        PENDING.visitPreviousStageList = null;
        ACCEPTED.visitPreviousStageList = List.of(PENDING);
        CHECK_IN.visitPreviousStageList = List.of(ACCEPTED);
        DECLINED.visitPreviousStageList = new ArrayList<>();
        CANCELLED.visitPreviousStageList = new ArrayList<>();
        EXPIRED.visitPreviousStageList = List.of(PENDING);
        COMPLETED.visitPreviousStageList = List.of(NOSHOW, ACCEPTED, CHECK_IN);
        CLOSED.visitPreviousStageList = List.of(COMPLETED);
        

        ACCEPTED.tourPreviousStageList = null;
        CANCELLED.tourPreviousStageList = new ArrayList<>();
        COMPLETED.tourPreviousStageList = List.of(ACCEPTED, NOSHOW);
        CLOSED.tourPreviousStageList = List.of(COMPLETED);
    }
}
