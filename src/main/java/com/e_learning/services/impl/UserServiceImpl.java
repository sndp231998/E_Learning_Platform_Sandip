package com.e_learning.services.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.e_learning.config.AppConstants;
import com.e_learning.entities.OtpRequest;
import com.e_learning.entities.Payment;
import com.e_learning.entities.Role;
import com.e_learning.entities.User;
import com.e_learning.exceptions.ApiException;
import com.e_learning.exceptions.ResourceNotFoundException;
import com.e_learning.payloads.UserDto;
import com.e_learning.payloads.UserFacultyDto;
import com.e_learning.repositories.CategoryRepo;
import com.e_learning.repositories.OtpRequestRepo;
import com.e_learning.repositories.PaymentRepo;
import com.e_learning.repositories.RoleRepo;
import com.e_learning.repositories.UserRepo;
import com.e_learning.services.NotificationService;
import com.e_learning.services.OtpRequestService;
import com.e_learning.services.UserService;


@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private OtpRequestRepo otpRepo;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private PaymentRepo paymentRepo;

   // @Autowired
   // private NotificationService notificationService;
@Autowired
    private CategoryRepo categoryRepo;
    @Autowired
private OtpRequestService sendmsg;
    
    @Override
    public UserDto startTrialForNewUser(Integer userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));

        // Set trial role
        Role trialRole = roleRepo.findById(AppConstants.SUBSCRIBED_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "Id", AppConstants.SUBSCRIBED_USER));
     // Check if the user is still on a trial or has already completed it
        if (user.getTrialExpiryDate() != null && LocalDateTime.now().isBefore(user.getTrialExpiryDate())) {
            throw new RuntimeException("User is already on a trial or has already completed it.");
        }
        
        // Clear existing roles and add the trial role
        user.getRoles().clear();
        user.getRoles().add(trialRole);
        
        // Assign all category titles as faculties
        List<String> allCategories = categoryRepo.findAll()
                .stream()
                .map(category -> category.getCategoryTitle())
                .collect(Collectors.toList());
        user.setFacult(allCategories);
        
        // Set trial expiry date to 7 days from now user.setTrialExpiryDate(LocalDate.now().plusDays(7));
