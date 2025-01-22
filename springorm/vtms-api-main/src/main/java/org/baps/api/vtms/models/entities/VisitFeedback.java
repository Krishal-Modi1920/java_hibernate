package org.baps.api.vtms.models.entities;

import org.baps.api.vtms.models.FeedBackRatingModel;
import org.baps.api.vtms.models.base.BaseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
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
@Table(name = "feedbacks")
@SQLDelete(sql = "UPDATE feedbacks SET status = 'DELETED' WHERE feedback_id=?")
@Where(clause = "status != 'DELETED'")
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class VisitFeedback extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1165911033129606151L;

    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(name = "feedback_id", length = 36)
    private String visitFeedbackId;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(
        name = "visit_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "visit_id")
    )
    private Visit visit;
    
    @Type(value = JsonBinaryType.class)
    @Column(name = "visitor_general_feedback_rating", columnDefinition = "jsonb")
    private List<FeedBackRatingModel> feedBackRatingModelListForGeneralFeedBack;

    @Column(name = "comment", length = 512)
    private String comment;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }

        if (o instanceof VisitFeedback visitFeedback) {
            return Objects.equals(visitFeedbackId, visitFeedback.visitFeedbackId);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}