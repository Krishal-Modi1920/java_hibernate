package org.baps.api.vtms.models.entities;

import org.baps.api.vtms.models.ChildLookupModel;
import org.baps.api.vtms.models.base.BaseEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;

import java.io.Serial;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JsonNode;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Getter
@Setter
@ToString
@Entity
@Table(name = "lookup")
@SQLDelete(sql = "UPDATE lookup SET status = 'DELETED' WHERE lookup_id=?")
@Where(clause = "status != 'DELETED'")
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class Lookup extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 8734397634110758191L;

    @Id
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "uuid2")
    @Column(name = "lookup_id", length = 36)
    private String lookupId;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    @JoinColumn(
        name = "parent_lookup_id",
        foreignKey = @ForeignKey(name = "lookup_id")
    )
    private Lookup parentLookup;

    @Column(name = "key", length = 64, nullable = false)
    private String key;

    @Column(name = "value", length = 255, nullable = false)
    private String value;

    @JdbcTypeCode(value = SqlTypes.JSON)
    @Column(name = "lang_meta", columnDefinition = "jsonb")
    private JsonNode langMeta;
     
    @Type(value = JsonBinaryType.class)
    @Column(name = "child_lookup", columnDefinition = "jsonb")
    private List<ChildLookupModel> childLookupModelList;
    
    @Column(name = "seq_number", nullable = false)
    private Integer sequenceNumber;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }

        if (o instanceof Lookup lookup) {
            return Objects.equals(lookupId, lookup.lookupId);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}