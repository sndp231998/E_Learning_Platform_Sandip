package com.e_learning.repositories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e_learning.entities.Expense;

public interface ExpenseRepo extends JpaRepository<Expense,Integer> {

	List<Expense> findByExpensedateBetween(LocalDateTime start, LocalDateTime end);

	List<Expense> findByExpensedate(LocalDate date);
}
