package com.e_learning.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e_learning.entities.User;
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
				// Extract the user ID from the authentication context
			    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			    User userDetails = (User) authentication.getPrincipal();
			    Integer tokenUserId = userDetails.getId(); // Get the user ID from the token


			    // Compare the user ID from the token with the user ID from the path variable
			    if (!tokenUserId.equals(userId)) {
			        return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
			    }		
				return new ResponseEntity<ExpenseDto>(createExpense, HttpStatus.CREATED);
			}
			
			
			//Get all expense
			@GetMapping("/expenses")
			public ResponseEntity<List<ExpenseDto>> getExpenses() {
				List<ExpenseDto> expenses = this.expenseService.getAllExpenses();
				return ResponseEntity.ok(expenses);
			}
		
			
}
