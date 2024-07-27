package com.e_learning.services;

import java.util.List;

import com.e_learning.payloads.IncomeDto;


public interface IncomeService {

	
	//create
		IncomeDto createIncome(IncomeDto paymentDto, Integer userId);
		
		
		
		List<IncomeDto> getAllIncomes();
}
