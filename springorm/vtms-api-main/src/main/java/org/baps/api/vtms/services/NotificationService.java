package org.baps.api.vtms.services;

import org.baps.api.vtms.models.notification.NotificationCampaignModel;

public interface NotificationService {
    void sendCampaign(NotificationCampaignModel notificationCampaignModel);
}
