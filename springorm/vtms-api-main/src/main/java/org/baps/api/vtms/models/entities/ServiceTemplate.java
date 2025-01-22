package org.baps.api.vtms.models.entities;

import org.baps.api.vtms.enumerations.ServiceTypeEnum;
import org.baps.api.vtms.models.base.BaseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Getter
@Setter
@ToString
@Entity
@Table(name = "services")
@SQLDelete(sql = "UPDATE services SET status = 'DELETED' WHERE service_id=?")
@Where(clause = "status != 'DELETED'")
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class ServiceTemplate extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 8323704534660252858L;

    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(name = "service_id", length = 36)
    private String serviceTemplateId;

    @Column(name = "name", length = 64)
    private String name;

    @Column(name = "description", length = 100)
    private String description;

    @Column(name = "type", length = 16, nullable = false)
    @Enumerated(EnumType.STRING)
    private ServiceTypeEnum serviceTypeEnum;

    @Column(name = "sub_type", length = 16)
    private String subType;

    @JdbcTypeCode(value = SqlTypes.JSON)
    @Column(name = "lang_meta", columnDefinition = "jsonb")
    private JsonNode langMeta;

    @JdbcTypeCode(value = SqlTypes.JSON)
    @Column(name = "fields", columnDefinition = "jsonb")
    private JsonNode fields;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(
        name = "site_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "site_id")
    )
    private Site site;
    
    @Column(name = "is_check_visit_time", nullable = false)
    private boolean checkVisitTime;

    @ToString.Exclude
    @OneToMany(mappedBy = "serviceTemplate", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VisitService> visitServiceList = new ArrayList<>();
    
    @ToString.Exclude
    @OneToMany(mappedBy = "serviceTemplate", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceLocation> serviceLocationList = new ArrayList<>();

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }

        if (o instanceof ServiceTemplate service) {
            return Objects.equals(serviceTemplateId, service.serviceTemplateId);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
