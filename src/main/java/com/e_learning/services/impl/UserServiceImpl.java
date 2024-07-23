package com.e_learning.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.e_learning.config.AppConstants;
import com.e_learning.entities.Role;
import com.e_learning.entities.User;
import com.e_learning.exceptions.ResourceNotFoundException;
import com.e_learning.payloads.UserDto;
import com.e_learning.repositories.RoleRepo;
import com.e_learning.repositories.UserRepo;
import com.e_learning.services.UserService;
@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleRepo roleRepo;

	@Override
	public UserDto createUser(UserDto userDto) {
		User user = this.dtoToUser(userDto);
		User savedUser = this.userRepo.save(user);
		return this.userToDto(savedUser);
	}

	@Override
	public UserDto updateUser(UserDto userDto, Integer userId) {

		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", " Id ", userId));

		user.setName(userDto.getName());
		user.setEmail(userDto.getEmail());
		user.setPassword(userDto.getPassword());
		user.setCollegename(userDto.getCollegename());

		User updatedUser = this.userRepo.save(user);
		UserDto userDto1 = this.userToDto(updatedUser);
		return userDto1;
	}

	@Override
	public UserDto getUserById(Integer userId) {

		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", " Id ", userId));

		return this.userToDto(user);
	}

	@Override
	public List<UserDto> getAllUsers() {

		List<User> users = this.userRepo.findAll();
		List<UserDto> userDtos = users.stream().map(user -> this.userToDto(user)).collect(Collectors.toList());

		return userDtos;
	}

	@Override
	public void deleteUser(Integer userId) {
		User user = this.userRepo.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));
		this.userRepo.delete(user);

	}

	public User dtoToUser(UserDto userDto) {
		User user = this.modelMapper.map(userDto, User.class);

		// user.setId(userDto.getId());
		// user.setName(userDto.getName());
		// user.setEmail(userDto.getEmail());
		// user.setAbout(userDto.getAbout());
		// user.setPassword(userDto.getPassword());
		return user;
	}

	public UserDto userToDto(User user) {
		UserDto userDto = this.modelMapper.map(user, UserDto.class);
		return userDto;
	}

	@Override
	public UserDto registerNewUser(UserDto userDto) {

		User user = this.modelMapper.map(userDto, User.class);

		// encoded the password
		user.setPassword(this.passwordEncoder.encode(user.getPassword()));

		// roles
		Role role = this.roleRepo.findById(AppConstants.NORMAL_USER).get();

		user.getRoles().add(role);

		User newUser = this.userRepo.save(user);

		return this.modelMapper.map(newUser, UserDto.class);
	}

	@Override
    public List<UserDto> getUsersByCollegeName(String collegename) {
        List<User> users = userRepo.findByCollegename(collegename);
        return users.stream()
                    .map(user -> modelMapper.map(user, UserDto.class))
                    .collect(Collectors.toList());
    }

	 @Override
	    public void addRoleToUser(String email, String roleName) {
		// Find the user by email
		    User user = userRepo.findByEmail(email)
		            .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

		    // Find the role by name
		    Role role = roleRepo.findByName(roleName)
		            .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));

		    // Clear existing roles
		    user.getRoles().clear();

		    // Add the new role
		    user.getRoles().add(role);

		    // Save the user
		    userRepo.save(user);
	    }
	 
	 @Override
	    public UserDto getUserByEmail(String email) {
	        User user = userRepo.findByEmail(email)
	                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
	        return modelMapper.map(user, UserDto.class);
	    }

	 @Override
	    public List<UserDto> getUsersByRole(String roleName) {
	        Role role = roleRepo.findByName(roleName)
	                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));
	        return userRepo.findAll().stream()
	                .filter(user -> user.getRoles().contains(role))
	                .map(user -> modelMapper.map(user, UserDto.class))
	                .collect(Collectors.toList());
	    }
}
