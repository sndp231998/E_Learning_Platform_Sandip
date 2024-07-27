//package com.e_learning.services.impl;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//import com.e_learning.entities.DailyBalance;
//import com.e_learning.entities.Expense;
//import com.e_learning.entities.Income;
//import com.e_learning.repositories.DailyBalanceRepo;
//import com.e_learning.repositories.ExpenseRepo;
//import com.e_learning.repositories.IncomeRepo;
//
//@Service
//public class BalanceCalculationService {
//
//    private static final Logger logger = LoggerFactory.getLogger(BalanceCalculationService.class);
//
//    @Autowired
//    private IncomeRepo incomeRepo;
//
//    @Autowired
//    private ExpenseRepo expenseRepo;
//    
//    @Autowired
//    private DailyBalanceRepo dailyBalanceRepo;
//
//   
//    @Scheduled(cron = "0 0 0 * * ?") // Runs at midnight every day
//   // @Scheduled(cron = "0 * * * * ?") //everymin
//    public void calculateDailyBalance() {
//        LocalDateTime startOfDay = LocalDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
//        LocalDateTime endOfDay = startOfDay.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
//        logger.info("Calculating daily balance for the period: {} to {}", startOfDay, endOfDay);
//        List<Income> incomes = incomeRepo.findByIncomedateBetween(startOfDay, endOfDay);
//        List<Expense> expenses = expenseRepo.findByExpensedateBetween(startOfDay, endOfDay);
//
//        double totalIncome = incomes.stream().mapToDouble(Income::getAmount).sum(); 
//        double totalExpense = expenses.stream().mapToDouble(Expense::getAmount).sum();
//        //c = (a > b) ? a : b;
//        //comparing both and assign grater in a;
//        double a= (totalIncome > totalExpense)? totalIncome : totalExpense;
//        
//        logger.info("Total Income: {}", totalIncome);
//        logger.info("Total Expense: {}", totalExpense);
//         
//        
//        DailyBalance dailyBalance = new DailyBalance();
//        dailyBalance.setTotalincome(a);
//        dailyBalance.setTotalexpense(a);
//        
//        dailyBalance.setDate(endOfDay);
//        
//        if (totalIncome > totalExpense) {
//            double gain = totalIncome - totalExpense;
//            dailyBalance.setProfit(gain);
//            logger.info("Profit for the day: {}", gain);
//        } else {
//            double loose = totalExpense - totalIncome;
//            dailyBalance.setLoss(loose);
//            logger.info("Loss for the day: {}", loose);
//        }
//        
//        dailyBalanceRepo.save(dailyBalance);
//        logger.info("Daily balance saved: {}", dailyBalance);
//        
//        
//        // Create next day's opening balance entry
//        createNextDayOpeningBalance(totalIncome, totalExpense, endOfDay);
//        
//        
//    }
//
//    private void createNextDayOpeningBalance(double totalIncome, double totalExpense, LocalDateTime endOfDay) {
//        LocalDateTime nextDay = endOfDay.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
//
//        logger.info("Creating  for the next day: {}", nextDay);
//
//        if (totalIncome > totalExpense) {
//            double to_blc_cd = totalIncome - totalExpense; //profit  vako 
//           Expense exx=new Expense();
//           exx.setExpensedate(nextDay);
//           exx.setParticular("By balance b/d");
//             exx.setProfitvako(to_blc_cd);//--->BY balance b/d right side
//            
//          expenseRepo.save(exx);
//            logger.info("expenses see created: {}",exx);
//        } else {
//            double loose = totalExpense - totalIncome;
//            Income inc = new Income();
//            inc.setIncomedate(nextDay);
//           inc.setLossvako(loose);
//            inc.setParticular("To balance c/d");
//           inc.setAmount(loose);
//            incomeRepo.save(inc);
//            
//            logger.info(" expense created: {}", inc);
//
//        }
//    }
//}
