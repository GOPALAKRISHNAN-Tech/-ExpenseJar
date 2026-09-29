package com.expensejar;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public Expense addExpense(@Valid @RequestBody Expense expense) {
        return expenseService.addExpense(expense);
    }

    @GetMapping
    public List<Expense> getAllExpenses() {
        return expenseService.getAllExpenses();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Long id) {
        return expenseService.getExpenseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Expense updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody Expense expense) {

        return expenseService.updateExpense(id, expense);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/spending")
    public BigDecimal getTotalSpending(
            @RequestParam Long userId,
            @RequestParam Long categoryId,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        return expenseService.getTotalSpending(
                userId,
                categoryId,
                LocalDate.parse(startDate),
                LocalDate.parse(endDate)
        );
    }

    @GetMapping("/trend")
    public Map<String, BigDecimal> getMonthlyTrend(
            @RequestParam Long userId,
            @RequestParam Long categoryId) {

        return expenseService.getMonthlyTrend(
                userId,
                categoryId
        );
    }
}