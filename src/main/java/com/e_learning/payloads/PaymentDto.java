package com.e_learning.payloads;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.e_learning.entities.Category;
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
	   // private List<CategoryDto> categories; // Change to List of CategoryDto
	    private List<CategoryDto> categories;
	    
	   // private List<Category> categories
	   
}
