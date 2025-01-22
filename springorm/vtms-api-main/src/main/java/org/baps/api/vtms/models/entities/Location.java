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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
@Table(name = "`locations`")
@SQLDelete(sql = "UPDATE locations SET status = 'DELETED' WHERE location_id=?")
@Where(clause = "status != 'DELETED'")
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Location extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -6349137584136597114L;

    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(name = "`location_id`", length = 36)
    private String locationId;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(
            name = "`parent_location_id`",
            nullable = true,
            foreignKey = @ForeignKey(name = "`location_id`")
            )
    private Location parentLocation;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(
            name = "`site_id`",
            nullable = false,
            foreignKey = @ForeignKey(name = "`site_id`")
            )
    private Site site;

    @Column(name = "`name`", length = 64, nullable = false)
    private String name;

    @Column(name = "`duration`", nullable = false)
    private int duration;

    @Column(name = "`sequence`", nullable = false)
    private int sequence;

    @ToString.Exclude
    @OneToMany(mappedBy = "location", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VisitLocation> visitLocationList = new ArrayList<>();
    
    @ToString.Exclude
    @OneToMany(mappedBy = "location", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceLocation> serviceLocationList = new ArrayList<>();

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }

        if (o instanceof Location location) {
            return Objects.equals(locationId, location.locationId);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}