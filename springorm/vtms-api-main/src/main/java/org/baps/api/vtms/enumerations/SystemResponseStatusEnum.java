package org.baps.api.vtms.enumerations;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SystemResponseStatusEnum {

    PERSONNEL_NOT_EXISTS_IN_VISIT(-1000),
    
    CHECK_PERMISSION(-2000);

    private final int status;

}
