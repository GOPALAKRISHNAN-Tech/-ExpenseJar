package com.expensejar;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    Optional<Budget> findByUserIdAndCategoryIdAndMonthAndYear(
            Long userId,
            Long categoryId,
            int month,
            int year
    );
}