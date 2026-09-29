package com.expensejar;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;

    public BudgetService(
            BudgetRepository budgetRepository,
            ExpenseRepository expenseRepository) {
        this.budgetRepository = budgetRepository;
        this.expenseRepository = expenseRepository;
    }

    public Budget addBudget(Budget budget) {
        return budgetRepository.save(budget);
    }

    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    public Optional<Budget> getBudgetById(Long id) {
        return budgetRepository.findById(id);
    }

    public Budget updateBudget(Long id, Budget budget) {
        Budget existingBudget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        existingBudget.setAmount(budget.getAmount());
        existingBudget.setCategory(budget.getCategory());
        existingBudget.setUser(budget.getUser());
        existingBudget.setMonth(budget.getMonth());
        existingBudget.setYear(budget.getYear());

        return budgetRepository.save(existingBudget);
    }

    public void deleteBudget(Long id) {
        budgetRepository.deleteById(id);
    }

    public String checkBudgetAlert(Long budgetId) {

        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new RuntimeException("Budget not found"));

        YearMonth yearMonth = YearMonth.of(
                budget.getYear(),
                budget.getMonth()
        );

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        BigDecimal spending = expenseRepository.getTotalSpending(
                budget.getUser().getId(),
                budget.getCategory().getId(),
                startDate,
                endDate
        );

        BigDecimal percentage = spending
                .divide(budget.getAmount(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        if (percentage.compareTo(BigDecimal.valueOf(90)) >= 0) {
            return "ALERT: You have reached "
                    + percentage.setScale(2, RoundingMode.HALF_UP)
                    + "% of your budget.";
        }

        return "Budget usage is "
                + percentage.setScale(2, RoundingMode.HALF_UP)
                + "%. No alert.";
    }
}