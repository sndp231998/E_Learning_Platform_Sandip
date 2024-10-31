package com.e_learning.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.e_learning.entities.User;

import com.e_learning.payloads.NoticeDto;

import com.e_learning.services.FileService;
import com.e_learning.services.NoticeService;

@RestController
@RequestMapping("/api/v1/")
public class NoticeController {
	
	@Autowired
	private NoticeService noticeService;

	@Autowired
	private FileService fileService;

	@Value("${project.image}")
	private String path;
	
	// Create a notice for a specific user and category (FOR_SUBSCRIBER)
	@PostMapping("/user/{userId}/category/{categoryId}/notices")
	public ResponseEntity<NoticeDto> createNotice(@RequestBody NoticeDto noticeDto, @PathVariable Integer userId,
			@PathVariable Integer categoryId) {
		 // Extract the user ID from the authentication context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userDetails = (User) authentication.getPrincipal();
        Integer tokenUserId = userDetails.getId(); // Get the user ID from the token

        // Compare the user ID from the token with the user ID from the path variable
        if (!tokenUserId.equals(userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
        }
        
        NoticeDto createNotice = this.noticeService.createNotice(noticeDto, userId, categoryId);
		
		return new ResponseEntity<NoticeDto>(createNotice, HttpStatus.CREATED);
	}
	
	  // Create a notice for all users (FOR_ALL)
	@PostMapping("/user/{userId}/notices")
	public ResponseEntity<NoticeDto> createNotice(@RequestBody NoticeDto noticeDto, @PathVariable Integer userId) {
		 // Extract the user ID from the authentication context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User userDetails = (User) authentication.getPrincipal();
        Integer tokenUserId = userDetails.getId(); // Get the user ID from the token

        // Compare the user ID from the token with the user ID from the path variable
        if (!tokenUserId.equals(userId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
        }
        
        NoticeDto createNotice = this.noticeService.createNotice(noticeDto, userId);
		
		return new ResponseEntity<NoticeDto>(createNotice, HttpStatus.CREATED);
	}
	 // Get all notices with FOR_ALL type
//	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/notices")
	public ResponseEntity<List<NoticeDto>> getAllNotices() {
		
		return ResponseEntity.ok(this.noticeService.getAllNotices());
	}
	 
	
	//get notices By userFaculty
		@PreAuthorize("hasRole('ADMIN') or hasRole('SUBSCRIBED')")
		@GetMapping("notices/user/{userId}/faculty/{faculty}")
		public ResponseEntity<List<NoticeDto>> getNoticesByUserFaculty(@PathVariable Integer userId, @PathVariable String faculty) {
		    List<NoticeDto> notices = this.noticeService.getNoticsByUserFaculty(userId, faculty);
		    		
		    return new ResponseEntity<>(notices, HttpStatus.OK);
		}
		 // Endpoint to check if a user has seen a specific notice
		@GetMapping("/user/{userId}/notice/{noticeId}/seen")
	    public ResponseEntity<Boolean> hasUserSeenNotice(
	            @PathVariable Integer userId,
	            @PathVariable Long noticeId) {
	        boolean hasSeen = noticeService.hasUserSeenNotice(userId, noticeId);
	        return ResponseEntity.ok(hasSeen);
	    }
	

}
