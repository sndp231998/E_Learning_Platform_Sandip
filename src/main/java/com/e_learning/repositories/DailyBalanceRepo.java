package com.e_learning.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.e_learning.entities.DailyBalance;

public interface DailyBalanceRepo extends JpaRepository<DailyBalance, Integer> {
}

