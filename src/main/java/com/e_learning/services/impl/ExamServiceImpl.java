package com.e_learning.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import com.e_learning.config.AppConstants;
import com.e_learning.entities.Category;
import com.e_learning.entities.Exam;
import com.e_learning.entities.LiveStreaming;
import com.e_learning.entities.Post;
import com.e_learning.entities.Role;
import com.e_learning.entities.Exam.ExamType;
import com.e_learning.entities.User;
import com.e_learning.exceptions.ApiException;
import com.e_learning.exceptions.ResourceNotFoundException;

import com.e_learning.payloads.ExamDto;
import com.e_learning.payloads.PostDto;
import com.e_learning.repositories.CategoryRepo;
import com.e_learning.repositories.ExamRepo;
import com.e_learning.repositories.RoleRepo;
import com.e_learning.repositories.UserRepo;
import com.e_learning.services.ExamService;
import com.e_learning.services.NotificationService;

import io.lettuce.core.dynamic.annotation.Param;
@Service
public class ExamServiceImpl implements ExamService{
	
	 private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	 
	 @Autowired
	    private ExamRepo examRepo;

	    @Autowired
	    private ModelMapper modelMapper;

	    @Autowired
	    private UserRepo userRepo;

	    @Autowired
	    private RoleRepo roleRepo;
	    @Autowired
	    private CategoryRepo categoryRepo;
	      
	    @Autowired
	    private NotificationService notificationService;
	     
	    @Override 
	    public ExamDto createExam(ExamDto examDto, Integer userId, Integer categoryId) {
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
	        
	        // Logging user faculty and category title
	        logger.info("User Faculty: " + faculties);
	        logger.info("Category Title: " + category.getCategoryTitle());

	        // Role-based permissions
	        if (userRoles.contains(normalRole) || userRoles.contains(subscribeRole)) {
	            throw new ApiException("Only teachers and admins are allowed to create exams.");
	        } 
	        
	        // Check if the user is a teacher and needs to match category title with faculty
	        if (userRoles.contains(teacherRole)) {
	            String normalizedCategoryTitle = category.getCategoryTitle().trim().toLowerCase();
	            
	            // Check if faculties contain the normalized category title
	            boolean hasPermission = faculties.stream()
	                    .map(faculty -> faculty.trim().toLowerCase())
	                    .anyMatch(faculty -> faculty.equals(normalizedCategoryTitle));
	            
	            if (!hasPermission) {
	                throw new ApiException("You do not have permission to create exams in this category.");
	            }
	        }
	        
	        // If the user is an admin, allow without further checks
	        if (userRoles.contains(adminRole)) {
	            // Admin has permission, so no further checks needed
	        } else if (!userRoles.contains(teacherRole)) {
	            // Restrict access if the role is neither Admin nor Teacher
	            throw new ApiException("You do not have permission to create exams.");
	        }
	        
	        // Proceed with exam creation
	        Exam exam = this.modelMapper.map(examDto, Exam.class);
	        exam.setImageName("");
	        exam.setAddedDate(LocalDateTime.now());
	        exam.setUser(user);
	        exam.setCategory(category);

	        // Check examType and set the appropriate fields
	        if (examDto.getExamType() == ExamType.ASSIGNMENT) {
	            if (examDto.getDeadline() == null) {
	                throw new IllegalArgumentException("Deadline is required for Assignment Exam type");
	            }
	            exam.setDeadline(examDto.getDeadline());
	            exam.setStartTime(null);
	            exam.setEndTime(null);
	        } else if (examDto.getExamType() == ExamType.EXAM || examDto.getExamType() == ExamType.TEST) {
	            if (examDto.getStartTime() == null || examDto.getEndTime() == null) {
	                throw new IllegalArgumentException("Starting time and end time are required for EXAM/TEST exam type");
	            }
	            exam.setStartTime(examDto.getStartTime());
	            exam.setEndTime(examDto.getEndTime());
	            exam.setDeadline(null);
	        } else {
	            throw new IllegalArgumentException("Invalid exam type. It must be either ASSIGNMENT, EXAM, or TEST.");
	        }

	        Exam newExam = this.examRepo.save(exam);

	        // Notify users about the new exam
	        notifyUsersAboutExam(category.getCategoryTitle(), newExam);
	        return this.modelMapper.map(newExam, ExamDto.class);
	    }

	    
	    
	    

