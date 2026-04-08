package com.example.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransferListener {

    private static final Logger log = LoggerFactory.getLogger(TransferListener.class);

    private final TransferProcessor transferProcessor;

    public TransferListener(TransferProcessor transferProcessor) {
        this.transferProcessor = transferProcessor;
    }

    @KafkaListener(topics = "transfers", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(List<ConsumerRecord<String, String>> records, Acknowledgment ack) {
        for (ConsumerRecord<String, String> record : records) {
            try {
                transferProcessor.processTransfer(record.value());
            } catch (Exception e) {
                log.error("Error processing message", e);
            }
        }
        ack.acknowledge();
    }
}