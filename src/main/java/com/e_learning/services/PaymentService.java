package com.e_learning.services;

import java.util.List;

import com.e_learning.payloads.CategoryDto;
import com.e_learning.payloads.PaymentDto;



public interface PaymentService {

	
	//create
	PaymentDto createPayment(PaymentDto paymentDto, Integer userId, List<Integer> categoryIds);
	
	//List<PaymentDto>getPaymentsByFaculty(Integer userId);
	
	List<PaymentDto> getAllPayments();
	
	PaymentDto rejectPayment(Integer paymentId);
	 PaymentDto approvePayment(Integer paymentId);
	
	 boolean isCategoryPaymentByUser(Integer userId, Integer categoryId);

	       // get
			PaymentDto getPayment(Integer paymentId);
			//Update 
			PaymentDto updatePayment(PaymentDto paymentDto, Integer paymentId);

}
