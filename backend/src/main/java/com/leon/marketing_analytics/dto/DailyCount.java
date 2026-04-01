package com.leon.marketing_analytics.dto;

import java.time.LocalDate;

public record DailyCount (
        LocalDate date,
        Long count
){
}
