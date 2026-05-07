package com.expensetracker.expensetracker.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class Income_ExpensesResponse {
    private String title;
    private BigDecimal amount;
    private String category;
    private LocalDate date;
}
