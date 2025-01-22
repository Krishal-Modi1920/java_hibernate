package org.baps.api.vtms.enumerations;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleEnum {

    SUPER_ADMIN,
    RELATIONSHIP_MANAGER,
    TOUR_GUIDE,
    TOUR_COORDINATOR,
    SERVICE_COORDINATOR,
    GUEST_VISIT_COORDINATOR,
    GUEST_USHER,
    VISIT_ADMIN,
    MEETING_COORDINATOR,
    INTERVIEW_SETUP_COORDINATOR,
    INTERVIEW_SETUP_VOLUNTEER,
    VOLUNTEER;
}
