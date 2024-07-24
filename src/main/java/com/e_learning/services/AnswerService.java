package com.e_learning.services;

import com.e_learning.payloads.AnswerDto;
import com.e_learning.payloads.ExamDto;


public interface AnswerService {
	AnswerDto createAnswer(AnswerDto answerDto, Integer examId);

	void deleteAnswer(Integer answerId);
	
	//update 

	AnswerDto updateAnswer(AnswerDto answerDto, Integer answerId);
	
	//get single post
	
	AnswerDto getAnswerById(Integer answerId);
}
