package com.example.bookshop.accountservice.config;

import com.example.bookshop.accountservice.CommonConstants;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DelegatingByTopicDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Configuration
@EnableKafka
public class KafkaConfig {
    @Bean
    public NewTopic accountRegistrationTopic() {
        return TopicBuilder.name(CommonConstants.KAFKA_TOPIC_ACCOUNT_REGISTRATION).build();
    }
    @Bean
    public NewTopic accountPasswordChangeTopic() {
        return TopicBuilder.name(CommonConstants.KAFKA_TOPIC_ACCOUNT_PASSWORD_CHANGE).build();
    }
    @Bean
    public DefaultErrorHandler kafkaErrorHandler() {
        BackOff fixedBackOff = new FixedBackOff(CommonConstants.KAFKA_BACKOFF_INTERVAL, CommonConstants.KAFKA_BACKOFF_MAX_ATTEMPTS);
        DefaultErrorHandler errorHandler = new DefaultErrorHandler((consumerRecord, exception) -> {
            System.out.println("--------------------------------------");
            System.out.println(consumerRecord.key());
            System.out.println(consumerRecord.value());
            System.out.println("--------------------------------------");
            exception.printStackTrace();
        },fixedBackOff);
        errorHandler.addRetryableExceptions(SocketTimeoutException.class);
        errorHandler.addNotRetryableExceptions(NullPointerException.class);
        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);
        System.out.println("DONE INITIALIZING KafkaErrorHandler");
        return errorHandler;
    }

    @Bean
    public ConsumerFactory<String, Object> generateFactory(Map<String, Object> configs) {
        return new DefaultKafkaConsumerFactory<>(
                configs,
                new StringDeserializer(),
                new DelegatingByTopicDeserializer(Map.of(
                        Pattern.compile(CommonConstants.KAFKA_TOPIC_ACCOUNT_CONFIRMED), new StringDeserializer()),
                        new StringDeserializer())
        );
    }

//    private <K, V> ConsumerFactory<K, V> generateFactory(Deserializer<K> keyDeserializer, Deserializer<V> valueDeserializer) {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
//        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
//        return new DefaultKafkaConsumerFactory<>(props, keyDeserializer, valueDeserializer);
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactoryString(Map<String, Object> configs) {
//        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
////        ConsumerFactory<String, Object> consumerFactory = new DefaultKafkaConsumerFactory<>(
////                configs,
////                new StringDeserializer(),
////                new DelegatingByTopicDeserializer(Map.of(
////                        Pattern.compile(CommonConstants.KAFKA_TOPIC_ACCOUNT_CONFIRMED), new StringDeserializer()),
////                        new StringDeserializer())
////        );
//        factory.setConsumerFactory(generateFactory(configs));
//        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
//        factory.setCommonErrorHandler(kafkaErrorHandler());
//        System.out.println("DONE INITIALIZING KafkaContainerFactoryString");
//        return factory;
//    }
}
