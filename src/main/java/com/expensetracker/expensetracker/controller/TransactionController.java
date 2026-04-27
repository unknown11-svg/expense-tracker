package com.expensetracker.expensetracker.controller;

import com.expensetracker.expensetracker.dto.TransactionRequest;
import com.expensetracker.expensetracker.dto.TransactionResponse;
import com.expensetracker.expensetracker.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


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

    //DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTranasction(@PathVariable Integer id){
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
    
}
