package com.e_learning.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e_learning.entities.Income;

public interface IncomeRepo extends JpaRepository<Income,Integer>{

	List<Income> findByIncomedateBetween(LocalDateTime start, LocalDateTime end);
}
