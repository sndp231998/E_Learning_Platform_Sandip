package com.e_learning.payloads;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.e_learning.entities.Category;
import com.e_learning.entities.Exam.ExamType;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class ExamDto {

    @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer examId;

  private String title;
  
  private String imageName;
  
  private LocalDateTime addedDate;
  
  private LocalDateTime deadline;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  
	private CategoryDto category;

	private ExamType examType;
	
	//private UserDto user;
}
