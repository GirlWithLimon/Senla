package com.example.common.repository;

import com.example.common.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, String> {

    @Modifying
    @Query("UPDATE Transfer t SET t.status = :status WHERE t.id = :id")
    void updateStatus(@Param("id") String id, @Param("status") String status);
}