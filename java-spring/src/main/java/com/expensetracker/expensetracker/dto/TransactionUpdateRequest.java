package com.expensetracker.expensetracker.dto;

import com.expensetracker.expensetracker.entity.TransactionType;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionUpdateRequest {
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    private String title;

    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private TransactionType type;

    @Size(max = 50, message = "Category must not exceed 50 characters")
    private String category;

    @PastOrPresent(message = "Date cannot be in the future")
    private LocalDate date;

    public void normalizeCategory(){
        if(category != null){
            category = category.strip().toLowerCase();
            if(category.isEmpty()){
                category = "other";
            }
        }
        
    }
}
