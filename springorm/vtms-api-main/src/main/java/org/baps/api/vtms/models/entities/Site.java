package org.baps.api.vtms.models.entities;

import org.baps.api.vtms.models.base.BaseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.io.Serial;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Getter
@Setter
@ToString
@Entity
@DynamicUpdate
@Table(name = "sites")
@SQLDelete(sql = "UPDATE sites SET status = 'DELETED' WHERE site_id=?")
@Where(clause = "status != 'DELETED'")
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Site extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 2701958127860245540L;

    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(name = "site_id", length = 36)
    private String siteId;

    @Column(name = "uucode", length = 8, nullable = false)
    private String uuCode;

    @Column(name = "name", length = 64, nullable = false)
    private String name;

    @Column(name = "short_name", length = 64)
    private String shortName;

    @Column(name = "description", length = 100)
    private String description;

    @Column(name = "time_zone", length = 16, nullable = false)
    private String timeZone;

    @Column(name = "date_format", length = 12)
    private String dateFormat;
    
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @ToString.Exclude
    @OneToMany(mappedBy = "site", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Visit> visitList = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "site", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ServiceTemplate> serviceTemplateList = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "site", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Location> locationList = new ArrayList<>();

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }

        if (o instanceof Site site) {
            return Objects.equals(siteId, site.siteId);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
