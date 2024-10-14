package com.beto.str_producer.services;

import java.util.concurrent.CompletableFuture;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class StringProducerService {

    private final KafkaTemplate<String,String> kafkaTemplate;

    public void sendMessage(String message){
        final ProducerRecord<String, String> record = createRecord(message);
     CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send("str-topic", message);
     future.whenComplete((result, ex) -> {
        if (ex == null) {
            handleSuccess(message, result);
        }
        else {
            handleFailure(message, record, ex);
        }
    });
  
    }

    private void handleSuccess(String message,SendResult<String, String> result) {
        int partition = result.getRecordMetadata().partition(); 
        long offset = result.getRecordMetadata().offset(); 

        log.info("Mensagem enviada com sucesso: {} para a partição: {} no offset: {}", message, partition, offset);
    }

    private void handleFailure(String message, ProducerRecord<String, String> record, Throwable ex) {
        log.error("Falha ao enviar mensagem: {} para o tópico: {}", message, record.topic(), ex);
    }

    private ProducerRecord<String, String> createRecord(String message) {
        return new ProducerRecord<>("str-topic", message);
    }
    
}
