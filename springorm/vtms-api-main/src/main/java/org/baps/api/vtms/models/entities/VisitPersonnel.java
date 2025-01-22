package org.baps.api.vtms.models.entities;

import org.baps.api.vtms.enumerations.RoleTagEnum;
import org.baps.api.vtms.models.CommentModel;
import org.baps.api.vtms.models.FeedBackRatingModel;
import org.baps.api.vtms.models.base.BaseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

import java.io.Serial;
import java.util.List;
import java.util.Objects;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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

@Getter
@Setter
@ToString
@Entity
@Table(name = "visit_personnel")
@DynamicUpdate
@SQLDelete(sql = "UPDATE visit_personnel SET status = 'DELETED' WHERE visit_personnel_id=?")
@Where(clause = "status != 'DELETED'")
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class VisitPersonnel extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -1227109873197424655L;

    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(name = "visit_personnel_id", length = 36)
    private String visitPersonnelId;

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
        name = "personnel_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "personnel_id")
    )
    private Personnel personnel;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(
        name = "role_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "role_id")
    )
    private Role role;

    @Column(name = "tag", length = 64, nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleTagEnum roleTagEnum;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(
        name = "visit_service_id",
        nullable = true,
        foreignKey = @ForeignKey(name = "visit_service_id"))
    private VisitService visitService;
    
    @Type(value = JsonBinaryType.class)
    @Column(name = "visitor_rating", columnDefinition = "jsonb")
    private List<FeedBackRatingModel> feedBackRatingModelListForTourGuide;

    @Type(value = JsonBinaryType.class)
    @Column(name = "personnel_feedback", columnDefinition = "jsonb")
    private CommentModel personnelFeedback;

    @ToString.Exclude
    @OneToOne(mappedBy = "interviewVolunteerVisitPersonnel", fetch = FetchType.LAZY)
    private VisitLocation visitLocation;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }

        if (o instanceof VisitPersonnel visitPersonnel) {
            return Objects.equals(visitPersonnelId, visitPersonnel.visitPersonnelId);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}