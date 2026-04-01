package com.leon.marketing_analytics.dto;

import java.time.LocalDateTime;

public record ReportSendResponse(
        Long id,
        String sentTo,
        LocalDateTime sentAt,
        Long reportId
) {
}
