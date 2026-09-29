package com.expensejar;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;
    private final BudgetService budgetService;

    public ExpenseService(
            ExpenseRepository expenseRepository,
            BudgetRepository budgetRepository,
            BudgetService budgetService) {

        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
        this.budgetService = budgetService;
    }

    public Expense addExpense(Expense expense) {

        if (expense.getAmount() == null ||
                expense.getAmount().signum() <= 0) {

            throw new RuntimeException(
                    "Expense amount must be a positive value"
            );
        }

        Expense savedExpense = expenseRepository.save(expense);

        int month = expense.getDate().getMonthValue();
        int year = expense.getDate().getYear();

        budgetRepository
                .findByUserIdAndCategoryIdAndMonthAndYear(
                        expense.getUser().getId(),
                        expense.getCategory().getId(),
                        month,
                        year
                )
                .ifPresent(budget -> {

                    String alert = budgetService.checkBudgetAlert(
                            budget.getId()
                    );

                    System.out.println("Budget Check: " + alert);
                });

        return savedExpense;
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public Optional<Expense> getExpenseById(Long id) {
        return expenseRepository.findById(id);
    }

    public Expense updateExpense(Long id, Expense expense) {

        Expense existingExpense = expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        existingExpense.setAmount(expense.getAmount());
        existingExpense.setCategory(expense.getCategory());
        existingExpense.setUser(expense.getUser());
        existingExpense.setDate(expense.getDate());

        return expenseRepository.save(existingExpense);
    }

    public void deleteExpense(Long id) {
        expenseRepository.deleteById(id);
    }

    public BigDecimal getTotalSpending(
            Long userId,
            Long categoryId,
            LocalDate startDate,
            LocalDate endDate) {

        return expenseRepository.getTotalSpending(
                userId,
                categoryId,
                startDate,
                endDate
        );
    }

    public BigDecimal getMonthlySpending(
            Long userId,
            Long categoryId,
            int year,
            int month) {

        YearMonth yearMonth = YearMonth.of(year, month);

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return expenseRepository.getTotalSpending(
                userId,
                categoryId,
                startDate,
                endDate
        );
    }

    public Map<String, BigDecimal> getMonthlyTrend(
            Long userId,
            Long categoryId) {

        List<Object[]> results =
                expenseRepository.getMonthlyTrend(userId, categoryId);

        Map<String, BigDecimal> trend = new LinkedHashMap<>();

        for (Object[] result : results) {

            int year = ((Number) result[0]).intValue();
            int month = ((Number) result[1]).intValue();

            BigDecimal amount = (BigDecimal) result[2];

            YearMonth yearMonth = YearMonth.of(year, month);

            trend.put(
                    yearMonth.toString(),
                    amount
            );
        }

        return trend;
    }
}