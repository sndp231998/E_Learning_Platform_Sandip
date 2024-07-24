package com.e_learning.payloads;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.e_learning.entities.Category;

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
  
  private String  deadline;
  
	private CategoryDto category;

	private UserDto user;
}
