package com.e_learning.entities;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@NoArgsConstructor
@Data
public class Answer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String content;

	private String imageName;
	
	 private LocalDateTime addedDate;
	 
	 private Double score;
	 
	@ManyToOne
	 @JoinColumn(name = "exam_id")
	private Exam exam;
	
	@ManyToOne
	private User user;
}
