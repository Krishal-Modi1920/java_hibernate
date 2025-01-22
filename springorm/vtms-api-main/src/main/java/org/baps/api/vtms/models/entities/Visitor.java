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
@Table(name = "visitors")
@DynamicUpdate
@SQLDelete(sql = "UPDATE visitors SET status = 'DELETED' WHERE visitor_id=?")
@Where(clause = "status != 'DELETED'")
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Visitor extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -468207389272565933L;

    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(name = "visitor_id", length = 36)
    private String visitorId;

    @Column(name = "salutation", length = 10, nullable = false)
    private String salutation;

    @Column(name = "first_name", length = 64, nullable = false)
    private String firstName;

    @Column(name = "middle_name", length = 64)
    private String middleName;

    @Column(name = "last_name", length = 64, nullable = false)
    private String lastName;

    @Column(name = "gender", length = 6, nullable = false)
    private String gender;

    @Column(name = "address_line_1", length = 100)
    private String addressLine1;

    @Column(name = "address_line_2", length = 100)
    private String addressLine2;

    @Column(name = "country", length = 36)
    private String country;

    @Column(name = "state", length = 36)
    private String state;

    @Column(name = "city", length = 36)
    private String city;

    @Column(name = "postal_code", length = 8)
    private String postalCode;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "phone_country_code", length = 5, nullable = false)
    private String phoneCountryCode;

    @Column(name = "phone_number", length = 12, nullable = false)
    private String phoneNumber;

    @Column(name = "preferred_comm_mode", length = 20)
    private String preferredCommMode;

    @Column(name = "designation", length = 56)
    private String designation;

    @Column(name = "organization_name", length = 255)
    private String organizationName;

    @Column(name = "organization_address", length = 255)
    private String organizationAddress;

    @Column(name = "organization_website", length = 255)
    private String organizationWebsite;

    @Column(name = "telegram_id", length = 255)
    private String telegramId;

    @Column(name = "facebook_id", length = 255)
    private String facebookId;

    @Column(name = "linkedin_id", length = 255)
    private String linkedinId;

    @Column(name = "twitter_id", length = 255)
    private String twitterId;

    @Column(name = "instagram_id", length = 255)
    private String instagramId;

    @Column(name = "comments", length = 512)
    private String comments;

    @ToString.Exclude
    @OneToMany(mappedBy = "visitor", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VisitVisitor> visitVisitorList = new ArrayList<>();

    @Column(name = "visitor_type", length = 128)
    private String visitorType;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }

        if (o instanceof Visitor visitor) {
            return Objects.equals(visitorId, visitor.visitorId);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
