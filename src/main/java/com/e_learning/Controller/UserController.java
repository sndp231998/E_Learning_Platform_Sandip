package com.e_learning.Controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.e_learning.payloads.ApiResponse;
import com.e_learning.payloads.UserDto;
import com.e_learning.services.UserService;




@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	@Autowired
	private UserService userService;

	// POST-create user
	@PostMapping("/")
	public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
		UserDto createUserDto = this.userService.createUser(userDto);
		return new ResponseEntity<>(createUserDto, HttpStatus.CREATED);
	}

	// PUT- update user
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

	// GET - user get
	@GetMapping("/")
	public ResponseEntity<List<UserDto>> getAllUsers() {
		return ResponseEntity.ok(this.userService.getAllUsers());
	}

	// GET - user get
	@GetMapping("/{userId}")
	public ResponseEntity<UserDto> getSingleUser(@PathVariable Integer userId) {
		return ResponseEntity.ok(this.userService.getUserById(userId));
	}
	
	@GetMapping("/college/{collegename}")
    public ResponseEntity<List<UserDto>> getUsersByCollegeName(@PathVariable String collegename) {
        List<UserDto> users = userService.getUsersByCollegeName(collegename);
        return ResponseEntity.ok(users);
    }
	
	//ROles ko ------------------
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
	
    @GetMapping("/role/{roleName}")
    public ResponseEntity<List<UserDto>> getUsersByRole(@PathVariable String roleName) {
        List<UserDto> users = userService.getUsersByRole(roleName);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

}
