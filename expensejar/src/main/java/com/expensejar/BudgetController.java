package com.expensejar;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    @PostMapping
    public Budget addBudget(@RequestBody Budget budget) {
        return budgetService.addBudget(budget);
    }

    @GetMapping
    public List<Budget> getAllBudgets() {
        return budgetService.getAllBudgets();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable Long id) {
        return budgetService.getBudgetById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Budget updateBudget(
            @PathVariable Long id,
            @RequestBody Budget budget) {
        return budgetService.updateBudget(id, budget);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/alert")
    public String checkBudgetAlert(@PathVariable Long id) {
        return budgetService.checkBudgetAlert(id);
    }
}