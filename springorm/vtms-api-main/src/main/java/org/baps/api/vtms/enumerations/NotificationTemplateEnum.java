package org.baps.api.vtms.enumerations;

import org.baps.api.vtms.constants.NotificationConstant;
import org.baps.api.vtms.models.notification.NotificationChannelEnum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Getter
@RequiredArgsConstructor
@SuppressFBWarnings({"EI_EXPOSE_REP"})
public enum NotificationTemplateEnum {

    VISIT_APPROVAL_PENDING_EMAIL(NotificationChannelEnum.EMAIL, NotificationConstant.VISIT_APPROVAL_PENDING_EMAIL_PARAMS),
    
    VISIT_REQUEST_RECEIVED_EMAIL(NotificationChannelEnum.EMAIL, NotificationConstant.VISIT_REQUEST_RECEIVED_EMAIL_PARAMS),
    
    VISIT_CREATED_SUCCESSFULLY_EMAIL(NotificationChannelEnum.EMAIL, NotificationConstant.VISIT_CREATED_SUCCESSFULLY_EMAIL_PARAMS),
    
    VISIT_CONFIRMATION_EMAIL(NotificationChannelEnum.EMAIL, NotificationConstant.VISIT_CONFIRMATION_EMAIL_PARAMS),
    
    NEW_VISIT_ASSIGNED_EMAIL(NotificationChannelEnum.EMAIL, NotificationConstant.NEW_VISIT_ASSIGNED_EMAIL_PARAM),
    
    VISIT_ACCEPTED_SUCCESSFULLY_EMAIL(NotificationChannelEnum.EMAIL, NotificationConstant.VISIT_ACCEPTED_SUCCESSFULLY_EMAIL_PARAM),
    
    VISIT_DECLINED_EMAIL(NotificationChannelEnum.EMAIL, NotificationConstant.VISIT_DECLINED_EMAIL_PARAMS),

    VISIT_CANCELLED_SEND_EMAIL_TO_VISITOR(NotificationChannelEnum.EMAIL, NotificationConstant.VISIT_CANCELLED_SEND_EMAIL_TO_VISITOR_PARAMS),

    VISIT_CANCELLED_BY_ADMIN_EMAIL(NotificationChannelEnum.EMAIL, NotificationConstant.VISIT_CANCELLED_BY_ADMIN_EMAIL_PARAMS),

    VISIT_ASSIGNED_EMAIL(NotificationChannelEnum.EMAIL, NotificationConstant.VISIT_ASSIGNED_EMAIL_PARAMS),

    MEETING_WITH_GUEST_EMAIL(NotificationChannelEnum.EMAIL, NotificationConstant.MEETING_WITH_GUEST_EMAIL_PARAMS),

    VISIT_FEEDBACK_EMAIL(NotificationChannelEnum.EMAIL, NotificationConstant.VISIT_FEEDBACK_EMAIL_PARAMS),

    DAILY_VISIT_EMAIL(NotificationChannelEnum.EMAIL, NotificationConstant.DAILY_VISIT_PARAMS);

    private final NotificationChannelEnum channel;
    
    private final Set<String> bodyVars;
}
