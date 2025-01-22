package org.baps.api.vtms.models.entities;

import org.baps.api.vtms.enumerations.TourSlotStageEnum;
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
import org.hibernate.annotations.Where;

import java.io.Serial;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Getter
@Setter
@ToString
@Entity
@DynamicUpdate
@Table(name = "tour_slots")
@SQLDelete(sql = "UPDATE tour_slots SET status = 'DELETED' WHERE tour_slot_id=?")
@Where(clause = "status != 'DELETED'")
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class TourSlot extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -8619035087651197438L;

    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(name = "tour_slot_id", length = 36)
    private String tourSlotId;

    @Column(name = "start_date_time", nullable = false)
    private LocalDateTime startDateTime;

    @Column(name = "end_date_time", nullable = false)
    private LocalDateTime endDateTime;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(
            name = "`site_id`",
            nullable = false,
            foreignKey = @ForeignKey(name = "`site_id`")
            )
    private Site site;

    @Enumerated(EnumType.STRING)
    @Column(name = "stage", nullable = false)
    private TourSlotStageEnum tourSlotStageEnum = TourSlotStageEnum.INACTIVE;

    @Column(name = "max_guest_size", nullable = false)
    private int maxGuestSize;

    @ToString.Exclude
    @OneToMany(mappedBy = "tourSlot", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TourSlotPersonnel> tourSlotPersonnelList = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "tourSlot", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Visit> visitList = new ArrayList<>();
    
    public void addTourSlotPersonnel(final TourSlotPersonnel tourSlotPersonnel) {
        tourSlotPersonnelList.add(tourSlotPersonnel);
        tourSlotPersonnel.setTourSlot(this);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        if (o instanceof TourSlot tourSlot) {
            return Objects.equals(tourSlotId, tourSlot.tourSlotId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @PrePersist
    public void prePersist() {
        this.endDateTime = endDateTime.minusSeconds(1);
    }
}