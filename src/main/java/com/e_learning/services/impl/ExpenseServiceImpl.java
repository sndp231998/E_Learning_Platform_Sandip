package com.e_learning.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_learning.entities.Expense;
import com.e_learning.entities.User;
import com.e_learning.exceptions.ResourceNotFoundException;
import com.e_learning.payloads.ExpenseDto;

import com.e_learning.repositories.ExpenseRepo;
import com.e_learning.repositories.UserRepo;
import com.e_learning.services.ExpenseService;

@Service
public class ExpenseServiceImpl implements ExpenseService{
	@Autowired
    private ExpenseRepo expenseRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public ExpenseDto createExpense(ExpenseDto expenseDto, Integer userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Expense expense = modelMapper.map(expenseDto, Expense.class);
        expense.setExpensedate((LocalDateTime.now()));
        
        expense.setUser(user);
        
        Expense savedExpense = expenseRepo.save(expense);
        
        return modelMapper.map(savedExpense, ExpenseDto.class);
    }

    @Override
    public List<ExpenseDto> getAllExpenses() {
        List<Expense> expenses = expenseRepo.findAll();
        return expenses.stream().map(expense -> modelMapper.map(expense, ExpenseDto.class)).collect(Collectors.toList());
    }
}

