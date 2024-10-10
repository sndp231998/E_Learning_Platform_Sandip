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

	

	//@Query("SELECT p.categories FROM Payment p WHERE p.user.userId = :userId")
	//List<Category> findCategoriesByUserId(Integer userId);
	@Query("SELECT c FROM Payment p JOIN p.categories c WHERE p.user.id = :userId")
	List<Category> findCategoriesByUserId(@Param("userId") Integer userId);


}
