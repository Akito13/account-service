package com.example.bookshop.accountservice.event;

import com.example.bookshop.accountservice.CommonConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
    private KafkaTemplate<String, String> accountRegistrationTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, String> accountRegistrationTemplate) {
        this.accountRegistrationTemplate = accountRegistrationTemplate;
    }

    public void notifyAccountRegistration(String email) {
        accountRegistrationTemplate.send(CommonConstants.KAFKA_TOPIC_ACCOUNT_REGISTRATION, email);
        System.out.println("SENT " + email + " TO KAFKA BROKERS FOR ACTIVATION PROCESS");
    }
}
