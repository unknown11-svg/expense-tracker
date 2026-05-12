package com.expensetracker.expensetracker.repository;

import com.expensetracker.expensetracker.entity.Transaction;
import com.expensetracker.expensetracker.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.type = :type")
    BigDecimal sumByType(@Param("type") TransactionType type);

    @Query("SELECT t FROM Transaction t WHERE t.type = :type " + 
    "AND (CAST(:startDate AS LocalDate) IS NULL OR t.date >= :startDate) " + 
    "AND (CAST(:endDate AS LocalDate) IS NULL OR t.date <= :endDate) " + 
    "AND (CAST(:category AS String) IS NULL OR t.category = :category) " +
    "ORDER BY t.date DESC")
    List<Transaction> findTransactions(
        @Param("type") TransactionType type,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        @Param("category") String category
    );
}