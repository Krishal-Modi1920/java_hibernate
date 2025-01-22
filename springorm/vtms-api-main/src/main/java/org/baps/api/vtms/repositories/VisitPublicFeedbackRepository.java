package org.baps.api.vtms.repositories;

import org.baps.api.vtms.enumerations.VisitTypeEnum;
import org.baps.api.vtms.models.VisitPublicFeedbackSummaryModel;
import org.baps.api.vtms.models.entities.VisitPublicFeedback;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VisitPublicFeedbackRepository extends JpaRepository<VisitPublicFeedback, String>,
    JpaSpecificationExecutor<VisitPublicFeedback> {

    Optional<VisitPublicFeedback> findByVisitPublicFeedbackIdAndVisitSiteUuCode(String visitFeedbackId, String siteUUCode);
    
    @Query("SELECT NEW org.baps.api.vtms.models.VisitPublicFeedbackSummaryModel(COUNT(vpf), SUM(vpf.bookingProcessRating),"
            + " SUM(vpf.overallRating)) "
            + "         FROM "
            + "             VisitPublicFeedback vpf "
            + "     INNER JOIN "
            + "             Visit v "
            + "     ON "
            + "         v = vpf.visit "
            + "     WHERE "
            + "         v.visitTypeEnum = :visitTypeEnum " 
            + "     AND "
            + "         v.startDateTime >= :starDateTime " 
            + "     AND "
            + "         v.startDateTime <= :endDateTime " 
            + "     AND "
            + "         v.site.uuCode = :siteUUCode ")
    VisitPublicFeedbackSummaryModel findVisitPublicFeedbackCountByFilter(@Param("siteUUCode") String siteUUCode,
            @Param("starDateTime") LocalDateTime starDateTime, @Param("endDateTime") LocalDateTime endDateTime,
            @Param("visitTypeEnum") VisitTypeEnum visitTypeEnum);
}
