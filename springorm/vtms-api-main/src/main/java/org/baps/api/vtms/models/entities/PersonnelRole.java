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
import jakarta.persistence.Table;

@Getter
@Setter
@ToString
@Entity
@Table(name = "personnel_role")
@SQLDelete(sql = "UPDATE visit_visitor SET status = 'DELETED' WHERE visit_visitor_id=?")
@Where(clause = "status != 'DELETED'")
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class PersonnelRole extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 6332273432403621415L;

    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(name = "personnel_role_id", length = 36)
    private String personnelRoleId;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(
        name = "personnel_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "personnel_id")
    )
    private Personnel personnel;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(
        name = "role_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "role_id")
    )
    private Role role;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(
        name = "site_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "site_id")
    )
    private Site site;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }

        if (o instanceof PersonnelRole personnelRole) {
            return Objects.equals(personnelRoleId, personnelRole.personnelRoleId);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
