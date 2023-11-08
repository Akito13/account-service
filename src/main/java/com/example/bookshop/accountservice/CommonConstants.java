package com.example.bookshop.accountservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CommonConstants {
    public static String JWT_KEY;
    public static String JWT_HEADER;
    public static Long JWT_EXP;
    public static String KAFKA_TOPIC_ACCOUNT_REGISTRATION;
    public static String KAFKA_TOPIC_ACCOUNT_CONFIRMED;
    public static Long KAFKA_BACKOFF_INTERVAL;
    public static Long KAFKA_BACKOFF_MAX_ATTEMPTS;

    @Value("${constant.security.JWT_KEY}")
    public void setJwtKey(String key) {
        CommonConstants.JWT_KEY = key;
    }
    @Value("${constant.security.JWT_HEADER}")
    public void setJwtHeader(String header) {
        CommonConstants.JWT_HEADER = header;
    }
    @Value("${constant.security.JWT_EXP}")
    public void setJwtHeader(Long expiration) {
        CommonConstants.JWT_EXP = expiration;
    }
    @Value("${constant.kafka.account-registration}")
    public void setKafkaTopicAccountRegistration(String topic) {
        CommonConstants.KAFKA_TOPIC_ACCOUNT_REGISTRATION = topic;
    }
    @Value("${constant.kafka.account-confirm-success}")
    public void setKafkaTopicAccountConfirmed(String topic) {
        CommonConstants.KAFKA_TOPIC_ACCOUNT_CONFIRMED = topic;
    }

    @Value("${constant.kafka.backoff.interval}")
    public void setKafkaBackoffInterval(Long interval) {
        CommonConstants.KAFKA_BACKOFF_INTERVAL = interval;
    }

    @Value("${constant.kafka.backoff.max-attempts}")
    public void setKafkaBackoffMaxAttempts(Long maxAttempts) {
        CommonConstants.KAFKA_BACKOFF_MAX_ATTEMPTS = maxAttempts;
    }
}
