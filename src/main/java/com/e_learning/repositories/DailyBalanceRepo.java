package com.e_learning.repositories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.e_learning.entities.DailyBalance;
import com.e_learning.payloads.DayWiseDataDto;

public interface DailyBalanceRepo extends JpaRepository<DailyBalance, Integer> {
	
	 Optional<DailyBalance> findByDate(LocalDate date);
	
}

