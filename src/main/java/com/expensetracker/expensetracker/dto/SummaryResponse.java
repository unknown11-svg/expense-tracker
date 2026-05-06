package com.expensetracker.expensetracker.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter
@Builder
@NoArgsConstructor @AllArgsConstructor
public class SummaryResponse {
    private BigDecimal income;
    private BigDecimal expenses;
    private BigDecimal balance;
}
