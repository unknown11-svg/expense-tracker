package com.expensetracker.expensetracker.service;

import com.expensetracker.expensetracker.dto.TransactionRequest;
import com.expensetracker.expensetracker.dto.TransactionResponse;
import com.expensetracker.expensetracker.entity.Transaction;
import com.expensetracker.expensetracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    //CREATE
    public TransactionResponse createTransaction(TransactionRequest request){
        Transaction transaction = (Transaction.builder()
        .title(request.getTitle())
        .amount(request.getAmount())
        .type(request.getType())
        .category(request.getCategory())
        .date(request.getDate())
        .build());
        
        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    //READ ALL
    public List<TransactionResponse> getAllTransactions(){
        return transactionRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // READ ONE
    public TransactionResponse getTransactionById( Integer id){
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found with id: "+ id));

        return mapToResponse(transaction);
    }

    //DELETE

    public void deleteTransaction( Integer id){
        if(!transactionRepository.existsById(id)){
            throw new RuntimeException("Transaction not found with this id: "+ id);
        }
        transactionRepository.deleteById(id);
    }

    //Helper: converts Entity -> Response DTO
    private TransactionResponse mapToResponse( Transaction transaction){
        return (TransactionResponse.builder()
        .id(transaction.getId())
        .title(transaction.getTitle())
        .amount(transaction.getAmount())
        .type(transaction.getType())
        .category(transaction.getCategory())
        .date(transaction.getDate()).build());
    }
    
}
