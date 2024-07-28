package com.e_learning.services.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_learning.entities.Income;
import com.e_learning.entities.User;
import com.e_learning.exceptions.ResourceNotFoundException;
import com.e_learning.payloads.IncomeDto;
import com.e_learning.repositories.IncomeRepo;
import com.e_learning.repositories.UserRepo;
import com.e_learning.services.IncomeService;
@Service
public class IncomeServiceImpl implements IncomeService{

	 @Autowired
	    private IncomeRepo incomeRepo;

	    @Autowired
	    private UserRepo userRepo;

	    @Autowired
	    private ModelMapper modelMapper;

	    @Override
	    public IncomeDto createIncome(IncomeDto incomeDto, Integer userId) {
	        User user = userRepo.findById(userId)
	                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
	        
	        Income income = modelMapper.map(incomeDto, Income.class);
	        income.setUser(user);
	        income.setIncomedate((LocalDate.now()));
	        
	        
	        Income savedIncome = incomeRepo.save(income);
	        
	        return modelMapper.map(savedIncome, IncomeDto.class);
	    }

	    @Override
	    public List<IncomeDto> getAllIncomes() {
	        List<Income> incomes = incomeRepo.findAll();
	        return incomes.stream().map(income -> modelMapper.map(income, IncomeDto.class)).collect(Collectors.toList());
	    }
	}


