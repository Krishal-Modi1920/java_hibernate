package org.baps.api.vtms.repositories;

import org.baps.api.vtms.enumerations.NotificationTemplateEnum;
import org.baps.api.vtms.models.entities.NotificationTemplate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, String> {

    Optional<NotificationTemplate> findByNotificationTemplateEnum(NotificationTemplateEnum notificationTemplateEnum);

}
