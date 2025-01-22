package org.baps.api.vtms.constants;

import lombok.experimental.UtilityClass;

import java.util.Set;

@UtilityClass
public class NotificationConstant {
    
    public static final String EMAIL_TYPE_PRIMARY = "PRIMARY";
    
    public static final String BAPS_HELP_DESK_NUMBER = "+1 609-918-1212";

    public static final String SERVICE_NAME = "serviceName";

    public static final String SERVICE_TYPE = "serviceType";
    
    public static final String HELP_DESK_NUMBER = "helpDeskPhoneNumber";
    
    public static final String LABEL = "label";
    
    public static final String RECIPIENT_NAME = "recipientName";
    
    public static final String VISIT_REQUEST_NUMBER = "visitRequestNumber";
    
    public static final String TYPE_OF_VISIT = "typeOfVisit";
    
    public static final String VISIT_PAGE_LINK = "visitLink";

    public static final String VISIT_DETAIL_PAGE_LINK = "visitDetailLink";

    public static final String TOTAL_VISITOR = "totalVisitors";
    
    public static final String VISIT_START_DATE = "visitStartDate";
    
    public static final String VISIT_START_TIME = "visitStartTime";
    
    public static final String VISIT_END_TIME = "visitEndTime";
    
    public static final String REQUESTER_NAME = "requesterName";
    
    public static final String PRIMARY_VISITOR_NAME = "primaryVisitorName";
    
    public static final String ORGANIZATION_NAME = "organizationName";
    
    public static final String REQUESTED_SERVICES = "requestedServices";
    
    public static final String ROLE_NAME = "roleName";
    
    public static final String VISIT_ADMIN_NAME = "visitAdminName";

    public static final String VISIT_ADMIN_PHONE_NUMBER = "visitAdminPhoneNumber";

    public static final String GUEST_VISIT_COORDINATOR_NAME = "guestVisitCoordinatorName";

    public static final String GUEST_VISIT_COORDINATOR_PHONE_NUMBER = "guestVisitCoordinatorPhoneNumber";

    public static final String VISIT_DECLINED_STAGE_REASON = "visitDeclinedStageReason";

    public static final String VISIT_CANCELLED_STAGE_REASON = "visitCancelledStageReason";

    public static final String VISIT_CANCELLED_BY_VISITOR_REASON = "The visitor has changed his plans.";

    public static final String MEETING_START_TIME = "meetingStartTime";

    public static final String MEETING_END_TIME = "meetingEndTime";

    public static final String VISIT_FEEDBACK_LINK = "visitFeedbackLink";

    public static final String VISIT_DATE = "visitDate";

    public static final String VISIT_LIST_DATA = "visitListData";

    public static final String VISIT_FEEDBACK_PATH = "/feedback";
    
    public static final String PUBLIC_VISIT_PAGE_LINK = "publicVisitLink";

    public static final String VISIT_DETAIL_PATH =
        "/visits/all?visitStatus=ALL&pageSize=10&currentPage=1&searchText=&visitType=&startDate=&endDate=&sortProperty=&sortDirection=&id=";

    public static final String TOUR_VISIT_PATH = "/visit/tours/";
    
    public static final String HOURLY_VISIT_PATH = "/visit/hourly/";
    
    public static final Set<String> VISIT_APPROVAL_PENDING_EMAIL_PARAMS =
        Set.of(PRIMARY_VISITOR_NAME, TOTAL_VISITOR, VISIT_PAGE_LINK, ORGANIZATION_NAME, VISIT_REQUEST_NUMBER, REQUESTER_NAME,
            VISIT_START_DATE, VISIT_START_TIME, VISIT_END_TIME, TYPE_OF_VISIT);
    
    public static final Set<String> VISIT_REQUEST_RECEIVED_EMAIL_PARAMS =
        Set.of(RECIPIENT_NAME, TOTAL_VISITOR, VISIT_START_DATE, VISIT_START_TIME, VISIT_END_TIME, PUBLIC_VISIT_PAGE_LINK);
    
    public static final Set<String> VISIT_CREATED_SUCCESSFULLY_EMAIL_PARAMS =
        Set.of(RECIPIENT_NAME, PRIMARY_VISITOR_NAME, TOTAL_VISITOR, VISIT_PAGE_LINK, ORGANIZATION_NAME, VISIT_REQUEST_NUMBER,
            TYPE_OF_VISIT, VISIT_START_DATE, VISIT_START_TIME, VISIT_END_TIME);
    
