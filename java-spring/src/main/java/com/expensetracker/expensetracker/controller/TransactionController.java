package com.expensetracker.expensetracker.controller;

import com.expensetracker.expensetracker.dto.Income_ExpensesResponse;
import com.expensetracker.expensetracker.dto.SummaryResponse;
import com.expensetracker.expensetracker.dto.TransactionRequest;
import com.expensetracker.expensetracker.dto.TransactionResponse;
import com.expensetracker.expensetracker.dto.TransactionUpdateRequest;
import com.expensetracker.expensetracker.service.TransactionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDate;


@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;

    //CREATE
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody TransactionRequest request){
        TransactionResponse response = transactionService.createTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions(){
        List<TransactionResponse> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    } 

    //READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById( @PathVariable Integer id){
        TransactionResponse transaction = transactionService.getTransactionById(id);

        return ResponseEntity.ok(transaction);
    }

    //UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(@PathVariable Integer id, @Valid @RequestBody TransactionUpdateRequest payload){
        TransactionResponse response = transactionService.updateTransaction(id, payload);
        
        return ResponseEntity.ok(response);
    }

    //DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTranasction(@PathVariable Integer id){
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    //Summary(Income, Expenses, Balance)
    @GetMapping("/summary")
    public ResponseEntity<SummaryResponse> getSummary(){
        SummaryResponse summary = transactionService.getSummary();
        return ResponseEntity.ok(summary);
    }

    //Filtering
    @GetMapping("/summary/expenses")
    public ResponseEntity<List<Income_ExpensesResponse>> listExpense(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam(required = false) @Size(max = 50) String category){
            List<Income_ExpensesResponse> expenses = transactionService.listExpenses(startDate, endDate, category);
            return ResponseEntity.ok(expenses);
    }

    @GetMapping("/summary/income")
    public ResponseEntity<List<Income_ExpensesResponse>> listIncome(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam(required = false) @Size(max = 50) String category){
            List<Income_ExpensesResponse> income = transactionService.listIncome(startDate, endDate, category);
            return ResponseEntity.ok(income);
    }
    
}
