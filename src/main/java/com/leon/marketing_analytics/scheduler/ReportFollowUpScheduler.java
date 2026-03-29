package com.leon.marketing_analytics.scheduler;

import com.leon.marketing_analytics.service.ReportSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReportFollowUpScheduler {
    private final ReportSendService reportSendService;

    @Scheduled(cron = "0 0 9 * * *")
    public void scheduleFollowUpEmail(){
        reportSendService.processFollowUps();
    }
}
