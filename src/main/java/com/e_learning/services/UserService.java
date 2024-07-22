package com.e_learning.services;

import java.util.List;

import com.e_learning.payloads.UserDto;



public interface UserService {
	
UserDto registerNewUser(UserDto user);
	
	
	UserDto createUser(UserDto user);

	UserDto updateUser(UserDto user, Integer userId);

	UserDto getUserById(Integer userId);

	List<UserDto> getAllUsers();

	void deleteUser(Integer userId);
	
	List<UserDto> getUsersByCollegeName(String collegename);  

}
