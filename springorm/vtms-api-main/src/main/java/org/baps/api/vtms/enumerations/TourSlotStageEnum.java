package org.baps.api.vtms.enumerations;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Getter
@RequiredArgsConstructor
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public enum TourSlotStageEnum {

    ACTIVE(true),
    INACTIVE(true),
    BOOKED(false),
    PARTIALLY(false);
    

    private final boolean change;
}