user.setTrialExpiryDate(LocalDateTime.now().plusDays(7));
     User updatedUser=   userRepo.save(user);

        // Log or notify the user
        logger.info("7-day trial started for user {} with all faculties assigned", userId);
        return this.userToDto(updatedUser);
    }

    @Scheduled(cron = "0 0 0 * * ?")  // Runs daily at midnight
    public void expireTrialRoles() {
        List<User> usersWithTrial = userRepo.findAllByTrialExpiryDateBefore(LocalDateTime.now());
        
        for (User user : usersWithTrial) {
            // Remove trial role and assign normal role
            Role normalRole = roleRepo.findById(AppConstants.NORMAL_USER)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "Id", AppConstants.NORMAL_USER));
            
            user.getRoles().clear();
            user.getRoles().add(normalRole);

            // Clear faculties assigned during the trial
            user.setFacult(Collections.emptyList());
            user.setTrialExpiryDate(null);  // Clear trial expiry date

            userRepo.save(user);

            // Notify the user
            String message = "Your 7-day trial has ended. Access to all faculties has been removed.";
            notificationService.createNotification(user.getId(), message);
        }}
    
    
    //---------------update-Facult-------------------
    @Override
    public UserDto updateFacult(UserDto userDto, Integer userId) {
        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));

        // Fetch the NORMAL_USER role
        Role normalUserRole = this.roleRepo.findById(AppConstants.NORMAL_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "Id", AppConstants.NORMAL_USER));

        // Check if user already has the NORMAL_USER role
        if (user.getRoles().contains(normalUserRole)) {
            throw new ApiException("Cannot add faculty !!.Please change the role before adding faculty.");
        }
        
        List<String> currentFacult = user.getFacult();
        List<String> newFacult = userDto.getFacult();

        // Fetch all valid category titles
        List<String> validCategories = categoryRepo.findAll()
                .stream()
                .map(category -> category.getCategoryTitle())
                .collect(Collectors.toList());

        List<String> alreadyExistingFaculties = new ArrayList<>();
        List<String> invalidFaculties = new ArrayList<>();

        for (String faculty : newFacult) {
            if (!validCategories.contains(faculty)) {
               
            	 throw new ApiException("Invalid faculty: '" + faculty + "' does not match any category title.");
               
            }

            if (currentFacult.contains(faculty)) {
            	throw new ApiException("Faculty '" + faculty + "' already exists for user.");
            	
            } else {
                // Add new valid faculty to user's list
                currentFacult.add(faculty);
            }
        }

        // Update user's faculty list
        user.setFacult(currentFacult);
        User updatedUser = this.userRepo.save(user);

        // Log existing and invalid faculties
        if (!alreadyExistingFaculties.isEmpty()) {
            logger.info("These faculties already exist for user {}: {}", userId, alreadyExistingFaculties);
        }
        if (!invalidFaculties.isEmpty()) {
            logger.info("These faculties are invalid and were not added: {}", invalidFaculties);
        }

        return this.userToDto(updatedUser);
    }

    
    @Override
    public void deleteFaculty(Integer userId, String facultyName) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Check if facultyName exists in category titles
        List<String> validCategories = categoryRepo.findAll()
                .stream()
                .map(category -> category.getCategoryTitle())
                .collect(Collectors.toList());

        if (!validCategories.contains(facultyName)) {
            throw new ApiException("Faculty '" + facultyName + "' does not match any category title.");
        }

        // Check if the facultyName exists in the user's faculty list
        List<String> currentFacult = user.getFacult();
        if (!currentFacult.contains(facultyName)) {
            throw new ApiException("Faculty '" + facultyName + "' does not exist in the user's faculty list.");
        }

        // Remove the faculty and save the user
        currentFacult.remove(facultyName);
        user.setFacult(currentFacult);
        userRepo.save(user);
    }

    
    @Override
    public void addRoleToUser(String email, String roleName) {
        // Fetch user by email, throw exception if not found
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        // Fetch role by name, throw exception if not found
        Role role = roleRepo.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));

        // Clear existing roles and assign new role
        user.getRoles().clear();  // Clear all existing roles
        user.getRoles().add(role);  // Assign new role
        user.setDate_Of_Role_Changed(LocalDateTime.now());  // Update role change date
        
        // Save updated user
        userRepo.save(user);
        System.out.println("User role changed to " + roleName + ".");
    }
    
    @Override
    public UserDto registerNewUser(UserDto userDto) {
        User user = this.modelMapper.map(userDto, User.class);
       user.setImageName("");
       user.setDateOfRegistration(LocalDateTime.now());
       user.setMobileNo(userDto.getEmail());
     // encoded the password
     		user.setPassword(this.passwordEncoder.encode(user.getPassword()));
     		// roles
    		Role role = this.roleRepo.findById(AppConstants.NORMAL_USER).get();
    		user.getRoles().add(role);
        String otp = userDto.getOtp();
        
        // Logging OTP from user
        logger.info("Otp from user: " + otp);
        
        if (otp == null) {
            throw new IllegalArgumentException("OTP must be provided");
        }

        // Get OTP requests from the repository
        List<OtpRequest> otpRequests = this.otpRepo.findByOtp(otp);
        logger.info("Retrieved OTP requests: " + otpRequests);
        
        // Iterate through each OTP request and check validity
        OtpRequest validOtpRequest = null;
        for (OtpRequest otpRequest : otpRequests) {
            // Null check before comparison
            if (otpRequest.getOtp() != null && otpRequest.getOtp().equals(otp)) {
                LocalDateTime otpValidUntil = otpRequest.getOtpValidUntil();
                if (otpValidUntil != null) {
                    Instant otpValidUntilInstant = otpValidUntil.atZone(ZoneId.systemDefault()).toInstant();
                    Instant now = Instant.now();
                    if (otpValidUntilInstant.isAfter(now)) {
                        validOtpRequest = otpRequest;
                        break; // Exit the loop if a valid OTP is found
                    }
                }
            }
        }
        
        if (validOtpRequest == null) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }
        
        String mobileNo = validOtpRequest.getMobileNo();
        user.setMobileNo(mobileNo);
        
        User newUser = this.userRepo.save(user);
        
        String welcomeMessage = String.format("Welcome, %s! We're excited to have you on our eLearning platform. Dive in and enjoy the journey ahead! "
        		+ "Thank you for choosing us, Utkrista Shikshya", user.getName());
        sendmsg.sendMessage(user.getMobileNo(), welcomeMessage); // Assuming notificationService sends SMS

     // Create in-app notification
        notificationService.createNotification(newUser.getId(), welcomeMessage);
        
        return this.modelMapper.map(newUser, UserDto.class);
    }
    
    
    
  
  
    //--------------------------forget password----------------
    //--------------------------get otp from user --------
    @Override
    public UserDto GetOtp(UserDto userDto, Integer userId) {
        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));

        // Saving OTP to User table (optional step)
        user.setOtp(userDto.getOtp());
        
        // Save and return updated user with OTP
        return modelMapper.map(userRepo.save(user), UserDto.class);
    }
    
    //forget password 
	@Override
	public UserDto updatePassword(UserDto userDto, Integer userId) {
		User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));

		String otp=userDto.getOtp();
		if(otp==null) {
			throw new IllegalArgumentException("OTP must be provided");
		}
		// Fetch the OTP from the database
	    List<OtpRequest> otpRequests = this.otpRepo.findByOtp(otp);
	    OtpRequest validOtpRequest = null;
	    for (OtpRequest otpRequest : otpRequests) {
	        if (otpRequest.getOtp() != null && otpRequest.getOtp().equals(otp)) {
	            LocalDateTime otpValidUntil = otpRequest.getOtpValidUntil();
	            if (otpValidUntil != null) {
	                Instant otpValidUntilInstant = otpValidUntil.atZone(ZoneId.systemDefault()).toInstant();
	                Instant now = Instant.now();
	                if (otpValidUntilInstant.isAfter(now)) {
	                    validOtpRequest = otpRequest;
	                    break; // Found valid OTP, exit loop
	                }
	            }
	        }
	    }
	    if (validOtpRequest == null) {
	        throw new IllegalArgumentException("Invalid or expired OTP");
	    }
	    // OTP is valid, proceed to update the password
	    user.setPassword(passwordEncoder.encode(userDto.getPassword()));
	    User updatedUser = userRepo.save(user);

	    return modelMapper.map(updatedUser, UserDto.class);
		
	}
    

    @Override
    public UserDto updateUser(UserDto userDto, Integer userId) {
        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));

        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
       // user.setPassword(userDto.getPassword());
        user.setCollegename(userDto.getCollegename());
        user.setImageName(userDto.getImageName());
     
        User updatedUser = this.userRepo.save(user);
        return this.userToDto(updatedUser);
    }

    
    
    
    @Override
    public UserDto createUser(UserDto userDto) {
        User user = this.dtoToUser(userDto);
        User savedUser = this.userRepo.save(user);
        return this.userToDto(savedUser);
    }


    @Override
    public UserDto getUserById(Integer userId) {
        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));

        return this.userToDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = this.userRepo.findAll();
        return users.stream().map(this::userToDto).collect(Collectors.toList());
    }

    @Override
    public void deleteUser(Integer userId) {
        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));
      user.getRoles().clear();
        this.userRepo.delete(user);
    }

    public User dtoToUser(UserDto userDto) {
        return this.modelMapper.map(userDto, User.class);
    }

    public UserDto userToDto(User user) {
        return this.modelMapper.map(user, UserDto.class);
    }

    
 
    // other methods


    @Override
    public List<UserDto> getUsersByCollegeName(String collegename) {
        List<User> users = userRepo.findByCollegename(collegename);
        return users.stream().map(user -> modelMapper.map(user, UserDto.class)).collect(Collectors.toList());
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



//---------------Update-Faculty--------------------------------------------------------
//    @Override
//    public UserDto updateFaculty(UserDto userDto, Integer userId) {
//        User user = this.userRepo.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));
//        
//        user.setFaculty(userDto.getFaculty());
//        logger.info("Faculty from service "+userDto.getFaculty());
//        User updatedUser = this.userRepo.save(user);
//        return this.userToDto(updatedUser);
//    }
    

    //----------update discount only-------------
    @Override
    public UserDto updateDiscount(UserDto userDto, Integer userId) {
        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));
        
        user.setDiscount(userDto.getDiscount());
        logger.info("discount from service "+userDto.getDiscount());
        User updatedUser = this.userRepo.save(user);
        return this.userToDto(updatedUser);
    }





    @Override
    public List<String> getFacultiesByUserId(int userId) {
    	User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));
    	
    	
        List<String>faculties= userRepo.findFacultiesByUserId(userId);
        if (faculties == null || faculties.isEmpty()) {
            throw new ResourceNotFoundException("Subscribed  cource ","Id",userId);
        }
        return faculties;
    }



    @Override
    public List<UserDto> getUsersJoinedInLast7Days() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<User> recentUsers = userRepo.findUsersJoinedInLast7Days(sevenDaysAgo);
        return recentUsers.stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

 
    public List<UserFacultyDto> getUsersWithTeacherOrSubscribedRoles() {
        List<User> users = userRepo.findByRolesTeacherOrSubscribed();
        System.out.println("Fetched Users: " + users.size());

        // Map each user to UserFacultyDto, including roles
        return users.stream()
                    .map(user -> {
                        // Extract role names as a list of strings
                        List<String> roleNames = user.getRoles().stream()
                                                     .map(Role::getName)
                                                     .collect(Collectors.toList());
                        // Create UserFacultyDto with id, faculties, and roles
                        return new UserFacultyDto(user.getId(), user.getFacult(), roleNames);
                    })
                    .collect(Collectors.toList());
    }

    }

	

