package org.baps.api.vtms.repositories;

import org.baps.api.vtms.enumerations.ServiceTypeEnum;
import org.baps.api.vtms.models.base.Status;
import org.baps.api.vtms.models.entities.VisitService;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VisitServiceRepository extends JpaRepository<VisitService, String> {

    @Query("SELECT CASE WHEN COUNT(vs) > 0 THEN true ELSE false END "
            + "     FROM VisitService vs "
            + "     WHERE vs.serviceTemplate.serviceTemplateId = :serviceTemplateId "
            + "         AND vs.serviceTemplate.status != org.baps.api.vtms.models.base.Status.DELETED "
            + "         AND (:meetingPersonnelId IS NULL OR vs.meetingPersonnel.personnelId = :meetingPersonnelId) "
            + "         AND vs.visit.visitId = :visitId AND vs.visit.status != org.baps.api.vtms.models.base.Status.DELETED "
            + "         AND (:visitServiceId IS NULL OR vs.visitServiceId != :visitServiceId) "
            + "         AND ("
            + "                ((vs.startDateTime <= :endDateTime) AND (vs.endDateTime >= :starDateTime)) "
            + "             OR ((vs.actualStartDateTime <= :endDateTime) OR (vs.actualEndDateTime >= :starDateTime))"
            + "          )")
    boolean existsByVisitServiceStartEndDateTime(@Param("visitId") String visitId, @Param("visitServiceId") String visitServiceId,
            @Param("serviceTemplateId") String serviceTemplateId, @Param("meetingPersonnelId") String meetingPersonnelId,
            @Param("starDateTime") LocalDateTime starDateTime, @Param("endDateTime") LocalDateTime endDateTime);

    List<VisitService> findByVisitVisitIdAndVisitStatusNotAndServiceTemplateServiceTypeEnumAndServiceTemplateStatusNotAndVisitSiteUuCode(
            String visitId, Status visitStatus, ServiceTypeEnum serviceTypeEnum, Status serviceTemplateStatus, String siteUUCode);

    Optional<VisitService> findByVisitVisitIdAndVisitStatusNotAndVisitServiceIdAndVisitSiteUuCode(String visitId, Status visitStatus,
            String visitServiceId, String siteUUCode);

    boolean existsByVisitVisitIdAndVisitStatusNotAndServiceTemplateServiceTypeEnum(
            String visitId, Status visitStatus, ServiceTypeEnum serviceTypeEnum);
}
