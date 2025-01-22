package org.baps.api.vtms.services;

import org.baps.api.vtms.constants.GeneralConstant;
import org.baps.api.vtms.enumerations.VisitStageEnum;
import org.baps.api.vtms.models.entities.Site;
import org.baps.api.vtms.models.entities.Visit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.apache.commons.collections.CollectionUtils;

@Slf4j
@Component
@RequiredArgsConstructor
@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public class ScheduleTaskService {

    private final VisitService visitService;

    private final SiteService siteService;

    private final NotificationComposeService notificationComposeService;

    @Async
    public void updateVisitStages() {
        log.info("CRON START - Update visit stages");
        try {
            
            final List<Site> existingSiteList = siteService.findAllSite();
            if (CollectionUtils.isNotEmpty(existingSiteList)) {
                existingSiteList.stream().forEach(site -> {
                    final LocalDateTime currentDateTimeOfSite = siteService.getCurrentDateTimeFromExistingSite(site);

                    final LocalDateTime atMidNight = currentDateTimeOfSite.withHour(GeneralConstant.ZERO_ZERO)
                            .withMinute(GeneralConstant.ZERO_ZERO)
                            .withSecond(GeneralConstant.ZERO_ZERO);

                    final LocalDateTime atMidNightAfter30Min = currentDateTimeOfSite.withHour(GeneralConstant.ZERO_ZERO)
                            .withMinute(GeneralConstant.THIRTY)
                            .withSecond(GeneralConstant.ZERO_ZERO);
                    
                    if (atMidNight.isBefore(currentDateTimeOfSite) && atMidNightAfter30Min.isAfter(currentDateTimeOfSite)
                            || atMidNight.equals(currentDateTimeOfSite)) {
                        
                        visitService.updateVisitStages(currentDateTimeOfSite);
                    }
                });
            }
        } catch (final Exception e) {
            log.error("CRON ERROR - Update visit stages : {}", e.getMessage());
        }
        log.info("CRON END - Update visit stages");
    }

    @Transactional
    public void sendVisitListNotification() {
        log.info("CRON START - Send visit list notification");
        final List<Visit> nextDayVisitList = visitService.getNextDayVisitList();
        final List<Visit> acceptedPendingVisitList = nextDayVisitList.stream().filter(v ->
            v.getVisitStageEnum().equals(VisitStageEnum.ACCEPTED) || v.getVisitStageEnum().equals(VisitStageEnum.PENDING)).toList();
        log.info("Next day accepted or pending visitIds: {}", acceptedPendingVisitList.stream().map(Visit::getVisitId).toList());
        if (CollectionUtils.isNotEmpty(acceptedPendingVisitList)) {
            final String visitDate = acceptedPendingVisitList.get(0).getStartDateTime().toLocalDate().toString();
            notificationComposeService.sendNextDayVisitListNotification(acceptedPendingVisitList, visitDate);
        }
        log.info("CRON END - Send visit list notification");
    }
}
