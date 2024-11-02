package com.e_learning.payloads;

import java.time.LocalDateTime;


import com.e_learning.entities.Exam;

import lombok.Data;
@Data
public class AnswerDto {

	private int id;

	private String content;

	private String imageName;
	
	private Double score; 
	
	 private LocalDateTime addedDate;
	 
	
	private ExamDto exam;
	
	private UserDto user;
	
	
}
