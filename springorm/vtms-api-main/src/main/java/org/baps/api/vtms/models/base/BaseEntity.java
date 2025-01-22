package org.baps.api.vtms.models.base;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -1L;

    @CreatedDate
    @Column(name = "`created_at`", columnDefinition = "timestamp", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "`updated_at`", columnDefinition = "timestamp")
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "`created_by`")
    private String createdBy;

    @LastModifiedBy
    @Column(name = "`updated_by`")
    private String updatedBy;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "`status`", nullable = false)
    private Status status = Status.ACTIVE;

}
