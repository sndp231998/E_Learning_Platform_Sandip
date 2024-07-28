package com.e_learning.payloads;

import java.util.List;

import lombok.Data;

@Data
public class DayWiseDataDto {
    private List<IncomeDto> incomes;
    private List<ExpenseDto> expenses;
    private DailyBalanceDto dailyBalance;  // Assuming you have a DailyBalanceDto
}
