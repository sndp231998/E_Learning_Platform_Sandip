package com.e_learning.payloads;

import java.time.LocalDateTime;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.e_learning.entities.Category;
import com.e_learning.entities.Notice;
import com.e_learning.entities.User;
import com.e_learning.entities.Notice.NoticeType;

import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class NoticeDto {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

	
    private String content;
	
	  private LocalDateTime addedDate;
	  
	  private String imageName;
	  
	  
	  private Boolean isRead = false; // Default is unread // false for unread, true for read

	    private LocalDateTime readDate; // Optional: timestamp when the notice was read
	  
	  private Notice.NoticeType noticeType; 
//	  private NoticeType noticeType;
//
//	  public enum NoticeType {
//	        FOR_ALL, FOR_SUBSCRIBER
//	    }
	
	    private CategoryDto category;

	    private UserDto user;
}
