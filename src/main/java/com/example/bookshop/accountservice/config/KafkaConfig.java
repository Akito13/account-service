package com.example.bookshop.accountservice.config;

import com.example.bookshop.accountservice.CommonConstants;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic accountRegistrationTopic() {
        return TopicBuilder.name(CommonConstants.KAFKA_TOPIC_ACCOUNT_REGISTRATION).build();
    }
}
