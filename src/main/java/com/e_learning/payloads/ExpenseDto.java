package com.e_learning.payloads;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.e_learning.entities.User;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class ExpenseDto {
	
private Integer expenseId;
	
private LocalDate expensedate;

private String particular;

private double amount;

//private UserDto userId;


}
