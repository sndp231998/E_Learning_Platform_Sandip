package com.e_learning.services;

import java.util.List;

import com.e_learning.payloads.AnswerDto;
import com.e_learning.payloads.ExamDto;
import com.e_learning.payloads.PostDto;
import com.e_learning.payloads.UserDto;


public interface AnswerService {
	AnswerDto createAnswer(AnswerDto answerDto, Integer examId, Integer userId);

	void deleteAnswer(Integer answerId);
	
	//update 

	AnswerDto updateAnswer(AnswerDto answerDto, Integer answerId);
	
	//get single answer
	
	AnswerDto getAnswerById(Integer answerId);
	
	//List<AnswerDto> findByExamCategory(String categoryTitle);
	
	List<AnswerDto> getAnswersByExam(Integer examId);

	AnswerDto updateScore(AnswerDto answerDto, Integer answerId);
}
