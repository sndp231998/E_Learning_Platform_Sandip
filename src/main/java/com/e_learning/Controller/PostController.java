package com.e_learning.Controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpHeaders;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.hibernate.engine.jdbc.StreamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

import com.e_learning.config.AppConstants;
import com.e_learning.entities.User;
import com.e_learning.payloads.ApiResponse;
import com.e_learning.payloads.ExamDto;
import com.e_learning.payloads.PostDto;
import com.e_learning.payloads.PostResponse;
import com.e_learning.services.FileService;
import com.e_learning.services.PostService;



@RestController
@RequestMapping("/api/v1/")
public class PostController {

	@Autowired
	private PostService postService;

	@Autowired
	private FileService fileService;

	@Value("${project.image}")
	private String path;
	
//	create
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/user/{userId}/category/{categoryId}/posts")
	public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto, @PathVariable Integer userId,
			@PathVariable Integer categoryId) {
		
		// Extract the user ID from the authentication context
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User userDetails = (User) authentication.getPrincipal();
	    Integer tokenUserId = userDetails.getId(); // Get the user ID from the token


	    // Compare the user ID from the token with the user ID from the path variable
	    if (!tokenUserId.equals(userId)) {
	        return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
	    }
		PostDto createPost = this.postService.createPost(postDto, userId, categoryId);
		return new ResponseEntity<PostDto>(createPost, HttpStatus.CREATED);
	}

	// get by user

	@GetMapping("/user/{userId}/posts")
	public ResponseEntity<List<PostDto>> getPostsByUser(@PathVariable Integer userId) {

		List<PostDto> posts = this.postService.getPostsByUser(userId);
		return new ResponseEntity<List<PostDto>>(posts, HttpStatus.OK);

	}

	// get by category
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/category/{categoryId}/posts")
	public ResponseEntity<List<PostDto>> getPostsByCategory(@PathVariable Integer categoryId) {

		List<PostDto> posts = this.postService.getPostsByCategory(categoryId);
		return new ResponseEntity<List<PostDto>>(posts, HttpStatus.OK);

	}
////	get Posts By userFaculty
//	@PreAuthorize("hasRole('ADMIN') or hasRole('SUBSCRIBED')")
//	 @GetMapping("posts/user/{userId}")
//	    public ResponseEntity<List<PostDto>> getPostsByUserFaculty(@PathVariable Integer userId) {
//	        List<PostDto> posts = this.postService.getPostsByUserFaculty(userId);
//	        return new ResponseEntity<>(posts, HttpStatus.OK);
//	    }
	
//	get Posts By userFaculty
	@PreAuthorize("hasRole('ADMIN') or hasRole('SUBSCRIBED')")
	@GetMapping("postss/user/{userId}/faculty/{faculty}")
	public ResponseEntity<List<PostDto>> getPostssByUserFaculty(@PathVariable Integer userId, @PathVariable String faculty) {
	    List<PostDto> posts = this.postService.getPostssByUserFacult(userId, faculty);
	    return new ResponseEntity<>(posts, HttpStatus.OK);
	}

