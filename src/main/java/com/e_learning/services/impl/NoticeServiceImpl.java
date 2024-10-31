package com.e_learning.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_learning.entities.Category;

import com.e_learning.entities.Notice;
import com.e_learning.entities.User;

import com.e_learning.exceptions.ResourceNotFoundException;

import com.e_learning.payloads.NoticeDto;

import com.e_learning.repositories.CategoryRepo;
import com.e_learning.repositories.NoticeRepo;

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
    private CategoryRepo categoryRepo;
  
    
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
	    
	    notices.forEach(notice -> {
	        notice.setIsRead(true);
	        notice.setReadDate(LocalDateTime.now()); // Optionally set read date
	        this.noticeRepo.save(notice); // Save updated notice back to the database
	    });
	    
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

	 // Set isRead to true for each notice fetched by faculty
	    notics.forEach(notice -> {
	        notice.setIsRead(true);
	        notice.setReadDate(LocalDateTime.now()); // Optionally set read date
	        this.noticeRepo.save(notice); // Save updated notice back to the database
	    });
	    //	    // Convert exams to ExamDto
//	    List<NoticeDto> noticeDtos = notics.stream()
//	                                  .map(notice -> this.modelMapper.map(notice, NoticeDto.class))
//	                                  .collect(Collectors.toList());

	    
	    return notics.stream()
                .map(this::noticeToDto)
                .collect(Collectors.toList());
	}
	  @Override
	    public boolean hasUserSeenNotice(Integer userId, Long noticeId) {
	        // Verify the existence of the user and notice
	        User user = userRepo.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

	        Notice notice = noticeRepo.findById(noticeId)
	            .orElseThrow(() -> new ResourceNotFoundException("Notice", "noticeId", noticeId));

	        // Check if the notice is associated with this user and if it has been read
	        // Check if the notice is associated with this user and if it has been read
	        return user.getId() == userId && Boolean.TRUE.equals(notice.getIsRead());

	    }

}
