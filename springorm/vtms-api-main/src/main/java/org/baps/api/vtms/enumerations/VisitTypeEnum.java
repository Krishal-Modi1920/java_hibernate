package org.baps.api.vtms.enumerations;

import lombok.Getter;

@Getter
public enum VisitTypeEnum {

    VISIT("VM"),
    TOUR("TM");

    private final String shortName;

    VisitTypeEnum(final String shortName) {
        this.shortName = shortName;
    }

}
