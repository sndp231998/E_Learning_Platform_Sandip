package com.e_learning.services.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.modelmapper.ModelMapper;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.e_learning.config.AppConstants;
import com.e_learning.entities.Category;
import com.e_learning.entities.Post;
import com.e_learning.entities.Role;
import com.e_learning.entities.User;
import com.e_learning.exceptions.ApiException;
import com.e_learning.exceptions.ResourceNotFoundException;
import com.e_learning.payloads.ApiResponse;
import com.e_learning.payloads.PostDto;
import com.e_learning.payloads.PostResponse;
import com.e_learning.repositories.CategoryRepo;
import com.e_learning.repositories.PostRepo;
import com.e_learning.repositories.RoleRepo;
import com.e_learning.repositories.UserRepo;
import com.e_learning.services.PostService;
import com.e_learning.services.UserService;

import ch.qos.logback.classic.Logger;



@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private CategoryRepo categoryRepo;
      
    org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public PostDto createPost(PostDto postDto, Integer userId, Integer categoryId) {

        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User ", "User id", userId));

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
                .orElseThrow(() -> new ResourceNotFoundException("Category", "category id ", categoryId));

        // Role-based permissions
        if (userRoles.contains(normalRole) || userRoles.contains(subscribeRole)) {
            throw new ApiException("Only teachers and admins are allowed to create.");
        } 
        
        // Check if the user is a teacher and needs to match category title with faculty
        if (userRoles.contains(teacherRole)) {
            String normalizedCategoryTitle = category.getCategoryTitle().trim().toLowerCase();
            
            // Check if faculties contain the normalized category title
            boolean hasPermission = faculties.stream()
                    .map(faculty -> faculty.trim().toLowerCase())
                    .anyMatch(faculty -> faculty.equals(normalizedCategoryTitle));
            
            if (!hasPermission) {
                throw new ApiException("You do not have permission to create in this category.");
            }
        }
        
        // If the user is an admin, allow without further checks
        if (userRoles.contains(adminRole)) {
            // Admin has permission, so no further checks needed
        } else if (!userRoles.contains(teacherRole)) {
            // Restrict access if the role is neither Admin nor Teacher
            throw new ApiException("You do not have permission to creates.");
        }
        
        Post post = this.modelMapper.map(postDto, Post.class);
        post.setImageName("");
        post.setAddedDate(LocalDateTime.now()); 
        post.setMentor(postDto.getMentor());
//        post.setDiscount(postDto.getDiscount());
//        post.setPrice(postDto.getPrice());
        post.setUser(user);
        post.setCategory(category);

        Post newPost = this.postRepo.save(post);

        return this.modelMapper.map(newPost, PostDto.class);
    }

    @Override
    public PostDto updatePost(PostDto postDto, Integer postId) {

        Post post = this.postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post ", "post id", postId));

        Category category = this.categoryRepo.findById(postDto.getCategory().getCategoryId()).get();

        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        post.setImageName(postDto.getImageName());
//        post.setDiscount(postDto.getDiscount());
//        post.setPrice(postDto.getPrice());
        post.setMentor(postDto.getMentor());
        post.setCategory(category);


        Post updatedPost = this.postRepo.save(post);
        return this.modelMapper.map(updatedPost, PostDto.class);
    }

    @Override
    public void deletePost(Integer postId) {

        Post post = this.postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post ", "post id", postId));

        this.postRepo.delete(post);

    }

    @Override
    public PostResponse getAllPost(Integer pageNumber, Integer pageSize, String sortBy, String sortDir) {

        Sort sort = (sortDir.equalsIgnoreCase("asc")) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable p = PageRequest.of(pageNumber, pageSize, sort);

        Page<Post> pagePost = this.postRepo.findAll(p);

        List<Post> allPosts = pagePost.getContent();

        List<PostDto> postDtos = allPosts.stream().map((post) -> this.modelMapper.map(post, PostDto.class))
                .collect(Collectors.toList());

        PostResponse postResponse = new PostResponse();

        postResponse.setContent(postDtos);
        postResponse.setPageNumber(pagePost.getNumber());
        postResponse.setPageSize(pagePost.getSize());
        postResponse.setTotalElements(pagePost.getTotalElements());

        postResponse.setTotalPages(pagePost.getTotalPages());
        postResponse.setLastPage(pagePost.isLast());

        return postResponse;
    }

    @Override
    public PostDto getPostById(Integer postId) {
        Post post = this.postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "post id", postId));
        return this.modelMapper.map(post, PostDto.class);
    }

    @Override
    public List<PostDto> getPostsByCategory(Integer categoryId) {

        Category cat = this.categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "category id", categoryId));
        List<Post> posts = this.postRepo.findByCategory(cat);

        List<PostDto> postDtos = posts.stream().map((post) -> this.modelMapper.map(post, PostDto.class))
                .collect(Collectors.toList());

        return postDtos;
    }

    @Override
    public List<PostDto> getPostsByUser(Integer userId) {

        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User ", "userId ", userId));
        List<Post> posts = this.postRepo.findByUser(user);

        List<PostDto> postDtos = posts.stream().map((post) -> this.modelMapper.map(post, PostDto.class))
                .collect(Collectors.toList());

        return postDtos;
    }

    @Override
    public List<PostDto> searchPosts(String keyword) {
        List<Post> posts = this.postRepo.searchByTitle("%" + keyword + "%");
        List<PostDto> postDtos = posts.stream().map((post) -> this.modelMapper.map(post, PostDto.class)).collect(Collectors.toList());
        return postDtos;
    }


	@Override
	public List<PostDto> getPostssByUserFacult(Integer userId, String faculty) {
	    // Retrieve user by ID
	    User user = this.userRepo.findById(userId)
	            .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
	    
	    // Get the user's faculties (multiple faculties)
	    List<String> userFacult = user.getFacult();
	    
	    // Check if the provided faculty exists in the user's faculty list
	    if (!userFacult.contains(faculty)) {
	        throw new ResourceNotFoundException("Faculty", "faculty", faculty);
	    }

	    // Find the category that matches the provided faculty
	    Category category = this.categoryRepo.findByCategoryTitle(faculty);
	    if (category == null) {
	        throw new ResourceNotFoundException("Category", "title", faculty);
	    }

	    // Fetch posts associated with the category
	    List<Post> posts = this.postRepo.findByCategory(category);
	    
	    // Convert posts to PostDto
	    List<PostDto> postDtos = posts.stream()
	                                  .map(post -> this.modelMapper.map(post, PostDto.class))
	                                  .collect(Collectors.toList());

	    return postDtos;
	}


	
	@Override
    public List<PostDto> getPostsByCategoryId(Integer categoryId) {
        List<Post> posts = postRepo.findByCategoryCategoryId(categoryId);
        return posts.stream().map(post -> modelMapper.map(post, PostDto.class)).collect(Collectors.toList());
    }

	
}
 
    

	

	
	
	