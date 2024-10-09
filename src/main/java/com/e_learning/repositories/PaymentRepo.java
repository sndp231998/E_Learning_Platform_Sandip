package com.e_learning.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.e_learning.entities.Category;
import com.e_learning.entities.Payment;

import com.e_learning.entities.User;

public interface PaymentRepo extends JpaRepository<Payment,Integer>{

	List<Payment> findByUser(User user);

	@Query("SELECT c FROM Category c JOIN Payment p ON c.id = p.category.id WHERE p.user.id = :userId")
	List<Category> findCategoriesByUserId(@Param("userId") Integer userId);

}
