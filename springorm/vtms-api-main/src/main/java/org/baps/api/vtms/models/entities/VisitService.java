package org.baps.api.vtms.models.entities;

import org.baps.api.vtms.models.CommentModel;
import org.baps.api.vtms.models.base.BaseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "visit_services")
@DynamicUpdate
@SQLDelete(sql = "UPDATE visit_services SET status = 'DELETED' WHERE visit_service_id=?")
@Where(clause = "status != 'DELETED'")
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class VisitService extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 7710942615274872359L;

    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(name = "visit_service_id", length = 36)
    private String visitServiceId;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(
            name = "visit_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "visit_id")
            )
    private Visit visit;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(
            name = "service_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "service_id")
            )
    private ServiceTemplate serviceTemplate;

    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    @Column(name = "actual_start_date_time")
    private LocalDateTime actualStartDateTime;

    @Column(name = "actual_end_date_time")
    private LocalDateTime actualEndDateTime;

    @JdbcTypeCode(value = SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private JsonNode metadata;

    @Type(value = JsonBinaryType.class)
    @Column(name = "comments", columnDefinition = "jsonb")
    private List<CommentModel> commentModelList;
    
    @Column(name = "seq_number", nullable = false)
    private int seqNubmer;

    @ToString.Exclude
    @OneToMany(mappedBy = "visitService", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VisitPersonnel> visitPersonnelList = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "visitService", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VisitLocation> visitLocationList = new ArrayList<>();

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(
        name = "meeting_personnel_id",
        nullable = true,
        foreignKey = @ForeignKey(name = "personnel_id")
    )
    private Personnel meetingPersonnel;
    
    public void addVisitPersonnel(final VisitPersonnel visitPersonnel) {
        visitPersonnelList.add(visitPersonnel);
        visitPersonnel.setVisitService(this);
    }
    
    public void addVisitLocation(final VisitLocation visitLocation) {
        visitLocationList.add(visitLocation);
        visitLocation.setVisitService(this);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }

        if (o instanceof VisitService visitService) {
            return Objects.equals(visitServiceId, visitService.visitServiceId);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
