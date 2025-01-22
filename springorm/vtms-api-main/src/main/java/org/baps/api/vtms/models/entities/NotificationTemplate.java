package org.baps.api.vtms.models.entities;

import org.baps.api.vtms.enumerations.NotificationTemplateEnum;
import org.baps.api.vtms.models.base.BaseEntity;
import org.baps.api.vtms.models.notification.NotificationChannelEnum;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.io.Serial;
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Getter
@Setter
@ToString
@Entity
@Table(name = "notification_templates")
@SQLDelete(sql = "UPDATE notification_templates SET status = 'DELETED' WHERE notification_template_id=?")
@Where(clause = "status != 'DELETED'")
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class NotificationTemplate extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 5447883337809321722L;

    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(name = "notification_template_id", length = 36)
    private String notificationTemplateConfigId;

    @Column(name = "notification_template", length = 64, nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationTemplateEnum notificationTemplateEnum;

    @Column(name = "channel", length = 16, nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationChannelEnum notificationChannelEnum;

    @Column(name = "temmpate_id", nullable = false, length = 36)
    private String templateId;
    
    @Column(name = "version", nullable = false)
    private int version;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }

        if (o instanceof NotificationTemplate notificationTemplateConfig) {
            return Objects.equals(notificationTemplateConfigId, notificationTemplateConfig.notificationTemplateConfigId);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
