package org.baps.api.vtms.common.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CronConfig {

    @Value("${daily-visit-list-cron}")
    private String dailyVisitListCron;

    @Bean
    public String getDailyVisitListCron() {
        return dailyVisitListCron;
    }
}
