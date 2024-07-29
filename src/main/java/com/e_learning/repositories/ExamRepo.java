package com.e_learning.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.e_learning.entities.Category;
import com.e_learning.entities.Exam;
import com.e_learning.entities.Post;
import com.e_learning.entities.User;

public interface ExamRepo extends JpaRepository<Exam, Integer>{

	List<Exam> findByCategory(Category category);
	//List<Exam> findByCategory(String categoryTitle);
	List<Exam> findByUser(User user);
	@Query("select e from Exam e where e.title like :key")
	List<Exam> searchByTitle(@Param("key") String title);

}
