package org.baps.api.vtms.models.entities;

import org.baps.api.vtms.enumerations.VisitStageEnum;
import org.baps.api.vtms.enumerations.VisitTypeEnum;
import org.baps.api.vtms.enumerations.VisitorContactTypeEnum;
import org.baps.api.vtms.models.DocumentModel;
import org.baps.api.vtms.models.StageModel;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.apache.commons.collections.CollectionUtils;

@Getter
@Setter
@ToString
@Entity
@Table(name = "visits")
@DynamicUpdate
@SQLDelete(sql = "UPDATE visits SET status = 'DELETED' WHERE visit_id=?")
@Where(clause = "status != 'DELETED'")
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Visit extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -1363869399567636735L;

    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(name = "visit_id", length = 36)
    private String visitId;

    @Column(name = "request_number", length = 16)
    private String requestNumber;

    @Column(name = "type", length = 64, nullable = false)
    @Enumerated(EnumType.STRING)
    private VisitTypeEnum visitTypeEnum;

    @Column(name = "type_of_visit", length = 128)
    private String typeOfVisit;

    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time")
    private LocalDateTime endDateTime;

    @Column(name = "total_visitors")
    private Integer totalVisitors;

    @Column(name = "child_female_count")
    private Integer childFemaleCount;

    @Column(name = "child_male_count")
    private Integer childMaleCount;

    @Column(name = "adult_female_count")
    private Integer adultFemaleCount;

    @Column(name = "adult_male_count")
    private Integer adultMaleCount;

    @Column(name = "senior_female_count")
    private Integer seniorFemaleCount;

    @Column(name = "senior_male_count")
    private Integer seniorMaleCount;
    
    @Column(name = "device_id", length = 128)
    private String deviceId;

    @Column(name = "requester_notes", length = 512)
    private String requesterNotes;
    
    @Type(value = JsonBinaryType.class)
    @Column(name = "requested_services", columnDefinition = "jsonb")
    private Set<String> requestedServiceIds;

    @Column(name = "visitor_comments", length = 512)
    private String visitorComments;

    @Column(name = "stage", length = 12, nullable = false)
    @Enumerated(EnumType.STRING)
    private VisitStageEnum visitStageEnum;

    @Type(value = JsonBinaryType.class)
    @Column(name = "stage_history", columnDefinition = "jsonb")
    private List<StageModel> stageModelList;

    @Type(value = JsonBinaryType.class)
    @Column(name = "documents", columnDefinition = "jsonb")
    private List<DocumentModel> documentModelList;

    @ToString.Exclude
    @OneToMany(mappedBy = "visit", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VisitVisitor> visitVisitorList = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "visit", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VisitPersonnel> visitPersonnelList = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "visit", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VisitService> visitServiceList = new ArrayList<>();

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(
        name = "site_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "site_id")
    )
    private Site site;

    @ToString.Exclude
    @OneToOne(mappedBy = "visit", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private VisitFeedback visitFeedback;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(
        name = "tour_slot_id",
        nullable = true,
        foreignKey = @ForeignKey(name = "tour_slot_id")
    )
    private TourSlot tourSlot;
    
    @Column(name = "point_of_contact", length = 64, nullable = false)
    @Enumerated(EnumType.STRING)
    private VisitorContactTypeEnum pointOfContact;

    @Column(name = "tour_type", length = 64)
    private String tourType;

    @ToString.Exclude
    @OneToOne(mappedBy = "visit", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private VisitPublicFeedback visitPublicFeedback;

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate;
    
    public void setVisitFeedback(final VisitFeedback visitFeedback) {
        visitFeedback.setVisit(this);
        this.visitFeedback = visitFeedback;
    }

    public void addVisitVisitor(final VisitVisitor visitVisitor) {
        visitVisitorList.add(visitVisitor);
        visitVisitor.setVisit(this);
    }

    public void addVisitService(final VisitService visitService) {
        visitServiceList.add(visitService);
        visitService.setVisit(this);
    }

    public void addStageHistory(final StageModel stageModel) {
        if (CollectionUtils.isEmpty(this.stageModelList)) {
            this.stageModelList = new ArrayList<>();
        }
        this.stageModelList.add(stageModel);
    }

    public void addDocument(final DocumentModel documentModel) {
        if (CollectionUtils.isEmpty(this.documentModelList)) {
            this.documentModelList = new ArrayList<>();
        }
        this.documentModelList.add(documentModel);
    }

    public void addVisitPersonnel(final VisitPersonnel visitPersonnel) {
        visitPersonnelList.add(visitPersonnel);
        visitPersonnel.setVisit(this);
    }
    
    public void setVisitBookedFeedback(final VisitPublicFeedback visitBookedFeedback) {
        visitBookedFeedback.setVisit(this);
        this.visitPublicFeedback = visitBookedFeedback;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }

        if (o instanceof Visit visit) {
            return Objects.equals(visitId, visit.visitId);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
