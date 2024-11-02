package com.e_learning.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notics")
@Data
@NoArgsConstructor
public class Notice {
	
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	//@Column(name = "notice_id")
    private Long noticeId;

	@Column(length = 1000000000)
    private String content;
	
	  private LocalDateTime addedDate;
	  
	  private String imageName;
      
	  @Enumerated(EnumType.STRING)
	  private NoticeType noticeType;

	  public enum NoticeType {
	        FOR_ALL, FOR_SUBSCRIBER
	    }

	    private LocalDateTime readDate; // Optional: timestamp when the notice was read
	    @ManyToOne
	    @JoinColumn(name = "category_id")
	    private Category category;

	  @ManyToOne
	  @JoinColumn(name = "user_id") // Specify the foreign key column for the User entity
	    private User user;
}
