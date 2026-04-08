package com.example.producer;

import com.example.common.entity.Account;
import com.example.common.repository.AccountRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AccountInitializer implements ApplicationRunner {

    private final AccountRepository accountRepository;
    private final Map<Long, Account> accountMap = new ConcurrentHashMap<>();

    public AccountInitializer(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (accountRepository.count() == 0) {
            List<Account> accounts = new ArrayList<>();
            for (int i = 1; i <= 1000; i++) {
                Account acc = new Account();
                acc.setBalance(BigDecimal.valueOf(10000));
                accounts.add(acc);
            }
            accountRepository.saveAll(accounts);
            System.out.println("Создано 1000 аккаунтов");
        }
        List<Account> allAccounts = accountRepository.findAll();
        for (Account acc : allAccounts) {
            accountMap.put(acc.getId(), acc);
        }
        System.out.println("Загружено " + accountMap.size() + " аккаунтов в память");
    }

    public Map<Long, Account> getAccountMap() {
        return accountMap;
    }
}