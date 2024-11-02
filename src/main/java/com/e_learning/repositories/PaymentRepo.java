package com.e_learning.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import com.e_learning.entities.Category;
import com.e_learning.entities.Payment;

import com.e_learning.entities.User;

public interface PaymentRepo extends JpaRepository<Payment,Integer>{

	@Query("SELECT p FROM Payment p WHERE p.user.id = :userId AND p.addedDate >= :within24Hours")
	List<Payment> findPaymentsByUserIdWithin24Hours(Integer userId, LocalDateTime within24Hours);

	
	@Query("SELECT p FROM Payment p WHERE p.user.id = :userId ORDER BY p.addedDate DESC")
	Payment findLastPaymentByUserId(Integer userId);
	
	// Fetch payments by user
    List<Payment> findByUser(User user);

    // Find if a user has already made a payment for a specific category
    @Query("SELECT p FROM Payment p JOIN p.categories c WHERE p.user = :user AND c = :category")
    Optional<Payment> findByUserAndCategory(@Param("user") User user, @Param("category") Category category);

    // Get all categories a user has already paid for
    @Query("SELECT c FROM Payment p JOIN p.categories c WHERE p.user.id = :userId")
    List<Category> findCategoriesByUserId(@Param("userId") Integer userId);

    @Query("SELECT SUM(p.totalPrice) FROM Payment p WHERE p.addedDate >= :startDate AND p.addedDate < :endDate")
    Integer calculateTotalRevenue(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

//    @Query("SELECT p FROM Payment p WHERE p.addedDate >= :startDate AND p.addedDate < :endDate")
//    List<Payment> findPaymentsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

	
}
