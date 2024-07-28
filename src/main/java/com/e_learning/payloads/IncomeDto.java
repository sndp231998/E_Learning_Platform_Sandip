package com.e_learning.payloads;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.e_learning.entities.User;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IncomeDto {
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
private Integer incomeId;
	

	private LocalDate incomedate;
	
	private String particular;
	
	private double amount;
	
	//private UserDto user;
	
}
