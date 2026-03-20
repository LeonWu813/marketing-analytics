package com.leon.marketing_analytics.exception;

public record ErrorResponse(
        int status,
        String error,
        String message
) {}
