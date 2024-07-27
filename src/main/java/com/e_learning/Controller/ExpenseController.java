package com.e_learning.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e_learning.payloads.ExpenseDto;
import com.e_learning.payloads.PaymentDto;
import com.e_learning.services.ExpenseService;


@RestController
@RequestMapping("/api/v1/")
public class ExpenseController {

	
	@Autowired
	private ExpenseService expenseService;

	//create Expense
			@PostMapping("/user/{userId}/expenses")
			public ResponseEntity<ExpenseDto> createExpense(@RequestBody ExpenseDto expenseDto, @PathVariable Integer userId) {
				ExpenseDto createExpense = this.expenseService.createExpense(expenseDto, userId);
						
				return new ResponseEntity<ExpenseDto>(createExpense, HttpStatus.CREATED);
			}
			
			
			//Get all expense
			@GetMapping("/expenses")
			public ResponseEntity<List<ExpenseDto>> getExpenses() {
				List<ExpenseDto> expenses = this.expenseService.getAllExpenses();
				return ResponseEntity.ok(expenses);
			}
		
			
}
