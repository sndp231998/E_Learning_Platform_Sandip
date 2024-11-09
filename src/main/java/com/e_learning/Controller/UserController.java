package com.e_learning.Controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.io.FilenameUtils;
import org.hibernate.engine.jdbc.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.e_learning.payloads.ApiResponse;

import com.e_learning.payloads.UserDto;
import com.e_learning.payloads.UserFacultyDto;
import com.e_learning.services.FileService;
import com.e_learning.services.UserService;
import com.e_learning.services.impl.RateLimitingService;
import com.e_learning.services.impl.UserServiceImpl;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
	 private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	@Autowired
	private UserService userService;
	
	@Autowired
	private FileService fileService;

	@Value("${project.image}")
	private String path;
	 @Autowired
	    private RateLimitingService rateLimitingService;

	 
	 
	 @PutMapping("/{userId}/start-trial")
	    public ResponseEntity<UserDto> startTrialForNewUser(@PathVariable Integer userId) {
	        UserDto updatedUser = this.userService.startTrialForNewUser(userId);
	        return ResponseEntity.ok(updatedUser);
	    }
	
	 
	// POST-create user
	 @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
	@PostMapping("/")
	public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
		UserDto createUserDto = this.userService.createUser(userDto);
		return new ResponseEntity<>(createUserDto, HttpStatus.CREATED);
	}

	// PUT- update user
	@PreAuthorize("hasRole('ADMIN') or hasRole('NORMAL')")
	@PutMapping("/{userId}")
	public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserDto userDto, @PathVariable("userId") Integer uid) {
		UserDto updatedUser = this.userService.updateUser(userDto, uid);
		return ResponseEntity.ok(updatedUser);
	}

	//ADMIN
	// DELETE -delete user
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{userId}")
	public ResponseEntity<ApiResponse> deleteUser(@PathVariable("userId") Integer uid) {
		this.userService.deleteUser(uid);
		return new ResponseEntity<ApiResponse>(new ApiResponse("User deleted Successfully", true), HttpStatus.OK);
	}
	
	 @PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("{userId}/{facultyName}")
    public ResponseEntity<ApiResponse> deleteFaculty(@PathVariable Integer userId, @PathVariable String facultyName) {
      this.userService.deleteFaculty(userId, facultyName);
      //  return ResponseEntity.noContent().build(); // Return 204 No Content on successful deletion
      return new ResponseEntity<ApiResponse>(new ApiResponse("Faculty delete successfull",true),HttpStatus.OK);

  	
	}
	
	
	
	
	
	// GET - user get
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/")
	public ResponseEntity<List<UserDto>> getAllUsers() {
		 rateLimitingService.checkRateLimit("test-api-key");
		return ResponseEntity.ok(this.userService.getAllUsers());
	}

	// GET - user get
	@GetMapping("/{userId}")
	public ResponseEntity<UserDto> getSingleUser(@PathVariable Integer userId) {
		return ResponseEntity.ok(this.userService.getUserById(userId));
	}
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/college/{collegename}")
    public ResponseEntity<List<UserDto>> getUsersByCollegeName(@PathVariable String collegename) {
        List<UserDto> users = userService.getUsersByCollegeName(collegename);
        return ResponseEntity.ok(users);
    }
	
	//-----------------ROles change----------------
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/addRole/email/{email}/role/{roleName}")
	public ResponseEntity<ApiResponse> addRoleToUser(@PathVariable String email, @PathVariable String roleName) {
	    	    userService.addRoleToUser(email, roleName);
	    ApiResponse response = new ApiResponse("Role added successfully", true);
	    return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email) {
        UserDto user = userService.getUserByEmail(email);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
	
	@PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/role/{roleName}")
    public ResponseEntity<List<UserDto>> getUsersByRole(@PathVariable String roleName) {
        List<UserDto> users = userService.getUsersByRole(roleName);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
//------Faculty add and update ----
	 // only Add new without  faculty for a user
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{userId}/faculty")
	public ResponseEntity<Map<String, Object>> updateUserFaculty(@PathVariable Integer userId, @RequestBody UserDto userDto) {
	    UserDto updatedUser = userService.updateFacult(userDto, userId);

	    Map<String, Object> response = new HashMap<>();
	    response.put("updatedUser", updatedUser);
	    
	    List<String> alreadyExistingFaculties = userDto.getFacult().stream()
	            .filter(faculty -> updatedUser.getFacult().contains(faculty))
	            .collect(Collectors.toList());
	    if (!alreadyExistingFaculties.isEmpty()) {
	        response.put("message", "These faculties already exist: " + alreadyExistingFaculties);
	    }
	   return ResponseEntity.ok(response);
	}


	
	//@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{userId}/discount")
	public ResponseEntity<UserDto> updateDiscount(@Valid @RequestBody UserDto userDto, @PathVariable("userId") Integer uid) {
		UserDto updatedDiscount = this.userService.updateDiscount(userDto, uid);
		String a=userDto.getDiscount();
		logger.info("discount form controller ..........................."+a);
		return ResponseEntity.ok(updatedDiscount);
	}
	

	
	//-------------Image upload-------------------
	// Post method for file upload
    @PostMapping("/file/upload/{userId}")
    public ResponseEntity<UserDto> uploadUserFile(@RequestParam("file") MultipartFile file,
                                                  @PathVariable Integer userId) throws IOException {
        UserDto userDto = this.userService.getUserById(userId);
        String fileName = this.fileService.uploadFile(path, file);
        userDto.setImageName(fileName);// Assuming you want to set the uploaded file name as imageName
        UserDto updateduser = this.userService.updateUser(userDto, userId);
        return new ResponseEntity<>(updateduser, HttpStatus.OK);
    }
    
    //method to serve files
    @GetMapping(value = "/image/{imageName}",produces = MediaType.IMAGE_JPEG_VALUE)
    public void downloadImage(
            @PathVariable("imageName") String imageName,
            HttpServletResponse response
    ) throws IOException {
    	String fileExtension = FilenameUtils.getExtension(imageName).toLowerCase();
        MediaType mediaType = MediaType.IMAGE_JPEG;  // Default

        if (fileExtension.equals("png")) {
            mediaType = MediaType.IMAGE_PNG;
        } else if (fileExtension.equals("jpg") || fileExtension.equals("jpeg")) {
            mediaType = MediaType.IMAGE_JPEG;
        }

        response.setContentType(mediaType.toString());
        try (InputStream resource = this.fileService.getResource(path, imageName)) {
            StreamUtils.copy(resource, response.getOutputStream());
        }
    }


    @GetMapping("/{userId}/faculties")
    public ResponseEntity<List<String>> getFacultiesByUserId(@PathVariable int userId) {
        List<String> faculties = userService.getFacultiesByUserId(userId);
        return ResponseEntity.ok(faculties);
    }
    
    @GetMapping("/recent-7-days")
    public ResponseEntity<?> getUsersJoinedInLast7Days() {
        List<UserDto> users = userService.getUsersJoinedInLast7Days();
        
        if (users.isEmpty()) {
            return ResponseEntity.ok("No users joined in the last seven days.");
        }
        
        return ResponseEntity.ok(users);
    }

    @GetMapping("/teachers-and-subscribers")
    public ResponseEntity<List<UserFacultyDto>> getTeachersAndSubscribers() {
        List<UserFacultyDto> users = userService.getUsersWithTeacherOrSubscribedRoles();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
    

