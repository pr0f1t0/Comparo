package com.pr0f1t.comparo.adminservice.exception;

public class EventPublishingException extends RuntimeException {
    public EventPublishingException(String message, Throwable cause) {
        super(message,  cause);
    }
}
