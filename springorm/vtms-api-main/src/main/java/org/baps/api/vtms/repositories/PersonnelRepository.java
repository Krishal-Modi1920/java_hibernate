package org.baps.api.vtms.repositories;

import org.baps.api.vtms.models.entities.Personnel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PersonnelRepository extends JpaRepository<Personnel, String>, JpaSpecificationExecutor<Personnel> {

    Optional<Personnel> findByUucode(String pid);

    @Query("SELECT p FROM Personnel p "
            + "     INNER JOIN VisitPersonnel vp "
            + "         ON p.personnelId = vp.personnel.personnelId "
            + "         AND vp.status != org.baps.api.vtms.models.base.Status.DELETED "
            + "     INNER JOIN Visit v "
            + "         ON v.visitId = vp.visit.visitId "
            + "         AND v.status != org.baps.api.vtms.models.base.Status.DELETED "
            + "     LEFT JOIN VisitService vs "
            + "         ON vs.visitServiceId = vp.visitService.visitServiceId "
            + "         AND vs.status != org.baps.api.vtms.models.base.Status.DELETED "
            + "     INNER JOIN Role r "
            + "         ON r.roleId = vp.role.roleId "
            + "     WHERE v.site.uuCode = :siteUUCode "
            + "     AND "
            + "     (     "
            + "         vp.visitService.visitServiceId IS NOT NULL "
            + "     AND "
            + "         r.status != org.baps.api.vtms.models.base.Status.DELETED "
            + "     AND "
            + "         r.checkAvailability = true "
            + "     AND "
            + "         vs.startDateTime <= :endDateTime "
            + "     AND "
            + "         vs.endDateTime >= :startDateTime"
            + "     )"
            + " OR (    "
            + "         vp.visitService.visitServiceId IS NULL "
            + "     AND "
            + "         r.status != org.baps.api.vtms.models.base.Status.DELETED "
            + "     AND "
            + "         r.checkAvailability = true "
            + "     AND "
            + "         v.startDateTime <= :endDateTime "
            + "     AND "
            + "         v.endDateTime >= :startDateTime"
            + ")")
    List<Personnel> findAllAssociatePersonnelInVisitOrVisitService(@Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime, @Param("siteUUCode") String siteUUCode);

    List<Personnel> findByPersonnelIdNotIn(List<String> personnelIdList);

    List<Personnel> findAllByPersonnelRoleListRoleUucodeInAndPersonnelRoleListSiteUuCode(Set<String> roleUucodeSet, String siteUUCode);

    List<Personnel> findAllByPersonnelIdIn(Set<String> personnelIds);
}
