package com.leon.marketing_analytics.exception;

public class MethodArgumentNotValidException extends RuntimeException {

    public MethodArgumentNotValidException(String message) {
        super(message);
    }
}