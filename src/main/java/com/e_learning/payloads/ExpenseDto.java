package com.e_learning.payloads;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class ExpenseDto {
	
private Integer expenseId;
	
private LocalDateTime expensedate;

private String particular;

private double amount;


}
