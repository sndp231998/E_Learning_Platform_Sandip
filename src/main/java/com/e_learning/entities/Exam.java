package com.e_learning.entities;

import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@NoArgsConstructor
public class Exam {

	      @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer examId;
	 
	    private String title;
	    
	    private String imageName;
	    
	    private LocalDateTime addedDate;
	    
	    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	    private String  deadline;
	    
	    @ManyToOne
	    @JoinColumn(name = "category_id")
	    private Category category;
	    
	    @ManyToOne
	    private User user;
	    
	    
}
