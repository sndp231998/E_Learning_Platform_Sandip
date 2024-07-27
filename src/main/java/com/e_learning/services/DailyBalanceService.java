package com.e_learning.services;

import java.util.List;


import com.e_learning.entities.DailyBalance;
import com.e_learning.payloads.CategoryDto;
import com.e_learning.payloads.DailyBalanceDto;
import com.e_learning.payloads.PostDto;

public interface DailyBalanceService {
	
	//create
	DailyBalanceDto createBalance(DailyBalanceDto dailyBalanceDto);
	


	//get all

	List<DailyBalanceDto> getDailyBalances();
	//get single
	DailyBalance get(Integer Id);
	
	
}