//	@PreAuthorize("hasRole('ADMIN') or hasRole('SUBSCRIBED')")
//	 @GetMapping("postss/user/{userId}")
//	    public ResponseEntity<List<PostDto>> getPostssByUserFacult(@PathVariable Integer userId) {
//	        List<PostDto> posts = this.postService.getPostsByUserFaculty(userId);
//	        return new ResponseEntity<>(posts, HttpStatus.OK);
//	    }
	
	// get all posts
	//@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/posts")
	public ResponseEntity<PostResponse> getAllPost(
			@RequestParam(value = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
			@RequestParam(value = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
			@RequestParam(value = "sortBy", defaultValue = AppConstants.SORT_BY, required = false) String sortBy,
			@RequestParam(value = "sortDir", defaultValue = AppConstants.SORT_DIR, required = false) String sortDir) {

		PostResponse postResponse = this.postService.getAllPost(pageNumber, pageSize, sortBy, sortDir);
		return new ResponseEntity<PostResponse>(postResponse, HttpStatus.OK);
	}

	// get post details by id
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/posts/{postId}")
	public ResponseEntity<PostDto> getPostById(@PathVariable Integer postId) {

		PostDto postDto = this.postService.getPostById(postId);
		return new ResponseEntity<PostDto>(postDto, HttpStatus.OK);

	}

	// delete post
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/posts/{postId}")
	public ApiResponse deletePost(@PathVariable Integer postId) {
		this.postService.deletePost(postId);
		return new ApiResponse("Post is successfully deleted !!", true);
	}

	// update post
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/posts/{postId}")
	public ResponseEntity<PostDto> updatePost(@RequestBody PostDto postDto, @PathVariable Integer postId) {

		PostDto updatePost = this.postService.updatePost(postDto, postId);
		return new ResponseEntity<PostDto>(updatePost, HttpStatus.OK);

	}

	// search
	@GetMapping("/posts/search/{keywords}")
	public ResponseEntity<List<PostDto>> searchPostByTitle(@PathVariable("keywords") String keywords) {
		List<PostDto> result = this.postService.searchPosts(keywords);
		return new ResponseEntity<List<PostDto>>(result, HttpStatus.OK);
	}

	// post image upload
	//@PreAuthorize("hasRole('ADMIN')")
//	@PostMapping("/post/file/upload/{postId}")
//	public ResponseEntity<PostDto> uploadPostImage(@RequestParam("image") MultipartFile image,
//			@PathVariable Integer postId) throws IOException {
//
//		PostDto postDto = this.postService.getPostById(postId);
//		
//		String fileName = this.fileService.uploadImage(path, image);
//		postDto.setImageName(fileName);
//		PostDto updatePost = this.postService.updatePost(postDto, postId);
//		return new ResponseEntity<PostDto>(updatePost, HttpStatus.OK);
//
//	}
//	
	// Updated upload method for multiple file types
	@PostMapping("/post/file/upload/{postId}")
	public ResponseEntity<PostDto> uploadExamFile(@RequestParam("file") MultipartFile file,
	                                              @PathVariable Integer postId) throws IOException {
	    // Get the file extension in lowercase
	    String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();

	    // Allowable file types
	    if (!fileExtension.equals("pdf") && !fileExtension.equals("jpeg") && !fileExtension.equals("jpg")
	            && !fileExtension.equals("png") && !fileExtension.equals("pptx")) {
	        return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
	    }

	    // Continue with file upload
	    PostDto postDto = this.postService.getPostById(postId);
	    String fileName = this.fileService.uploadFile(path, file);
	    postDto.setImageName(fileName);  // Assuming imageName is used for storing any file type name
	    PostDto updatedPost = this.postService.updatePost(postDto, postId);
	    return new ResponseEntity<>(updatedPost, HttpStatus.OK);
	}

	// Method to serve files of various types
	@GetMapping(value = "/post/image/{fileName}")
	public void downloadFile(
	        @PathVariable("fileName") String fileName,
	        HttpServletResponse response
	) throws IOException {
	    // Determine the file extension to set content type
	    String fileExtension = FilenameUtils.getExtension(fileName).toLowerCase();
	    MediaType mediaType;

	    switch (fileExtension) {
	        case "png":
	            mediaType = MediaType.IMAGE_PNG;
	            break;
	        case "jpg":
	        case "jpeg":
	            mediaType = MediaType.IMAGE_JPEG;
	            break;
	        case "pdf":
	            mediaType = MediaType.APPLICATION_PDF;
	            break;
	        case "pptx":
	            mediaType = MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.presentationml.presentation");
	            break;
	        default:
	            mediaType = MediaType.APPLICATION_OCTET_STREAM;
	    }

	    // Set the content type
	    response.setContentType(mediaType.toString());
	    
	    // Set the Content-Disposition header manually
	    response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

	    // Serve the file
	    try (InputStream resource = this.fileService.getResource(path, fileName)) {
	        StreamUtils.copy(resource, response.getOutputStream());
	    }
	}

//    }
    //Get Posts by category id
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<PostDto>> getPostsByCategoryId(@PathVariable Integer categoryId) {
        List<PostDto> posts = postService.getPostsByCategoryId(categoryId);
        return ResponseEntity.ok(posts);
    }
    
    
}