    public static final Set<String> VISIT_CONFIRMATION_EMAIL_PARAMS =
        Set.of(RECIPIENT_NAME, HELP_DESK_NUMBER, VISIT_START_DATE, VISIT_START_TIME, VISIT_END_TIME, VISIT_REQUEST_NUMBER,
            PRIMARY_VISITOR_NAME, TOTAL_VISITOR, TYPE_OF_VISIT, ORGANIZATION_NAME, REQUESTED_SERVICES);
    
    public static final Set<String> NEW_VISIT_ASSIGNED_EMAIL_PARAM =
        Set.of(RECIPIENT_NAME, ROLE_NAME, VISIT_ADMIN_NAME, VISIT_REQUEST_NUMBER, PRIMARY_VISITOR_NAME, VISIT_START_DATE,
            VISIT_START_TIME, VISIT_END_TIME, TOTAL_VISITOR, TYPE_OF_VISIT, ORGANIZATION_NAME, VISIT_PAGE_LINK);
    
    public static final Set<String> VISIT_ACCEPTED_SUCCESSFULLY_EMAIL_PARAM =
        Set.of(RECIPIENT_NAME, VISIT_REQUEST_NUMBER, PRIMARY_VISITOR_NAME, VISIT_START_DATE, VISIT_START_TIME,
            VISIT_END_TIME, TOTAL_VISITOR, TYPE_OF_VISIT, ORGANIZATION_NAME, VISIT_PAGE_LINK);
    
    public static final Set<String> VISIT_DECLINED_EMAIL_PARAMS =
        Set.of(RECIPIENT_NAME, VISIT_DECLINED_STAGE_REASON, VISIT_REQUEST_NUMBER, VISIT_START_DATE, VISIT_START_TIME,
            VISIT_END_TIME, TOTAL_VISITOR, TYPE_OF_VISIT, ORGANIZATION_NAME, VISIT_PAGE_LINK);

    public static final Set<String> VISIT_CANCELLED_SEND_EMAIL_TO_VISITOR_PARAMS =
        Set.of(RECIPIENT_NAME, VISIT_REQUEST_NUMBER, VISIT_START_DATE, VISIT_START_TIME,
            VISIT_END_TIME, TOTAL_VISITOR, TYPE_OF_VISIT, ORGANIZATION_NAME, VISIT_PAGE_LINK, VISIT_CANCELLED_STAGE_REASON);

    public static final Set<String> VISIT_CANCELLED_BY_ADMIN_EMAIL_PARAMS =
        Set.of(RECIPIENT_NAME, VISIT_CANCELLED_STAGE_REASON, VISIT_REQUEST_NUMBER, VISIT_START_DATE, VISIT_START_TIME,
            VISIT_END_TIME, TOTAL_VISITOR, TYPE_OF_VISIT, ORGANIZATION_NAME, VISIT_PAGE_LINK);

    public static final Set<String> VISIT_ASSIGNED_EMAIL_PARAMS =
        Set.of(RECIPIENT_NAME, ROLE_NAME, VISIT_REQUEST_NUMBER, VISIT_START_DATE, VISIT_START_TIME,
            VISIT_END_TIME, TOTAL_VISITOR, TYPE_OF_VISIT, ORGANIZATION_NAME, VISIT_DETAIL_PAGE_LINK,
            VISIT_ADMIN_NAME, VISIT_ADMIN_PHONE_NUMBER, GUEST_VISIT_COORDINATOR_NAME, GUEST_VISIT_COORDINATOR_PHONE_NUMBER,
            SERVICE_TYPE, SERVICE_NAME);
    public static final Set<String> MEETING_WITH_GUEST_EMAIL_PARAMS = Set.of(
        RECIPIENT_NAME, PRIMARY_VISITOR_NAME, VISIT_START_DATE, VISIT_START_TIME, VISIT_END_TIME,
        MEETING_START_TIME, MEETING_END_TIME, VISIT_DETAIL_PAGE_LINK
    );

    public static final Set<String> VISIT_FEEDBACK_EMAIL_PARAMS = Set.of(RECIPIENT_NAME, VISIT_FEEDBACK_LINK);

    public static final Set<String> DAILY_VISIT_PARAMS = Set.of(VISIT_LIST_DATA);
}
