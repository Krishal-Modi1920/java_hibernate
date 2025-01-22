package org.baps.api.vtms.repositories;

import org.baps.api.vtms.enumerations.VisitStageEnum;
import org.baps.api.vtms.enumerations.VisitTypeEnum;
import org.baps.api.vtms.models.VisitCountModel;
import org.baps.api.vtms.models.base.Status;
import org.baps.api.vtms.models.entities.Visit;

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
public interface VisitRepository extends JpaRepository<Visit, String>, JpaSpecificationExecutor<Visit> {

    List<Visit> findAllByVisitStageEnumInAndEndDateTimeLessThan(Set<VisitStageEnum> currentVisitStageEnums, LocalDateTime localDateTime);

    List<Visit> findAllByVisitPersonnelListRoleUucodeAndSiteUuCode(String uucode, String siteUUCode);

    @Query("SELECT v FROM Visit v "
                    + "     INNER JOIN VisitPersonnel vp "
                    + "         ON  v.visitId = vp.visit.visitId "
                    + "     INNER JOIN Role r "
                    + "         ON  r.roleId = vp.role.roleId "
                    + "     INNER JOIN Personnel p "
                    + "         ON  p.personnelId = vp.personnel.personnelId"
                    + "     WHERE "
                    + "         vp.status != org.baps.api.vtms.models.base.Status.DELETED "
                    + "     AND "
                    + "         r.status != org.baps.api.vtms.models.base.Status.DELETED "
                    + "     AND "
                    + "         p.status != org.baps.api.vtms.models.base.Status.DELETED "
                    + "     AND "
                    + "         r.uucode in (:roleUucodeSet)"
                    + "     AND "
                    + "         LOWER(CONCAT(p.firstName, ' ', p.lastName)) LIKE :personnelName"
                    + "     AND "
                    + "         v.site.uuCode = :siteUUCode")
    List<Visit> findAllByVisitPersonnelLikePersonnelNameAndRoleUucodeSet(@Param("roleUucodeSet") Set<String> roleUucodeSet,
            @Param("personnelName") String personnelName, @Param("siteUUCode") String siteUUCode);

    @Query("SELECT v FROM Visit v "
            + "     INNER JOIN VisitPersonnel vp "
            + "         ON  v.visitId = vp.visit.visitId "
            + "     INNER JOIN Personnel p "
            + "         ON  p.personnelId = vp.personnel.personnelId"
            + "     WHERE "
            + "         vp.status != org.baps.api.vtms.models.base.Status.DELETED "
            + "     AND "
            + "         p.status != org.baps.api.vtms.models.base.Status.DELETED "
            + "     AND "
            + "         p.personnelId = :personnelId"
            + "     AND "
            + "         v.site.uuCode = :siteUUCode")
    List<Visit> findAllByVisitPersonnelIdAndSiteUuCode(@Param("personnelId") String personnelId,
                                                       @Param("siteUUCode") String siteUUCode);


    List<Visit> findAllByTourSlotTourSlotIdAndSiteUuCode(String tourSlotId, String siteUUCode);

    Optional<Visit> findByVisitIdAndSiteUuCode(String visitId, String siteUUCode);
    
    Visit findTopBySiteUuCodeAndVisitTypeEnumOrderByCreatedAtDesc(String siteUUCode, VisitTypeEnum visitTypeEnum);
    
    @Query("SELECT NEW org.baps.api.vtms.models.VisitCountModel(COUNT(v), SUM(v.totalVisitors), CAST(v.startDateTime as DATE)) " 
            + "         FROM Visit v " 
            + "     WHERE "
            + "         v.visitTypeEnum = :visitTypeEnum " 
            + "     AND "
            + "         v.startDateTime >= :starDateTime " 
            + "     AND "
            + "         v.startDateTime <= :endDateTime " 
            + "     AND "
            + "         v.site.uuCode = :siteUUCode " 
            + "     AND "
            + "         v.visitStageEnum in (:visitStageEnumList) " 
            + "     GROUP BY "
            + "         DATE(v.startDateTime)")
    List<VisitCountModel> findVisitCountGroupByStartDateAndTime(@Param("siteUUCode") String siteUUCode,
            @Param("starDateTime") LocalDateTime starDateTime, @Param("endDateTime") LocalDateTime endDateTime,
            @Param("visitTypeEnum") VisitTypeEnum visitTypeEnum, @Param("visitStageEnumList") List<VisitStageEnum> visitStageEnumList);
    
    
    @Query("SELECT NEW org.baps.api.vtms.models.VisitCountModel(COUNT(v), SUM(v.totalVisitors), CAST(v.startDateTime as DATE)) " 
            + "         FROM "
            + "             Visit v "
            + "     INNER JOIN "
            + "             VisitPersonnel vp "
            + "     ON "
            + "         v = vp.visit "
            + "     WHERE "
            + "         v.visitTypeEnum = :visitTypeEnum " 
            + "     AND "
            + "         v.startDateTime >= :starDateTime " 
            + "     AND "
            + "         v.startDateTime <= :endDateTime " 
            + "     AND "
            + "         v.site.uuCode = :siteUUCode " 
            + "     AND "
            + "         v.visitStageEnum in (:visitStageEnumList) "
            + "     AND "
            + "         vp.personnel.personnelId = :personnelId "
            + "     AND "
            + "         vp.status != :visitPersonnelStatus" 
            + "     GROUP BY "
            + "         DATE(v.startDateTime)")
    List<VisitCountModel> findVisitCountByPersonnelIdAndFilter(@Param("siteUUCode") String siteUUCode,
            @Param("starDateTime") LocalDateTime starDateTime, @Param("endDateTime") LocalDateTime endDateTime,
            @Param("visitTypeEnum") VisitTypeEnum visitTypeEnum, @Param("personnelId") String personnelId, 
            @Param("visitStageEnumList") List<VisitStageEnum> visitStageEnumList, 
            @Param("visitPersonnelStatus") Status visitPersonnelStatus);

    @Query("SELECT v FROM Visit v WHERE v.startDateTime BETWEEN :startOfNextDay AND :endOfNextDay")
    List<Visit> findVisitsForNextDay(@Param("startOfNextDay") LocalDateTime startOfNextDay,
                                     @Param("endOfNextDay") LocalDateTime endOfNextDay);

    Optional<Visit> findByVisitIdAndSiteUuCodeAndIsPrivate(String visitId, String siteUUCode, boolean isPrivate);
    
    @Query("SELECT v "
            + "FROM Visit v "
            + "JOIN v.visitVisitorList vv "
            + "JOIN vv.visitor visitor "
            + "WHERE v.requestNumber = :requestNumber "
            + "  AND ( "
            + "       CONCAT(visitor.phoneCountryCode, visitor.phoneNumber) = :phoneNumber "
            + "       OR visitor.email = :email "
            + "       OR visitor.lastName = :lastName "
            + "      ) "
            + "  AND visitor.status = :visitorStatus "
            + "  AND v.status = :visitStatus "
            + "  AND v.site.uuCode = :siteUUCode "
            + "  AND v.site.status = :siteStatus")
    Optional<Visit> findVisitByRequestNumberAndFilter(@Param("siteUUCode") String siteUUCode, 
            @Param("requestNumber") String requestNumber, @Param("email") String email,
            @Param("phoneNumber") String phoneNumber, @Param("lastName") String lastName, 
            @Param("siteStatus") Status siteStatus, @Param("visitStatus") Status visitStatus,
            @Param("visitorStatus") Status visitorStatus);
}
