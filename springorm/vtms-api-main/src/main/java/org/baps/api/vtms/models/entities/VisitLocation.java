package org.baps.api.vtms.models.entities;

import org.baps.api.vtms.enumerations.LocationTagEnum;
import org.baps.api.vtms.models.base.BaseEntity;
import org.baps.api.vtms.models.base.Status;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.Objects;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.apache.commons.lang3.ObjectUtils;

@Getter
@Setter
@ToString
@Entity
@Table(name = "`visit_locations`")
@SQLDelete(sql = "UPDATE visit_locations SET status = 'DELETED' WHERE visit_location_id=?")
@Where(clause = "status != 'DELETED'")
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class VisitLocation extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 8742324524298637528L;

    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(name = "`visit_location_id`", length = 36)
    private String visitLocationId;

    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    @Column(name = "location_tag", nullable = false)
    @Enumerated(EnumType.STRING)
    private LocationTagEnum locationTagEnum;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(
        name = "visit_service_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "visit_service_id")
    )
    private VisitService visitService;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(
        name = "location_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "location_id")
    )
    private Location location;

    @Column(name = "interview_package", length = 64)
    private String interviewPackage;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(
        name = "visit_personnel_id",
        foreignKey = @ForeignKey(name = "visit_personnel_id")
    )
    private VisitPersonnel interviewVolunteerVisitPersonnel;

    public void removeInterviewVolunteerVisitPersonnel() {
        if (ObjectUtils.isNotEmpty(this.interviewVolunteerVisitPersonnel)) {
            this.interviewVolunteerVisitPersonnel.setStatus(Status.DELETED);
            this.interviewVolunteerVisitPersonnel = null;
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }

        if (o instanceof VisitLocation visitLocation) {
            return Objects.equals(visitLocationId, visitLocation.visitLocationId);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