	    private void notifyUsersAboutExam(String categoryTitle, Exam newExam) {
	        List<User> users = userRepo.findByFaculty(categoryTitle);

	        for (User matchedUser : users) {
	            String message;
	            if (newExam.getExamType() == ExamType.ASSIGNMENT) {
	                message = String.format(
	                        "New Assignment titled '%s' has started in your faculty category '%s'. Join now! Deadline: %s",
	                        newExam.getTitle(),
	                        categoryTitle,
	                        newExam.getDeadline()
	                );
	            } else {
	                message = String.format(
	                        "New titled '%s' has started in your faculty category '%s'. Join now! Starting Time: '%s' and Ending Time: %s",
	                        newExam.getTitle(),
	                        categoryTitle,
	                        newExam.getStartTime(),
	                        newExam.getEndTime()
	                );
	            }
	            notificationService.createNotification(matchedUser.getId(), message);
	        }
	    }
	
	    
	
	@Override
	public ExamDto updateExam(ExamDto examDto, Integer examId) {
	    Exam exam = this.examRepo.findById(examId)
	            .orElseThrow(() -> new ResourceNotFoundException("Exam", "exam id", examId));

	    // Check if the category exists in the examDto
	    if (examDto.getCategory() != null && examDto.getCategory().getCategoryId() != null) {
	        // Fetch category if it is present
	        Category category = this.categoryRepo.findById(examDto.getCategory().getCategoryId())
	                .orElseThrow(() -> new ResourceNotFoundException("Category", "category id", examDto.getCategory().getCategoryId()));

	        exam.setCategory(category); // Set the category if found
	    }

	    // Update other fields of the exam
	    exam.setTitle(examDto.getTitle());
	    exam.setDeadline(examDto.getDeadline());
	    exam.setImageName(examDto.getImageName());

	    // Save the updated exam
	    Exam updatedExam = this.examRepo.save(exam);
	    return this.modelMapper.map(updatedExam, ExamDto.class);
	}




	@Override
	public void deleteExam(Integer examId) {
		Exam exam = this.examRepo.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam ", "exam id", examId));

        this.examRepo.delete(exam);
		
	}

	

	@Override
	public ExamDto getExamById(Integer examId) {
		 Exam exam = this.examRepo.findById(examId)
	                .orElseThrow(() -> new ResourceNotFoundException("exam", "exam id", examId));
	        return this.modelMapper.map(exam, ExamDto.class);
	}

	@Override
	public List<ExamDto> getExamsByCategory(Integer categoryId) {
		 Category cat = this.categoryRepo.findById(categoryId)
	                .orElseThrow(() -> new ResourceNotFoundException("Category", "category id", categoryId));
	        List<Exam> exams = this.examRepo.findByCategory(cat);
	        		
	        // If no exams are found, you can handle it here
	        if (exams.isEmpty()) {
	            throw new ResourceNotFoundException("No exams", "category id", categoryId);
	        }

	        List<ExamDto> examDtos = exams.stream().map((exam) -> this.modelMapper.map(exam, ExamDto.class))
	                .collect(Collectors.toList());

	        return examDtos;
	}

	@Override
	public List<ExamDto> getExamsByUser(Integer userId) {
		User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User ", "userId ", userId));
        List<Exam> exams = this.examRepo.findByUser(user);

        List<ExamDto> examDtos = exams.stream().map((exam) -> this.modelMapper.map(exam, ExamDto.class))
                .collect(Collectors.toList());

        return examDtos;
	}


	@Override
	public List<ExamDto> searchExams(String keyword) {
		 List<Exam> exams = this.examRepo.searchByTitle("%" + keyword + "%");
	        List<ExamDto> examDtos = exams.stream().map((exam) -> this.modelMapper.map(exam, ExamDto.class)).collect(Collectors.toList());
	        return examDtos;
	}

	//get all
	@Override
	public List<ExamDto> getExams() {
		List<Exam> ex = this.examRepo.findAll();
		List<ExamDto> examDtos = ex.stream().map((exa) -> this.modelMapper.map(exa, ExamDto.class))
				.collect(Collectors.toList());

		return examDtos;
	}



	@Override
	public List<ExamDto> getExamsByUserFaculty(Integer userId, String faculty) {
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
	    // Fetch posts associated with the category
	    List<Exam> exams = this.examRepo.findByCategory(category);
//	    // Convert exams to ExamDto
	    List<ExamDto> examDtos = exams.stream()
	                                  .map(exam -> this.modelMapper.map(exam, ExamDto.class))
	                                  .collect(Collectors.toList());
//
	    return examDtos;
	}

	
	
	
}
