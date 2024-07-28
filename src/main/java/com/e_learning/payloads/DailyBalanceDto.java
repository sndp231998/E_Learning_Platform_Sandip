package com.e_learning.payloads;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class DailyBalanceDto {

	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;
	    
	    private LocalDate date;
	    
	    private double profit;  
	    private double loss;  
	  
	    private double totalincome;
	    private double totalexpense;
}
