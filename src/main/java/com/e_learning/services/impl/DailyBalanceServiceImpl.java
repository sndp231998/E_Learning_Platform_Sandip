package com.e_learning.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_learning.entities.DailyBalance;
import com.e_learning.entities.Expense;
import com.e_learning.entities.Income;
import com.e_learning.exceptions.ResourceNotFoundException;

import com.e_learning.payloads.DailyBalanceDto;
import com.e_learning.repositories.DailyBalanceRepo;
import com.e_learning.repositories.ExpenseRepo;
import com.e_learning.repositories.IncomeRepo;
import com.e_learning.services.DailyBalanceService;
@Service
public class DailyBalanceServiceImpl implements DailyBalanceService {

    private static final Logger logger = LoggerFactory.getLogger(DailyBalanceService.class);

    @Autowired
    private IncomeRepo incomeRepo;

    @Autowired
    private ExpenseRepo expenseRepo;

    @Autowired
    private DailyBalanceRepo dailyBalanceRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public DailyBalanceDto createBalance(DailyBalanceDto dailyBalanceDto) {

        List<Income> incomes = this.incomeRepo.findAll(); // Replace with actual method
        List<Expense> expenses = this.expenseRepo.findAll(); // Replace with actual method

        if (!incomes.isEmpty() && !expenses.isEmpty()) {
            double totalIncome = incomes.stream().mapToDouble(Income::getAmount).sum();
            double totalExpense = expenses.stream().mapToDouble(Expense::getAmount).sum();

            dailyBalanceDto.setTotalincome(totalIncome);
            dailyBalanceDto.setTotalexpense(totalExpense);
            dailyBalanceDto.setDate(LocalDateTime.now());

            if (totalIncome > totalExpense) {
                dailyBalanceDto.setProfit(totalIncome - totalExpense);
            } else if (totalIncome < totalExpense) {
                dailyBalanceDto.setLoss(totalExpense - totalIncome);
            } else {
                dailyBalanceDto.setProfit(0.0);
                dailyBalanceDto.setLoss(0.0);
            }

            DailyBalance dailyBalance = this.modelMapper.map(dailyBalanceDto, DailyBalance.class);
            dailyBalance = this.dailyBalanceRepo.save(dailyBalance);
            return this.modelMapper.map(dailyBalance, DailyBalanceDto.class);
        } else {
            throw new IllegalArgumentException("Income or Expense data is missing.");
        }
    }

    @Override
    public List<DailyBalanceDto> getDailyBalances() {
    	List<DailyBalance> dailyblc = this.dailyBalanceRepo.findAll();
		List<DailyBalanceDto> DblcDtos = dailyblc.stream().map((cat) -> this.modelMapper.map(cat, DailyBalanceDto.class))
				.collect(Collectors.toList());

		return DblcDtos;
    }

    @Override
    public DailyBalance get(Integer Id) {
        // TODO Auto-generated method stub
        return null;
    }
}
