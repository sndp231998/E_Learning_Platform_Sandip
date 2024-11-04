package com.e_learning.services;

import java.util.List;

import com.e_learning.entities.User;
import com.e_learning.payloads.UserDto;



public interface UserService {
	
UserDto registerNewUser(UserDto user);
	
//-----------User GetOnlyPhoneNum(User user);------------

	UserDto createUser(UserDto user);

	UserDto updateUser(UserDto user, Integer userId);

	UserDto getUserById(Integer userId);

	List<UserDto> getAllUsers();

	void deleteUser(Integer userId);
	
	List<UserDto> getUsersByCollegeName(String collegename);  
	
	//--------------------role change------------------------
	void addRoleToUser(String email, String roleName);
	
	//void updateUserRoles();
	
	//void sendSubscriptionExpiryWarnings();
	
    UserDto getUserByEmail(String email);
    
    List<UserDto> getUsersByRole(String roleName);
    
   // List<UserDto>getUsersByPayment(String )
	
//---------------forget password---------------
    UserDto updatePassword(UserDto user ,Integer userId);
    UserDto GetOtp(UserDto user,Integer userId);
    
    //---------faculty add and update ---------------
    
    //UserDto updateFaculty(UserDto user, Integer userId);
    UserDto updateFacult(UserDto user,Integer userID);
    
    List<String> getFacultiesByUserId(int userId);
    //------discount--------------
   //UserDto updateDiscount(Integer userId,String discount);
    UserDto updateDiscount(UserDto user, Integer userId);
    
    List<UserDto> getUsersJoinedInLast7Days();

	void deleteFaculty(Integer userId, String facultyName);

	UserDto startTrialForNewUser(Integer userId);
}
