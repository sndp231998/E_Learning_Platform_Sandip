package com.e_learning.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_learning.entities.Category;

import com.e_learning.entities.Notice;
import com.e_learning.entities.User;
import com.e_learning.entities.UserNotice;
import com.e_learning.exceptions.ResourceNotFoundException;

import com.e_learning.payloads.NoticeDto;

import com.e_learning.repositories.CategoryRepo;
import com.e_learning.repositories.NoticeRepo;
import com.e_learning.repositories.UserNoticeRepository;
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
    private UserNoticeRepository userNoticeRepo;
   
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
	  //  notice.setIsRead(noticeDto.getIsRead());
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
	public List<NoticeDto> getAllNotices(Integer userId) {
	    List<Notice> notices = this.noticeRepo.findByNoticeType(Notice.NoticeType.FOR_ALL);

	    notices.forEach(notice -> {
	        markNoticeAsRead(userId, notice.getNoticeId());
	        notice.setReadDate(LocalDateTime.now()); // Optionally set read date
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
	        markNoticeAsRead(userId, notice.getNoticeId());
	        notice.setReadDate(LocalDateTime.now());
	    });

	    return notics.stream()
	            .map(this::noticeToDto)
	            .collect(Collectors.toList());
	}
	
	
	public void markNoticeAsRead(Integer userId, Long noticeId) {
	    // Find the user and notice entities
	    User user = this.userRepo.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User", "User id", userId));

	    Notice notice = this.noticeRepo.findById(noticeId)
	            .orElseThrow(() -> new ResourceNotFoundException("Notice", "Notice id", noticeId));

	    // Check if an entry already exists for this user and notice
	    boolean alreadyRead = userNoticeRepo.existsByUserAndNotice(user, notice);
	    if (alreadyRead) {
	        throw new IllegalStateException("Notice already read by the user");
	    }

	    // Create a new UserNotice entry
	    UserNotice userNotice = new UserNotice();
	    userNotice.setUser(user);
	    userNotice.setNotice(notice);
	    userNotice.setRead(true);
	    
	    // Save to UserNotice repository
	    userNoticeRepo.save(userNotice);
	}

	
	

	// Method to mark a specific notice as read by a specific user
    public Notice makeNoticeAsRead(Integer userId, Long noticeId) {
        // Find the user and notice entities
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        
        Notice notice = noticeRepo.findById(noticeId)
                .orElseThrow(() -> new ResourceNotFoundException("Notice", "noticeId", noticeId));
        
        // Find or create a UserNotice record for this user and notice
        UserNotice userNotice = userNoticeRepo.findByUser_IdAndNotice_NoticeId(userId, noticeId)
                .orElse(new UserNotice());

        // Set the user, notice, and mark as read
        userNotice.setUser(user);
        userNotice.setNotice(notice);
        userNotice.setRead(true); // Mark as read
        userNoticeRepo.save(userNotice); // Save to the database

        return notice; // Return the Notice object
    }
	
	public boolean isNoticeReadByUser(Integer userId, Long noticeId) {
      
		Optional<UserNotice> userNotice = userNoticeRepo.findByUser_IdAndNotice_NoticeId(userId, noticeId);
       
		return userNotice.map(UserNotice::isRead).orElse(false);
    }
	
	//----------------count-----------------
//	@Override
//	public int countUnreadNoticesByUserId(Integer userId, Notice.NoticeType Type) {
//	    // Example implementation, adjust to your logic
//	    return noticeRepo.countUnreadByUserIdAndType(userId, Type);
//	}

}


