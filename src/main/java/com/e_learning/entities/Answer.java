package com.e_learning.entities;

import java.time.LocalDateTime;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@Data
public class Answer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String content;

	private String imageName;
	
	 private LocalDateTime addedDate;
	 
	@ManyToOne
	private Exam exam;
}
