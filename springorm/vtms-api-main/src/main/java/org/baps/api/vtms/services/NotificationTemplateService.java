package org.baps.api.vtms.services;

import org.baps.api.vtms.enumerations.NotificationTemplateEnum;
import org.baps.api.vtms.models.entities.NotificationTemplate;
import org.baps.api.vtms.repositories.NotificationTemplateRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationTemplateService {

    private final NotificationTemplateRepository notificationTemplateRepository;
    
    /**
     * Retrieve a notification template configuration based on the specified notification template.
     *
     * @param notificationTemplateEnum The notification template enum to search for.
     * @return Optional containing the matching notification template configuration if found, otherwise empty.
     */
    public Optional<NotificationTemplate> findByNotificationTemplate(final NotificationTemplateEnum notificationTemplateEnum) {
        
        // Retrieve the notification template configuration from the repository.
        final Optional<NotificationTemplate> optionalNotificationTemplate = notificationTemplateRepository
                .findByNotificationTemplateEnum(notificationTemplateEnum);
        
        // Log an error if the configuration is not found.
        if (optionalNotificationTemplate.isEmpty()) {
            log.error("NotificationTemplate with notificationTemplateEnum {} not found.",
                    notificationTemplateEnum);
        }
        
        return optionalNotificationTemplate;
    }
}
