package com.e_learning.services.impl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_learning.config.AppConstants;
import com.e_learning.entities.Category;
import com.e_learning.entities.Exam;
import com.e_learning.entities.LiveStreaming;
import com.e_learning.entities.Post;
import com.e_learning.entities.Role;
import com.e_learning.entities.User;
import com.e_learning.exceptions.ApiException;
import com.e_learning.exceptions.ResourceNotFoundException;
import com.e_learning.payloads.LiveStreamingDto;
import com.e_learning.payloads.PostDto;
import com.e_learning.payloads.UserDto;
import com.e_learning.repositories.CategoryRepo;
import com.e_learning.repositories.LiveStreamingRepo;
import com.e_learning.repositories.RoleRepo;
import com.e_learning.repositories.UserRepo;
import com.e_learning.services.LiveStreamingService;
import com.e_learning.services.NotificationService;

@Service
public class LiveStreamingServiceImpl implements LiveStreamingService {

	private static final Logger logger = LoggerFactory.getLogger(LiveStreamingServiceImpl.class);

    @Autowired
    private LiveStreamingRepo liveRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CategoryRepo categoryRepo;
    
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private NotificationService notificationService;
    

    @Override
    public LiveStreamingDto createLiveStreaming(LiveStreamingDto liveDto, Integer userId, Integer categoryId) {
    	logger.info("Creating live streaming for user ID: {} and category ID: {}", userId, categoryId);
    	User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "User id", userId));

    	 List<String> faculties = userRepo.findFacultiesByUserId(userId);
    	 
    	 
    	 Set<Role> userRoles = user.getRoles(); // Using Set<Role> for role comparison

	        // Define the roles for comparison
	        Role teacherRole = this.roleRepo.findById(AppConstants.TEACHER_USER)
	                .orElseThrow(() -> new ResourceNotFoundException("Role", "Role id", AppConstants.TEACHER_USER));
	        
	        Role adminRole = this.roleRepo.findById(AppConstants.ADMIN_USER)
	                .orElseThrow(() -> new ResourceNotFoundException("Role", "Role id", AppConstants.ADMIN_USER));
	        
	        Role normalRole = this.roleRepo.findById(AppConstants.NORMAL_USER)
	                .orElseThrow(() -> new ResourceNotFoundException("Role", "Role id", AppConstants.NORMAL_USER));
	        
	        Role subscribeRole = this.roleRepo.findById(AppConstants.SUBSCRIBED_USER)
	                .orElseThrow(() -> new ResourceNotFoundException("Role", "Role id", AppConstants.SUBSCRIBED_USER));

        Category category = this.categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "category id", categoryId));

        // Role-based permissions
        if (userRoles.contains(normalRole) || userRoles.contains(subscribeRole)) {
            throw new ApiException("Only teachers and admins are allowed to create Live Class.");
        } 
        
        // Check if the user is a teacher and needs to match category title with faculty
        if (userRoles.contains(teacherRole)) {
            String normalizedCategoryTitle = category.getCategoryTitle().trim().toLowerCase();
            
            // Check if faculties contain the normalized category title
            boolean hasPermission = faculties.stream()
                    .map(faculty -> faculty.trim().toLowerCase())
                    .anyMatch(faculty -> faculty.equals(normalizedCategoryTitle));
            
            if (!hasPermission) {
                throw new ApiException("You do not have permission to create Live Class in this category.");
            }
        }
        
        // If the user is an admin, allow without further checks
        if (userRoles.contains(adminRole)) {
            // Admin has permission, so no further checks needed
        } else if (!userRoles.contains(teacherRole)) {
            // Restrict access if the role is neither Admin nor Teacher
            throw new ApiException("You do not have permission to create Live Class.");
        }
        
        LiveStreaming live = this.modelMapper.map(liveDto, LiveStreaming.class);
        live.setTitle(liveDto.getTitle());
        live.setStartingTime(liveDto.getStartingTime());
        live.setStreamlink(liveDto.getStreamlink());
        live.setUser(user);
        live.setCategory(category);
        
        LiveStreaming newLive = this.liveRepo.save(live);

        logger.info("Live streaming created successfully with ID: {}", newLive.getLiveId());
        
        notifyUsersAboutLiveStreaming(category.getCategoryTitle(), newLive); 
        
        return this.modelMapper.map(newLive, LiveStreamingDto.class);
    }

 // New method to notify users based on the live streaming category
    private void notifyUsersAboutLiveStreaming(String categoryTitle, LiveStreaming newLive) {
        logger.info("Notifying users about new live streaming in category: {}", categoryTitle);
        
       
        List<User> users = userRepo.findByFaculty(categoryTitle);
        
        if (users.isEmpty()) {
            logger.warn("No users found for category: {}", categoryTitle);
        }
        // Log the users that will be notified
        logger.info("Matched users for category '{}': {}", categoryTitle, 
                     users.stream().map(User::getId).collect(Collectors.toList()));
        
        
        for (User matchedUser : users) {
            String message = String.format(
                    "New live streaming titled '%s' has started in your faculty category '%s'. Join now! Starting time: %s",
                    newLive.getTitle(), 
                    categoryTitle,
                    newLive.getStartingTime()
            );

            notificationService.createNotification(matchedUser.getId(), message);
            logger.info("Notification sent to user ID: {}", matchedUser.getId());
        }
    }

    
    
    
    
    
    
