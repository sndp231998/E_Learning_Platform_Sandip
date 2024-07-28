package com.e_learning.services.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
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
import com.e_learning.payloads.DayWiseDataDto;
import com.e_learning.payloads.ExpenseDto;
import com.e_learning.payloads.IncomeDto;
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

        List<Income> incomes = this.incomeRepo.findAll();
        List<Expense> expenses = this.expenseRepo.findAll();

        double totalIncome = incomes.stream().mapToDouble(Income::getAmount).sum();
        double totalExpense = expenses.stream().mapToDouble(Expense::getAmount).sum();

        // Get the current date without time
        LocalDate today = LocalDate.now();
        Optional<DailyBalance> dailyBalanceOptional = this.dailyBalanceRepo.findByDate(today);

        logger.info("seeing " + dailyBalanceOptional);
        DailyBalance dailyBalance;
        if (dailyBalanceOptional.isPresent()) {
            dailyBalance = dailyBalanceOptional.get();
        } else {
            // Create new daily balance
            dailyBalance = new DailyBalance();
            dailyBalance.setDate(today);
        }

        dailyBalance.setTotalincome(totalIncome);
        dailyBalance.setTotalexpense(totalExpense);

        if (totalIncome > totalExpense) {
            dailyBalance.setProfit(totalIncome - totalExpense);
            dailyBalance.setLoss(0.0);
        } else if (totalIncome < totalExpense) {
            dailyBalance.setLoss(totalExpense - totalIncome);
            dailyBalance.setProfit(0.0);
        } else {
            dailyBalance.setProfit(0.0);
            dailyBalance.setLoss(0.0);
        }

        dailyBalance = this.dailyBalanceRepo.save(dailyBalance);
        return this.modelMapper.map(dailyBalance, DailyBalanceDto.class);
    }

    @Override
    public List<DailyBalanceDto> getDailyBalances() {
        List<DailyBalance> dailyblc = this.dailyBalanceRepo.findAll();
        List<DailyBalanceDto> DblcDtos = dailyblc.stream()
                .map((cat) -> this.modelMapper.map(cat, DailyBalanceDto.class))
                .collect(Collectors.toList());

        return DblcDtos;
    }

    @Override
    public DayWiseDataDto getDayWiseData(LocalDate date) {
        List<Income> incomes = this.incomeRepo.findByIncomedate(date);
        List<Expense> expenses = this.expenseRepo.findByExpensedate(date);
        Optional<DailyBalance> dailyBalanceOpt = this.dailyBalanceRepo.findByDate(date);

        DayWiseDataDto dayWiseDataDto = new DayWiseDataDto();
        dayWiseDataDto.setExpenses(expenses.stream()
                .map(expense -> this.modelMapper.map(expense, ExpenseDto.class))
                .collect(Collectors.toList()));
        dayWiseDataDto.setIncomes(incomes.stream()
                .map(income -> this.modelMapper.map(income, IncomeDto.class))
                .collect(Collectors.toList()));

        dailyBalanceOpt.ifPresent(dailyBalance -> 
            dayWiseDataDto.setDailyBalance(this.modelMapper.map(dailyBalance, DailyBalanceDto.class))
        );

        return dayWiseDataDto;
    }

    @Override
    public DailyBalance get(Integer Id) {
        // TODO Auto-generated method stub
        return null;
    }
}
