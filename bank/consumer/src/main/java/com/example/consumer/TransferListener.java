package com.example.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class TransferListener {

    private static final Logger log = LoggerFactory.getLogger(TransferListener.class);

    private final TransferProcessor transferProcessor;

    public TransferListener(TransferProcessor transferProcessor) {
        this.transferProcessor = transferProcessor;
    }

    @KafkaListener(topics = "transfers", groupId = "${spring.kafka.consumer.group-id}", concurrency = "1")
    public void listen(ConsumerRecord<String, String> record, Acknowledgment ack) {
        String json = record.value();
        log.info("Received message from partition {} offset {}: {}",
                record.partition(), record.offset(), json);
        try {
            transferProcessor.processTransfer(json);
            ack.acknowledge();
            log.info("Message acknowledged");
        } catch (Exception e) {
            log.error("Error processing message, will not commit offset", e);
        }
    }
}