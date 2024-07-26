package com.e_learning.entities;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
public class Payment {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer paymentId;
	    
	    private Integer amount;
	    
	    private  LocalDateTime addedDate;
	    
	    private  String validDate;
	    
	    private String payment_screensort;
	    
	    private String faculty;
	    
	    @ManyToOne
	    private User user;
	
	   
	    
}
