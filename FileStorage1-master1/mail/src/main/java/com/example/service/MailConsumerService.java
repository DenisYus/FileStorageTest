package com.example.service;

import com.example.exception.ObjectMapperException;
import com.example.model.MailMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class MailConsumerService {

    private final MailService mailService;

    public MailConsumerService(MailService mailService) {
        this.mailService = mailService;
    }

    @KafkaListener(topics = "mailTopic", groupId = "mail-group")
    public void listen(String message){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            MailMessage mailMessage = objectMapper.readValue(message, MailMessage.class);
            mailService.sendEmail(mailMessage.getEmail(), mailMessage.getSubject(), mailMessage.getText());
        } catch (JsonProcessingException e) {
            throw new ObjectMapperException(e.getMessage());
        }

    }
}
