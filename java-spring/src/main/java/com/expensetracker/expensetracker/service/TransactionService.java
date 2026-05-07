package com.expensetracker.expensetracker.service;

import com.expensetracker.expensetracker.dto.TransactionRequest;
import com.expensetracker.expensetracker.dto.TransactionUpdateRequest;
import com.expensetracker.expensetracker.dto.TransactionResponse;
import com.expensetracker.expensetracker.dto.Income_ExpensesResponse;
import com.expensetracker.expensetracker.dto.SummaryResponse;
import com.expensetracker.expensetracker.entity.Transaction;
import com.expensetracker.expensetracker.entity.TransactionType;
import com.expensetracker.expensetracker.exceptions.InvalidDateRangeException;
import com.expensetracker.expensetracker.exceptions.NotFoundException;
import com.expensetracker.expensetracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;

    //CREATE
    @SuppressWarnings("null")
    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request){
        request.normalizeCategory();
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
        return transactionRepository.findAll(Sort.by(Sort.Direction.DESC, "date")).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // READ ONE
    public TransactionResponse getTransactionById( int id){
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new NotFoundException("Transaction not found with id: "+ id));

        return mapToResponse(transaction);
    }

    //UPDATE
    @Transactional
    public TransactionResponse updateTransaction(int id, TransactionUpdateRequest payload){
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new NotFoundException("Transaction not found with id: " + id));

        //Only update fields that are not null
        if(payload.getTitle() != null){
            transaction.setTitle(payload.getTitle());
        }
        if(payload.getAmount() != null){
            transaction.setAmount(payload.getAmount());
        }
        if(payload.getType() != null){
            transaction.setType(payload.getType());
        }
        if(payload.getCategory() != null){
            payload.normalizeCategory();
            transaction.setCategory(payload.getCategory());
        }
        if(payload.getDate() != null){
            transaction.setDate(payload.getDate());
        }

        Transaction savedTransaction = transactionRepository.save(transaction);

        return mapToResponse(savedTransaction);
    }

    //DELETE

    @Transactional
    public void deleteTransaction( int id){
        if(!transactionRepository.existsById(id)){
            throw new NotFoundException("Transaction not found with this id: "+ id);
        }
        transactionRepository.deleteById(id);
    }

    //Summary(Income, Expenses, Balance)
    public SummaryResponse getSummary(){
        BigDecimal income = transactionRepository.sumByType(TransactionType.income);
        BigDecimal expenses = transactionRepository.sumByType(TransactionType.expense);
        BigDecimal balance = income.subtract(expenses);

        return new SummaryResponse(income, expenses, balance);
    }

    //Filtering (Expenses) - Optional[Date, Category]
    public List<Income_ExpensesResponse> listExpenses(LocalDate startDate, LocalDate endDate, String category){
        if (startDate != null && endDate != null && endDate.isBefore(startDate)){
            throw new InvalidDateRangeException("End date must be later than start date");
        }

        List<Transaction> transactions = transactionRepository.findTransactions(TransactionType.expense, startDate, endDate, category);

        return transactions.stream().map(this::maptoIncExpResponse).collect(Collectors.toList());
    }

    public List<Income_ExpensesResponse> listIncome(LocalDate startDate, LocalDate endDate, String category){
        if (startDate != null && endDate != null && endDate.isBefore(startDate)){
            throw new InvalidDateRangeException("End date must be later than start date");
        }

        List<Transaction> transactions = transactionRepository.findTransactions(TransactionType.income, startDate, endDate, category);

        return transactions.stream().map(this::maptoIncExpResponse).collect(Collectors.toList());
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

    private Income_ExpensesResponse maptoIncExpResponse(Transaction transaction){
        return (Income_ExpensesResponse.builder()
        .title(transaction.getTitle())
        .amount(transaction.getAmount())
        .category(transaction.getCategory())
        .date(transaction.getDate()).build());
    }
    
}
