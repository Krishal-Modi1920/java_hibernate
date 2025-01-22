package org.baps.api.vtms.models.entities;

import org.baps.api.vtms.models.base.BaseEntity;

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
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Getter
@Setter
@ToString
@Entity
@Table(name = "visit_public_feedback")
@SQLDelete(sql = "UPDATE visit_public_feedback SET status = 'DELETED' WHERE visit_public_feedback=?")
@Where(clause = "status != 'DELETED'")
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class VisitPublicFeedback extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1165911033129606151L;

    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(name = "visit_public_feedback_id", length = 36)
    private String visitPublicFeedbackId;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(
        name = "visit_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "visit_id")
    )
    private Visit visit;

    @Column(name = "overall_rating")
    private Integer overallRating;

    @Column(name = "booking_process_rating")
    private Integer bookingProcessRating;

    @Column(name = "comment", length = 512)
    private String comment;

    @Column(name = "is_booking_feedback", nullable = false, columnDefinition = "default 'false'")
    private boolean isBookingFeedback;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }

        if (o instanceof VisitPublicFeedback visitFeedback) {
            return Objects.equals(visitPublicFeedbackId, visitFeedback.visitPublicFeedbackId);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}