package org.baps.api.vtms.repositories;

import org.baps.api.vtms.enumerations.RoleTagEnum;
import org.baps.api.vtms.models.base.Status;
import org.baps.api.vtms.models.entities.VisitPersonnel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface VisitPersonnelRepository extends JpaRepository<VisitPersonnel, String> {

    @Query("SELECT vp FROM VisitPersonnel vp "
                    + "INNER JOIN Personnel p "
                    + "     ON p.personnelId = vp.personnel.personnelId "
                    + "     AND p.status != org.baps.api.vtms.models.base.Status.DELETED "
                    + "INNER JOIN Visit v "
                    + "     ON v.visitId = vp.visit.visitId "
                    + "     AND v.status != org.baps.api.vtms.models.base.Status.DELETED "
                    + "LEFT JOIN VisitService vs "
                    + "     ON vs.visitServiceId = vp.visitService.visitServiceId "
                    + "     AND vs.status != org.baps.api.vtms.models.base.Status.DELETED "
                    + "INNER JOIN Role r "
                    + "     ON r.roleId = vp.role.roleId "
                    + "WHERE p.personnelId IN :personnelIds "
                    + "AND "
                    + "     v.site.uuCode=:siteUUCode "
                    + "AND "
                    + "     (:tourSlotId IS NULL OR v.tourSlot.tourSlotId != :tourSlotId) "
                    + "AND ("
                    + "       (     vp.visitService.visitServiceId IS NOT NULL "
                    + "         AND "
                    + "             r.status != org.baps.api.vtms.models.base.Status.DELETED "
                    + "         AND "
                    + "             r.checkAvailability = true "
                    + "         AND "
                    + "             vs.startDateTime <= :endDateTime "
                    + "         AND "
                    + "             vs.endDateTime >= :startDateTime"
                    + "     )"
                    + "     OR (    vp.visitService.visitServiceId IS NULL "
                    + "         AND "
                    + "             r.status != org.baps.api.vtms.models.base.Status.DELETED "
                    + "         AND "
                    + "             r.checkAvailability = true "
                    + "         AND "
                    + "             v.startDateTime <= :endDateTime "
                    + "         AND "
                    + "             v.endDateTime >= :startDateTime"
                    + "     )"
                    + ")")
    List<VisitPersonnel> findAssociatedVisitPersonnelInVisitOrVisitServiceByPersonnelIds(
            @Param("personnelIds") List<String> personnelIds, @Param("startDateTime") LocalDateTime startDateTime, 
            @Param("endDateTime") LocalDateTime endDateTime,  @Param("tourSlotId") String tourSlotId,
            @Param("siteUUCode") String siteUUCode);

    @Query("SELECT vp FROM VisitPersonnel vp "
            + "INNER JOIN Personnel p "
            + "     ON p.personnelId = vp.personnel.personnelId "
            + "     AND p.status != org.baps.api.vtms.models.base.Status.DELETED "
            + "INNER JOIN Visit v "
            + "     ON v.visitId = vp.visit.visitId "
            + "     AND v.status != org.baps.api.vtms.models.base.Status.DELETED "
            + "LEFT JOIN VisitService vs "
            + "     ON vs.visitServiceId = vp.visitService.visitServiceId "
            + "     AND vs.status != org.baps.api.vtms.models.base.Status.DELETED "
            + "INNER JOIN Role r "
            + "     ON r.roleId = vp.role.roleId "
            + "WHERE p.personnelId IN :personnelIds AND v.site.uuCode=:siteUUCode "
            + "AND ("
            + "         (:visitServiceId IS NOT NULL AND vp.visitService.visitServiceId != :visitServiceId) "
            + "     OR "
            + "         (:visitId IS NOT NULL AND v.visitId != :visitId) "
            + ") "
            + "AND ("
            + "       (     vp.visitService.visitServiceId IS NOT NULL "
            + "         AND "
            + "             r.status != org.baps.api.vtms.models.base.Status.DELETED "
            + "         AND "
            + "             r.checkAvailability = true "
            + "         AND "
            + "             vs.startDateTime <= :endDateTime "
            + "         AND "
            + "             vs.endDateTime >= :startDateTime)"
            + "     OR (    vp.visitService.visitServiceId IS NULL "
            + "         AND "
            + "             r.status != org.baps.api.vtms.models.base.Status.DELETED "
            + "         AND "
            + "             r.checkAvailability = true "
            + "         AND "
            + "             v.startDateTime <= :endDateTime "
            + "         AND "
            + "             v.endDateTime >= :startDateTime)"
            + ")")
    List<VisitPersonnel> findAssociatedVisitPersonnelInVisitOrVisitServiceByPersonnelIdsExceptVisitServiceId(
            @Param("personnelIds") List<String> personnelIds, 
            @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime,
            @Param("visitServiceId") String visitServiceId, @Param("visitId") String visitId, @Param("siteUUCode") String siteUUCode);

    List<VisitPersonnel> findAllByVisitPersonnelIdInAndRoleTagEnumAndVisitServiceVisitServiceIdAndVisitServiceStatusNotAndVisitSiteUuCode(
            Set<String> visitPersonnelId, RoleTagEnum roleTagEnum, String visitServiceId, Status visitServiceStatus, String siteUUCode);

    List<VisitPersonnel> findAllByVisitPersonnelIdInAndVisitVisitIdAndRoleTagEnumAndVisitStatusNotAndVisitSiteUuCode(
        Set<String> visitPersonnelId, String visitId, RoleTagEnum roleTagEnum, Status visitStatus, String siteUUCode);

    List<VisitPersonnel> findAllByVisitVisitIdAndRoleTagEnumAndVisitSiteUuCode(String visitId, RoleTagEnum roleTagEnum, String siteUUCode);
    
    List<VisitPersonnel> findByVisitVisitIdAndVisitStatusNotAndPersonnelPersonnelIdAndPersonnelStatusNotAndVisitSiteUuCode(
        String visitId, Status visitStatus, String personnelId, Status personnelStatus, String siteUUCode);

    boolean existsVisitPersonnelByVisitVisitIdAndRoleTagEnumAndStatusNotAndVisitSiteUuCode(
        String visitId, RoleTagEnum roleTagEnum, Status status, String siteUUCode);

    Optional<VisitPersonnel> findByVisitPersonnelIdAndVisitSiteUuCode(String visitPersonnelId, String siteUUCode);
    
    List<VisitPersonnel> findByVisitStatusNotAndPersonnelPersonnelIdAndPersonnelStatusNotAndVisitSiteUuCode(
        Status visitStatus, String personnelId, Status personnelStatus, String siteUUCode);

}
