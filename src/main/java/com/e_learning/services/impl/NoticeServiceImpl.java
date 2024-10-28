package com.e_learning.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_learning.entities.Category;
import com.e_learning.entities.Exam;
import com.e_learning.entities.Notice;
import com.e_learning.entities.User;
import com.e_learning.entities.UserNoticeStatus;
import com.e_learning.exceptions.ResourceNotFoundException;
import com.e_learning.payloads.ExamDto;
import com.e_learning.payloads.NoticeDto;
import com.e_learning.payloads.UserDto;
import com.e_learning.repositories.CategoryRepo;
import com.e_learning.repositories.NoticeRepo;
import com.e_learning.repositories.UserNoticeStatusRepo;
import com.e_learning.repositories.UserRepo;
import com.e_learning.services.NoticeService;

@Service
public  class NoticeServiceImpl implements NoticeService{
    
	@Autowired
	private NoticeRepo noticeRepo;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserNoticeStatusRepo userNoticeStatusRepo;
    
    @Autowired
    private CategoryRepo categoryRepo;
      
    
    
 // Method to mark a notice as read by a specific user
    public void markNoticeAsRead(Integer userId, Long noticeId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        Notice notice = noticeRepo.findById(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("Notice", "noticeId", noticeId));

        UserNoticeStatus status = userNoticeStatusRepo.findByUser_IdAndNotice_NoticeId(userId, noticeId)
                .orElseGet(() -> {
                    UserNoticeStatus newStatus = new UserNoticeStatus();
                    newStatus.setUser(user);
                    newStatus.setNotice(notice);
                    return newStatus;
                });

        status.setReadStatus(true);
        status.setReadDate(LocalDateTime.now());
        userNoticeStatusRepo.save(status);
    }
    
	
    // Check if a specific user has read a specific notice
 public boolean hasUserReadNotice(Integer userId, Long noticeId) {
     return userNoticeStatusRepo.findByUser_IdAndNotice_NoticeId(userId, noticeId)
             .map(UserNoticeStatus::getReadStatus)
             .orElse(false); // Returns false if no record is found (meaning it's unread)
 }
    
    // Retrieve unread notices for a specific user
    public List<NoticeDto> getUnreadNoticesByUser(Integer userId) {
        List<UserNoticeStatus> unreadStatuses = userNoticeStatusRepo.findByUser_IdAndReadStatus(userId, false);
        
        return unreadStatuses.stream()
                .map(status -> modelMapper.map(status.getNotice(), NoticeDto.class))
                .collect(Collectors.toList());
    }
    
    

    
    
    
	@Override
	public NoticeDto createNotice(NoticeDto noticeDto, Integer userId, Integer categoryId) {
		 User user = this.userRepo.findById(userId)
	                .orElseThrow(() -> new ResourceNotFoundException("User", "User id", userId));

	        Category category = this.categoryRepo.findById(categoryId)
	                .orElseThrow(() -> new ResourceNotFoundException("Category", "category id", categoryId));

	        Notice notice = this.modelMapper.map(noticeDto, Notice.class);
	        notice.setImageName("");
	        notice.setAddedDate(LocalDateTime.now());
	        notice.setIsRead(noticeDto.getIsRead());
            
	        notice.setContent(noticeDto.getContent());
	        if (noticeDto.getNoticeType() == Notice.NoticeType.FOR_SUBSCRIBER) {
	        	notice.setNoticeType(noticeDto.getNoticeType());
	        
	        notice.setUser(user);
	        notice.setCategory(category);
			

			Notice newnotice= this.noticeRepo.save(notice);

	        return this.modelMapper.map(newnotice, NoticeDto.class);
	        } else {
		        throw new IllegalArgumentException("Please choose noticeType = FOR_SUBSCRIBE. Provided: " + noticeDto.getNoticeType());
		    }
	}
	//if= user noticeType=FOR_ALL
	@Override
	public NoticeDto createNotice(NoticeDto noticeDto, Integer userId) {
	    User user = this.userRepo.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User", "User id", userId));

	    Notice notice = this.modelMapper.map(noticeDto, Notice.class);
	    notice.setImageName("");
	    notice.setAddedDate(LocalDateTime.now());
	    notice.setIsRead(noticeDto.getIsRead());
	    notice.setContent(noticeDto.getContent());

	    // Check if the notice type is FOR_ALL
	    if (noticeDto.getNoticeType() == Notice.NoticeType.FOR_ALL) { // Use == for enums
	        notice.setNoticeType(noticeDto.getNoticeType());
	        notice.setUser(user);
	        Notice newNotice = this.noticeRepo.save(notice);
	        return this.modelMapper.map(newNotice, NoticeDto.class);
	    } else {
	        throw new IllegalArgumentException("Please choose noticeType = FOR_ALL. Provided: " + noticeDto.getNoticeType());
	    }
	}


//for all user 
	@Override
	public List<NoticeDto> getAllNotices() {
	    // No need for toString() now as the method accepts NoticeType
	    List<Notice> notices = this.noticeRepo.findByNoticeType(Notice.NoticeType.FOR_ALL);
	    
	    return notices.stream()
	                  .map(this::noticeToDto)
	                  .collect(Collectors.toList());
	}


	
	public Notice dtoToNotice(NoticeDto noticeDto) {
        return this.modelMapper.map(noticeDto, Notice.class);
    }

    public NoticeDto noticeToDto(Notice notice) {
        return this.modelMapper.map(notice, NoticeDto.class);
    }



	

	@Override
	public List<NoticeDto> getNoticesByUserId(Integer userId) {
	    List<Notice> notices = noticeRepo.findByUser_Id(userId); // Using a custom query in NoticeRepo
	    return notices.stream().map(this::noticeToDto).collect(Collectors.toList());
	}




	@Override
	public List<NoticeDto> getNoticsByUserFaculty(Integer userId, String faculty) {
		//get user id
		User user = this.userRepo.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
//	    
//	    // Get the user's faculties (multiple faculties)
	    List<String> userFacult = user.getFacult();
//	    
//	    // Check if the provided faculty exists in the user's faculty list
	    if (!userFacult.contains(faculty)) {
        throw new ResourceNotFoundException("Faculty", "faculty", faculty);
	    }
//	    // Find the category that matches the provided faculty
	    Category category = this.categoryRepo.findByCategoryTitle(faculty);
	    if (category == null) {
	        throw new ResourceNotFoundException("Category", "title", faculty);
	    }
	    // Fetch notics associated with the category
	    List<Notice> notics = this.noticeRepo.findByCategory(category);
//	    // Convert exams to ExamDto
	    List<NoticeDto> noticeDtos = notics.stream()
	                                  .map(notice -> this.modelMapper.map(notice, NoticeDto.class))
	                                  .collect(Collectors.toList());
//
	    return noticeDtos;
	}



}
