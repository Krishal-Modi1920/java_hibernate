package org.baps.api.vtms.repositories;

import org.baps.api.vtms.enumerations.TourSlotStageEnum;
import org.baps.api.vtms.models.TourSlotWithVisitorCountModel;
import org.baps.api.vtms.models.entities.TourSlot;

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
public interface TourSlotRepository extends JpaRepository<TourSlot, String>, JpaSpecificationExecutor<TourSlot> {

    boolean existsByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqualAndSiteUuCode(
        LocalDateTime startDateTime, LocalDateTime endDateTime, String siteUUCode);

    List<TourSlot> findByStartDateTimeGreaterThanEqualAndEndDateTimeLessThanEqualAndSiteUuCode(
        LocalDateTime startDateTime, LocalDateTime endDateTime, String siteUUCode);

    Optional<TourSlot> findByTourSlotIdAndTourSlotStageEnumNotInAndSiteUuCode(String tourSlotId,
                                                                              List<TourSlotStageEnum> tourSlotStageEnumList,
                                                                              String siteUUCode);

    @Query("SELECT new org.baps.api.vtms.models.TourSlotWithVisitorCountModel(v.tourSlot.tourSlotId, sum(v.totalVisitors)) "
        + "     FROM "
        + "         Visit v "
        + "     WHERE "
        + "         v.visitStageEnum != org.baps.api.vtms.enumerations.VisitStageEnum.CANCELLED "
        + "         AND v.tourSlot.site.uuCode = :siteUUCode"
        + "         AND v.tourSlot.tourSlotId in (:tourSlotIds) "
        + "     GROUP BY v.tourSlot.tourSlotId")
    List<TourSlotWithVisitorCountModel> countBookedVisitorCountByTourSlotIds(@Param("tourSlotIds") Set<String> tourSlotIds,
                                                                             @Param("siteUUCode") String siteUUCode);

    Optional<TourSlot> findByTourSlotIdAndSiteUuCode(String tourSlotId, String siteUUCode);
}
