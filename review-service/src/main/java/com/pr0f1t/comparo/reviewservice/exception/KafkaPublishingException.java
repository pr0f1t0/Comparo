package com.pr0f1t.comparo.reviewservice.exception;

public class KafkaPublishingException extends RuntimeException {
    public KafkaPublishingException(String message, Throwable cause) {
        super(message, cause);
    }

    public KafkaPublishingException(String message) {
        super(message);
    }
}