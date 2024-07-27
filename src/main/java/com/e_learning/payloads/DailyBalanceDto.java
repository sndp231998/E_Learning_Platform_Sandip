package com.e_learning.payloads;

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
	    
	    private LocalDateTime date;
	    
	    private double profit;  // Carry forward profit
	    private double loss;  // Carry forward loss//private double closingBalance;
	    
	    
	    
	    private double totalincome;
	    private double totalexpense;
}
