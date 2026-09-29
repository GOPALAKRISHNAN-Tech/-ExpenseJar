package com.expensejar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("""
        SELECT COALESCE(SUM(e.amount), 0)
        FROM Expense e
        WHERE e.user.id = :userId
        AND e.category.id = :categoryId
        AND e.date BETWEEN :startDate AND :endDate
    """)
    BigDecimal getTotalSpending(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
        SELECT YEAR(e.date), MONTH(e.date), COALESCE(SUM(e.amount), 0)
        FROM Expense e
        WHERE e.user.id = :userId
        AND e.category.id = :categoryId
        GROUP BY YEAR(e.date), MONTH(e.date)
        ORDER BY YEAR(e.date), MONTH(e.date)
    """)
    List<Object[]> getMonthlyTrend(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId
    );
}