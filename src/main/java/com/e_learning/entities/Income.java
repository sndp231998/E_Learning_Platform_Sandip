package com.e_learning.entities;


import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
public class Income {
	  @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer incomeId;
	
		private LocalDateTime incomedate;
		
		private String particular;
		
		private double amount;
		
		
	
	 @ManyToOne
	    private User user;
	 
	 
	 @ManyToOne
	 private DailyBalance dailyBalance;
	
	
}
