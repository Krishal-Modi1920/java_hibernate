package org.baps.api.vtms.constants;

import org.baps.api.vtms.enumerations.RoleTagEnum;
import org.baps.api.vtms.enumerations.ServiceTypeEnum;

import lombok.experimental.UtilityClass;

import java.time.format.DateTimeFormatter;
import java.util.List;

@UtilityClass
public class GeneralConstant {


    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";

    public static final String DATE_FORMAT_MM_DD_YYYY = "MM-dd-yyyy";
    
    public static final String TIME_FORMAT_12 = "hh:mm a";
    
    public static final String TIME_FORMAT_24 = "HH:mm:ss";

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    
    public static final DateTimeFormatter DATE_FORMATTER_YYYY_MM_DD = DateTimeFormatter.ofPattern(DATE_FORMAT_YYYY_MM_DD);

    public static final DateTimeFormatter DATE_FORMATTER_MM_DD_YYYY = DateTimeFormatter.ofPattern(DATE_FORMAT_MM_DD_YYYY);

    public static final DateTimeFormatter TIME_FORMAT_12_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT_12);    
    
    public static final DateTimeFormatter TIME_FORMAT_24_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT_24);

    public static final String VALID_VALUES = "{validValues}";

    public static final String PATH_SEPARATOR = "/";

    public static final String PUBLIC = "public";

    public static final int ZERO_ZERO = 00;
    
    public static final int ZERO_TWO = 02;
    
    public static final int TWENTY_THREE = 23;
    
    public static final int FIFTY_NINE = 59;
    
    public static final int THIRTY = 30;
    
    public static final String TOUR_MANAGEMENT = "Tour Management";
    
    public static final String AUDIO_VIDEO = "Audio-Video";
    
    public static final String X_APP_PUBLIC_TOKEN_KEY =  "x-app-public-token-key";

    public static final String X_APP_SITE_UUCODE = "x-app-site-uucode";

    public static final String X_APP_API_VERSION = "x-app-api-version";

    public static final String API_VERSION_V1 = X_APP_API_VERSION + "=v1";
    
    public static final String UTC = "UTC";
    
    public static final RoleTagEnum SUPER_ADMIN_TAG = RoleTagEnum.SUPER_ADMIN_TAG;
    
    public static final RoleTagEnum TEAM_ROLE_TAG = RoleTagEnum.TEAM;
    
    public static final RoleTagEnum TOUR_COORDINATOR_ROLE_TAG = RoleTagEnum.TOUR_COORDINATOR;

    public static final RoleTagEnum TOUR_GUIDE_ROLE_TAG = RoleTagEnum.TOUR_GUIDE;

    public static final List<RoleTagEnum> TOUR_ROLE_TAG_LIST = List.of(TOUR_COORDINATOR_ROLE_TAG, TOUR_GUIDE_ROLE_TAG);

    public static final List<RoleTagEnum> INTERVIEW_SETUP_ROLE_TAG_LIST = List.of(RoleTagEnum.INTERVIEW_SETUP_COORDINATOR,
            RoleTagEnum.INTERVIEW_SETUP_VOLUNTEER);
    
    public static final List<RoleTagEnum> SERVICE_OR_MEETING_ROLE_TAG_LIST = List.of(RoleTagEnum.MEETING_COORDINATOR,
            RoleTagEnum.SERVICE_COORDINATOR);

    public static final ServiceTypeEnum TOUR_SERVICE_TYPE_ENUM = ServiceTypeEnum.TOUR;
}
