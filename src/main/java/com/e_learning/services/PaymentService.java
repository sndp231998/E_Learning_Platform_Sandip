package com.e_learning.services;

import java.util.List;

import com.e_learning.payloads.PaymentDto;



public interface PaymentService {

	
	//create
	PaymentDto createPayment(PaymentDto paymentDto, Integer userId, List<Integer> categoryIds);
	
	//List<PaymentDto>getPaymentsByFaculty(Integer userId);
	
	List<PaymentDto> getAllPayments();
}
