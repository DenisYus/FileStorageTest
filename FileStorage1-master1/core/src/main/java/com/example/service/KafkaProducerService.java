package com.example.service;


import com.example.exception.ObjectMapperException;
import com.example.model.MailMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String,String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, String message){
        kafkaTemplate.send(topic, message);
    }
    public void sendMailMessage(String email, String subject, String text){
        ObjectMapper objectMapper = new ObjectMapper();
        MailMessage mailMessage = new MailMessage();
        mailMessage.setEmail(email);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);
        try {
            String message = objectMapper.writeValueAsString(mailMessage);
            sendMessage("mailTopic", message);
        } catch (JsonProcessingException e) {
            throw new ObjectMapperException(e.getMessage());
        }
    }
}
