package com.e_learning.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e_learning.entities.Answer;


public interface AnswerRepo extends JpaRepository<Answer, Integer> {

}
