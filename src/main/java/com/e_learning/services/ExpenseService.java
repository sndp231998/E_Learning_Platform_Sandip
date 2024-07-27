package com.e_learning.services;

import java.util.List;

import com.e_learning.payloads.ExpenseDto;
import com.e_learning.payloads.IncomeDto;

public interface ExpenseService {

	//create
			ExpenseDto createExpense(ExpenseDto expenseDto, Integer userId);
			
			
			
			List<ExpenseDto> getAllExpenses();
}
