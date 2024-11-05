package com.e_learning.services;

import java.util.List;

import com.e_learning.payloads.ApiResponse;
import com.e_learning.payloads.PostDto;
import com.e_learning.payloads.PostResponse;
import com.e_learning.payloads.UserDto;



public interface PostService {

	//create 

		PostDto createPost(PostDto postDto,Integer userId,Integer categoryId);

		//update 

		PostDto updatePost(PostDto postDto, Integer postId);

		// delete

		void deletePost(Integer postId);
		
		//get all posts
		
		PostResponse getAllPost(Integer pageNumber,Integer pageSize,String sortBy,String sortDir);
		
		//get single post
		
		PostDto getPostById(Integer postId);
		
		
		
		//get all posts by category
		
		List<PostDto> getPostsByCategory(Integer categoryId);
		
		//get all posts by user
		List<PostDto> getPostsByUser(Integer userId);
		
	
		
		//List<PostDto>getPostsByUserFaculty(Integer userId);
		
	//	List<PostDto>getPostssByUserFacult(Integer userId);
		List<PostDto>getPostssByUserFacult(Integer userId, String faculty) ;
		//search posts
		List<PostDto> searchPosts(String keyword);
		
		List<PostDto> getPostsByCategoryId(Integer categoryId);
		
		
		
}
