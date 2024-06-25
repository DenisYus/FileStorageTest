package com.example;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@MockBean({KafkaAdmin.class, NewTopic.class, ProducerFactory.class, KafkaTemplate.class})
public @interface MockKafkaBeans {

}
