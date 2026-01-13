package com.poc.data_assessment.service.consumer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import org.springframework.kafka.annotation.KafkaListener;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(
    name = "spring.kafka.bootstrap-servers",
    matchIfMissing = true
)
public class DeConsumer{

    @KafkaListener(topics = "de-topic", containerFactory = "updateDeEventListenerFactory")
    public void consume(UpdateDeEvent updateDeEvent) {
        System.out.println("Consumed message: " + updateDeEvent.permanentId() + " " + updateDeEvent.timeBucket());
    }
    
}
