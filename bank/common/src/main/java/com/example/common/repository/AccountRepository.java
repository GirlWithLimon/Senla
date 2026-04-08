package com.example.common.repository;

import com.example.common.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> findByIdWithLock(@Param("id") Long id);
    @Modifying
    @Query("UPDATE Account a SET a.balance = a.balance + :delta WHERE a.id = :id")
    void updateBalance(@Param("id") Long id, @Param("delta") BigDecimal delta);
}