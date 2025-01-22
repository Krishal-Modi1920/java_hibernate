package org.baps.api.vtms.models.entities;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Getter
@Setter
@ToString
@Entity
@DynamicUpdate
@Table(name = "tour_slot_personnel")
@SQLDelete(sql = "UPDATE tour_slot_personnel SET status = 'DELETED' WHERE tour_slot_personnel_id=?")
@Where(clause = "status != 'DELETED'")
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class TourSlotPersonnel extends BaseEntity {
    
    @Serial
    private static final long serialVersionUID = 6115984899004159143L;
    
    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(name = "`tour_slot_personnel_id`", length = 36)
    private String tourSlotPersonnelId;
    
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(
        name = "tour_slot_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "tour_slot_id")
    )
    private TourSlot tourSlot;
    
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(
        name = "personnel_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "personnel_id")
    )
    private Personnel personnel;
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        if (o instanceof TourSlotPersonnel tourSlotPersonnel) {
            return Objects.equals(tourSlotPersonnelId, tourSlotPersonnel.tourSlotPersonnelId);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}