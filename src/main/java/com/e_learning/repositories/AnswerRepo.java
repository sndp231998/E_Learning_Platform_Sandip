package com.e_learning.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.e_learning.entities.Answer;
import com.e_learning.entities.Category;
import com.e_learning.entities.Exam;
import com.e_learning.entities.Post;
import com.e_learning.entities.User;
import com.e_learning.payloads.AnswerDto;
import com.e_learning.payloads.PostDto;

import io.lettuce.core.dynamic.annotation.Param;


public interface AnswerRepo extends JpaRepository<Answer, Integer> {

	List<Answer> findByExam(Exam exm);
//	@Query("SELECT a FROM Answer a JOIN a.exam e JOIN e.category c WHERE c.categorytitle = :categoryTitle")
//    List<Answer> findByExamCategory(@Param("categoryTitle") String categoryTitle);
//	
	 

}
