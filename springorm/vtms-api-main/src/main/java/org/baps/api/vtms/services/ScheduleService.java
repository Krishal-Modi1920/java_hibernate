package org.baps.api.vtms.services;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@Component
@RequiredArgsConstructor
@SuppressFBWarnings({"EI_EXPOSE_REP2"})
public class ScheduleService {

    private final ScheduleTaskService scheduleTaskService;

    @Scheduled(cron = "0 0,30 * * * *")
    public void everyHalfAnHour() {
        scheduleTaskService.updateVisitStages();
    }

    @Scheduled(cron = "#{@getDailyVisitListCron}")
    public void dailyVisitListScheduler() {
        scheduleTaskService.sendVisitListNotification();
    }
}
