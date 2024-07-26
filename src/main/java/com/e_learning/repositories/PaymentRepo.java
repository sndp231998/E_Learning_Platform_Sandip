package com.e_learning.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e_learning.entities.Payment;

import com.e_learning.entities.User;

public interface PaymentRepo extends JpaRepository<Payment,Integer>{

	List<Payment> findByUser(User user);
}
