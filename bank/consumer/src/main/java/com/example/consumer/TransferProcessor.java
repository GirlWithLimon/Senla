package com.example.consumer;

import com.example.common.entity.Account;
import com.example.common.entity.Transfer;
import com.example.common.repository.AccountRepository;
import com.example.common.repository.TransferRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class TransferProcessor {

    private static final Logger log = LoggerFactory.getLogger(TransferProcessor.class);

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;
    private final ObjectMapper objectMapper;

    public TransferProcessor(AccountRepository accountRepository,
                             TransferRepository transferRepository,
                             ObjectMapper objectMapper) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processTransfer(String json) {
        Transfer transfer;
        try {
            transfer = objectMapper.readValue(json, Transfer.class);
        } catch (Exception e) {
            log.error("Failed to parse JSON: {}", json, e);
            return;
        }

        String transferId = transfer.getId();
        log.info("Processing transfer {}", transferId);

        if (transferRepository.existsById(transferId)) {
            log.info("Transfer {} already processed, skipping", transferId);
            return;
        }

        Optional<Account> fromOpt = accountRepository.findById(transfer.getFromAccountId());
        Optional<Account> toOpt = accountRepository.findById(transfer.getToAccountId());

        if (fromOpt.isEmpty() || toOpt.isEmpty()) {
            log.error("Account not found for transfer {}: from={}, to={}",
                    transferId, transfer.getFromAccountId(), transfer.getToAccountId());
            return;
        }

        Account from = fromOpt.get();
        if (from.getBalance().compareTo(transfer.getAmount()) < 0) {
            log.error("Insufficient funds for transfer {}: account {} balance {} < amount {}",
                    transferId, from.getId(), from.getBalance(), transfer.getAmount());
            return;
        }

        Transfer errorRecord = new Transfer(
                transferId,
                transfer.getFromAccountId(),
                transfer.getToAccountId(),
                transfer.getAmount(),
                "error"
        );
        transferRepository.save(errorRecord);

        try {
            performTransferAndUpdateStatus(transfer);
            log.info("Transfer {} processed successfully", transferId);
        } catch (Exception e) {
            log.error("Transaction failed for transfer {} (record saved with error status)", transferId, e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void performTransferAndUpdateStatus(Transfer transfer) {
        accountRepository.updateBalance(transfer.getFromAccountId(), transfer.getAmount().negate());
        accountRepository.updateBalance(transfer.getToAccountId(), transfer.getAmount());
        transferRepository.updateStatus(transfer.getId(), "ready");
    }
}