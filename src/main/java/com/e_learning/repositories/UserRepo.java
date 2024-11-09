package com.e_learning.repositories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.e_learning.entities.User;

import io.lettuce.core.dynamic.annotation.Param;



public interface UserRepo extends JpaRepository<User, Integer>{
	
	Optional<User> findByEmail(String email);
	Optional<User> findByMobileNo(String mobileNo);
	
	@Query("SELECT u FROM User u JOIN u.facult f WHERE f = :faculty")
	List<User> findByFaculty(@Param("faculty") String faculty);

	
	List<User> findByCollegename(String collegename);
	
	@Query("SELECT f FROM User u JOIN u.facult f WHERE u.id = :id")
    List<String> findFacultiesByUserId(@Param("id") int id);
//
//@Query("SELECT DISTINCT u.faculty FROM User u")
//List<String> findAllFaculties();

@Query("SELECT u FROM User u WHERE u.dateOfRegistration >= :sevenDaysAgo")
List<User> findUsersJoinedInLast7Days(LocalDateTime sevenDaysAgo);

List<User> findAllByTrialExpiryDateBefore(LocalDateTime now);

//@Query("SELECT u FROM User u JOIN u.roles r WHERE r.name IN ('TEACHER_USER', 'SUBSCRIBED_USER')")
//List<User> findByRolesTeacherOrSubscribed();

//@Query("SELECT u FROM User u JOIN u.roles r WHERE LOWER(r.name) IN ('ROLE_TEACHER', 'ROLE_SUBSCRIBER')")
@Query("SELECT u FROM User u JOIN u.roles r WHERE r.name IN ('ROLE_TEACHER', 'ROLE_SUBSCRIBED')")
List<User> findByRolesTeacherOrSubscribed();


}