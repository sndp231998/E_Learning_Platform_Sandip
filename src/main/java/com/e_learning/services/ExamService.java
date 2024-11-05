package com.e_learning.services;

import java.util.List;

import com.e_learning.payloads.CategoryDto;
import com.e_learning.payloads.ExamDto;
import com.e_learning.payloads.ExamResponse;
import com.e_learning.payloads.PostDto;
import com.e_learning.payloads.PostResponse;

public interface ExamService {

	//create 

			ExamDto createExam(ExamDto examDto,Integer userId,Integer categoryId);

			//update 

			ExamDto updateExam(ExamDto examDto, Integer examId);

			// delete

			void deleteExam(Integer examId);
			
			//get all exams
			List<ExamDto> getExams();
			
			
			//get single post
			
			ExamDto getExamById(Integer examId);
			
			
			//get all exams by category
			
			List<ExamDto> getExamsByCategory(Integer categoryId);
			
			//get all exams by user
			List<ExamDto> getExamsByUser(Integer userId);
			
			//List<ExamDto>getExamsByUserFaculty(Integer userId);
			
			List<ExamDto>getExamsByUserFaculty(Integer userId, String faculty) ;
			List<ExamDto> searchExams(String keyword);
}
