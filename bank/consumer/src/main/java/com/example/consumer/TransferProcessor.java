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
            log.error("Ошибка парсинга JSON: {}", json, e);
            return;
        }

        String transferId = transfer.getId();
        log.info("Процесс передачи {}", transferId);

        if (transferRepository.existsById(transferId)) {
            log.info("Передача {} уже в процессе, пропускаем", transferId);
            return;
        }
        Transfer record = new Transfer(
                transferId,
                transfer.getFromAccountId(),
                transfer.getToAccountId(),
                transfer.getAmount(),
                "В процессе"
        );
        transferRepository.save(record);
        Optional<Account> fromOpt = accountRepository.findByIdWithLock(transfer.getFromAccountId());
        Optional<Account> toOpt = accountRepository.findByIdWithLock(transfer.getToAccountId());

        if (fromOpt.isEmpty() || toOpt.isEmpty()) {
            log.error("Аккаунт не найден для передачи {}: из={}, в={}",
                    transferId, transfer.getFromAccountId(), transfer.getToAccountId());
            transferRepository.updateStatus(transferId, "Ошибка, аккаунт не найден!");
            return;
        }

        Account from = fromOpt.get();
        if (from.getBalance().compareTo(transfer.getAmount()) < 0) {
            log.error("Недостаточно средств для перевода {}: аккаунт {} баланс {} < суммы {}",
                    transferId, from.getId(), from.getBalance(), transfer.getAmount());
            transferRepository.updateStatus(transferId, "Ошибка, недостаточно средств!");
            return;
        }

        try {
            accountRepository.updateBalance(transfer.getFromAccountId(), transfer.getAmount().negate());
            accountRepository.updateBalance(transfer.getToAccountId(), transfer.getAmount());
            transferRepository.updateStatus(transfer.getId(), "выполнен");
            log.info("Процесс перевода {} прошел успешно", transferId);
        } catch (Exception e) {
            log.error("Перевод {} не удался", transferId, e);
            transferRepository.updateStatus(transferId, "Ошибка: " + e.getMessage());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void performTransferAndUpdateStatus(Transfer transfer) {

    }
}