package org.baps.api.vtms.models.entities;

import org.baps.api.vtms.enumerations.EmailSourceEnum;
import org.baps.api.vtms.models.base.BaseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.apache.commons.collections.CollectionUtils;

@Getter
@Setter
@ToString
@Entity
@Table(name = "personnel")
@SQLDelete(sql = "UPDATE personnel SET status = 'DELETED' WHERE personnel_id=?")
@Where(clause = "status != 'DELETED'")
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Personnel extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 5585292109126618605L;

    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(name = "personnel_id", length = 36)
    private String personnelId;

    @Column(name = "personnel_uuid", length = 36)
    private String personnelUUId;

    @Column(name = "uucode", length = 9)
    private String uucode;

    @Column(name = "external_id")
    private Integer externalId;

    @Column(name = "first_name", length = 32, nullable = false)
    private String firstName;

    @Column(name = "middle_name", length = 64)
    private String middleName;

    @Column(name = "last_name", length = 64)
    private String lastName;

    @Column(name = "gender", length = 6)
    private String gender;

    @Column(name = "age_group", length = 10)
    private String ageGroup;

    @Column(name = "mandal", length = 16)
    private String mandal;

    @Column(name = "center_id", length = 16)
    private String centerId;

    @Column(name = "parazone_id", length = 16)
    private String parazoneId;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "personal_email", length = 255)
    private String personalEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "email_source", length = 30, columnDefinition = "VARCHAR(30) default 'NONE'", nullable = false)
    private EmailSourceEnum emailSourceEnum;

    @Column(name = "phone_country_code", length = 3, nullable = false)
    private String phoneCountryCode;

    @Column(name = "phone_number", length = 12, nullable = false)
    private String phoneNumber;

    @ToString.Exclude
    @OneToMany(mappedBy = "personnel", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PersonnelRole> personnelRoleList = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "personnel", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VisitPersonnel> visitPersonnelList = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "meetingPersonnel", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VisitService> meetingVisitPersonnelList = new ArrayList<>();

    public Collection<? extends GrantedAuthority> getAuthorities() {

        if (CollectionUtils.isNotEmpty(this.personnelRoleList)) {

            final List<Permission> permissionList = new ArrayList<>();
            final var existingRoleList = this.personnelRoleList.stream().map(PersonnelRole::getRole).toList();

            existingRoleList.forEach(role -> 
                    permissionList.addAll(role.getRolePermissionList().stream().map(RolePermission::getPermission)
                    .toList()));

            return permissionList.stream()
                    .map(permission -> new SimpleGrantedAuthority(permission.getPermissonEnum().name())).toList();
        }
        return Collections.emptyList();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }

        if (o instanceof Personnel personnel) {
            return Objects.equals(personnelId, personnel.personnelId);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
