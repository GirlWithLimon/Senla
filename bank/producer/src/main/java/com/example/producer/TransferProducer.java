package com.example.producer;

import com.example.common.entity.Transfer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
public class TransferProducer {

    private static final Logger log = LoggerFactory.getLogger(TransferProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final AccountInitializer accountInitializer;
    private final ObjectMapper objectMapper;
    private final Random random = new Random();

    public TransferProducer(KafkaTemplate<String, String> kafkaTemplate,
                            AccountInitializer accountInitializer,
                            ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.accountInitializer = accountInitializer;
        this.objectMapper = objectMapper;
    }

    @Scheduled(fixedDelay = 200)
    @Transactional
    public void generateAndSend() {
        try {
            List<Long> accountIds = new ArrayList<>(accountInitializer.getAccountMap().keySet());
            if (accountIds.size() < 2) {
                log.warn("Not enough accounts to generate transfer");
                return;
            }
            long fromId = accountIds.get(random.nextInt(accountIds.size()));
            long toId;
            do {
                toId = accountIds.get(random.nextInt(accountIds.size()));
            } while (toId == fromId);

            BigDecimal amount = BigDecimal.valueOf(random.nextInt(1000) + 1);
            String transferId = UUID.randomUUID().toString();

            Transfer transfer = new Transfer();
            transfer.setId(transferId);
            transfer.setFromAccountId(fromId);
            transfer.setToAccountId(toId);
            transfer.setAmount(amount);

            String json = objectMapper.writeValueAsString(transfer);
            String key = String.valueOf(transferId.hashCode());
            kafkaTemplate.send("transfers", key, json);
            log.info("Sent transfer {}: from {} to {} amount {}", transferId, fromId, toId, amount);
        } catch (Exception e) {
            log.error("Failed to send transfer", e);
            throw new RuntimeException(e);
        }
    }
}