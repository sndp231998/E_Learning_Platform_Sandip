package com.e_learning.payloads;

import java.time.LocalDateTime;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.e_learning.entities.User;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class PaymentDto {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer paymentId;
	    
	    private Integer totalPrice;
	    
	    private  LocalDateTime addedDate;
	    
	    private  String validDate;
	    
	    private String payment_screensort;
	    
	   
	    private UserDto user;
	    private CategoryDto category;
}