//    @Override
//    public List<LiveStreamingDto> getLiveStreamingsByUserFaculty(Integer userId) {
//        // Retrieve user by ID
//        User user = this.userRepo.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
//
//        // Get the user's faculty
//        String userFaculty = user.getFaculty();
//
//        // Find the category that matches the user's faculty
//        Category category = this.categoryRepo.findByCategoryTitle(userFaculty);
//        if (category == null) {
//            throw new ResourceNotFoundException("Category", "title", userFaculty);
//        }
//        // Fetch live streams associated with the category
//        List<LiveStreaming> lives = this.liveRepo.findByCategory(category);
//
//        // Convert live streams to LiveStreamingDto
//        List<LiveStreamingDto> liveDtos = lives.stream()
//                .map(live -> this.modelMapper.map(live, LiveStreamingDto.class))
//                .collect(Collectors.toList());
//
//        return liveDtos;
//    }
    @Override
    public List<LiveStreamingDto> getLiveStreamingByCategory(Integer categoryId) {
        Category category = this.categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "category id", categoryId));

        List<LiveStreaming> lives = this.liveRepo.findByCategory(category);
 if(lives.isEmpty()) {
	 throw new ResourceNotFoundException("live streaming","category id",categoryId);
 }
        List<LiveStreamingDto> liveDtos = lives.stream()
                .map(live -> this.modelMapper.map(live, LiveStreamingDto.class))
                .collect(Collectors.toList());

        return liveDtos;
    }

    
  
    public LiveStreaming dtoToLiveStreaming(LiveStreamingDto liveStreamingDto) {
        return this.modelMapper.map(liveStreamingDto, LiveStreaming.class);
    }

    public LiveStreamingDto LiveStreamingToDto(LiveStreaming liveStreaming) {
        return this.modelMapper.map(liveStreaming, LiveStreamingDto.class);
    }

    @Override
    public List<LiveStreamingDto> getAllLives() {
        List<LiveStreaming> lives = liveRepo.findAll();
        return lives.stream()
                    .map(this::LiveStreamingToDto) // Ensure this method returns LiveStreamingDto
                    .collect(Collectors.toList());
    }

    @Override
    public LiveStreamingDto updateLiveStreaming(LiveStreamingDto liveDto, Integer liveId) {
        // Check if liveId exists
        LiveStreaming live = this.liveRepo.findById(liveId)
                .orElseThrow(() -> new ResourceNotFoundException("live ", "live id", liveId));
        
        // Set fields from the DTO to the entity
        live.setStartingTime(liveDto.getStartingTime());
        live.setStreamlink(liveDto.getStreamlink());
        live.setTitle(liveDto.getTitle());

        // Check if category is provided before setting it
        if (liveDto.getCategory() != null && liveDto.getCategory().getCategoryId() != null) {
            Category category = this.categoryRepo.findById(liveDto.getCategory().getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", liveDto.getCategory().getCategoryId()));
            live.setCategory(category);
        }

        // Save the updated entity
        LiveStreaming updatedLive = this.liveRepo.save(live);
        return this.modelMapper.map(updatedLive, LiveStreamingDto.class);
    }

	@Override
	public void deleteLiveStreaming(Integer liveId) {
		LiveStreaming live = this.liveRepo.findById(liveId)
                .orElseThrow(() -> new ResourceNotFoundException("Liv ", "live id", liveId));

        this.liveRepo.delete(live);
		
	}
//	@Override
//	public List<PostDto> getPostssByUserFacult(Integer userId, String faculty) {
//	    // Retrieve user by ID

	
	

//

//	    return postDtos;
//	}


	@Override
	public List<LiveStreamingDto> getLiveStreamingByUserFaculty(Integer userId, String faculty) {
	    User user = this.userRepo.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

//// Get the user's faculties (multiple faculties)
List<String> userFacult = user.getFacult();
//
// Check if the provided faculty exists in the user's faculty list
if (!userFacult.contains(faculty)) {
   throw new ResourceNotFoundException("Faculty", "faculty", faculty);
}

//// Find the category that matches the provided faculty
Category category = this.categoryRepo.findByCategoryTitle(faculty);
if (category == null) {
   throw new ResourceNotFoundException("Category", "title", faculty);
}
//// Fetch posts associated with the category
List<LiveStreaming> lives = this.liveRepo.findByCategory(category);
//
//// Convert lives to liveDto
  List<LiveStreamingDto> liveDtos = lives.stream()
                              .map(live -> this.modelMapper.map(live, LiveStreamingDto.class))
                             .collect(Collectors.toList());

		return liveDtos;
	}


}
