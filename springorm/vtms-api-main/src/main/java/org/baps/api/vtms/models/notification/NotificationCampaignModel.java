package org.baps.api.vtms.models.notification;

import lombok.Builder;
import lombok.Data;

import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Data
@Builder
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class NotificationCampaignModel {

    private String templateId;

    private Integer templateVersion;

    private NotificationChannelEnum channel;

    private List<NotificationUser> users;
}
