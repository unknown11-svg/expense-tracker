package com.expensetracker.expensetracker.dto;

import com.expensetracker.expensetracker.entity.TransactionType;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class TransactionResponse {
    private Integer id;
    private String title;
    private BigDecimal amount;
    private TransactionType type;
    private String category;
    private LocalDate date;
}
