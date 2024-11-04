package com.e_learning.services.impl;

import java.time.Instant;
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
    
    
    
    
    //---------------update-Facult-------------------
    @Override
    public UserDto updateFacult(UserDto userDto, Integer userId) {
        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "Id", userId));

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

   
  //1800000==30 min
//  @Override
//  @Scheduled(fixedRate =120000) // Runs every 2 minutes
//  public void updateUserRoles() {
//      logger.info("updateUserRoles method started");
//      List<User> users = userRepo.findAll();
//      logger.info("Number of users found: {}", users.size());
//
//      for (User user : users) {
//          if (user.getSubscriptionValidDate() != null) { 
//              LocalDateTime validDate = user.getSubscriptionValidDate();
//              logger.info("Processing user: {}, Subscription Valid Date: {}", user.getEmail(), validDate);
//
//              for (Role role : user.getRoles()) {
//                  LocalDateTime roleChangeDate = user.getDate_Of_Role_Changed();
//                  logger.info("User role: {}, Role Change Date: {}", role.getName(), roleChangeDate);
//
//                  // Check if current date is after the valid date
//                  if (roleChangeDate != null && LocalDateTime.now().isAfter(validDate)) {
//                      logger.info("Conditions met for user: {}, Role: {}", user.getEmail(), role.getName());
//
//                      // Remove old role
//                      user.getRoles().clear();
//                      logger.info("Cleared old roles for user: {}", user.getEmail());
//
//                      // Add new role
//                      Role newRole = this.roleRepo.findById(AppConstants.NORMAL_USER)
//                              .orElseThrow(() -> new ResourceNotFoundException("Role", "id", AppConstants.NORMAL_USER));
//
//                      logger.info("Added new role: {} for user: {}", newRole.getName(), user.getEmail());
//                      user.getRoles().add(newRole);
//
//                      // Clear the subscription valid date
//                      user.setSubscriptionValidDate(null);
//                      logger.info("Cleared subscription valid date for user: {}", user.getEmail());
//
//                      userRepo.save(user);
//                      logger.info("User roles updated and saved for user: {}", user.getEmail());
//                  } else {
//                      logger.info("Conditions not met for user: {}, Role: {}", user.getEmail(), role.getName());
//                  }
//              }
//          } else {
//              logger.info("User {} does not have a subscription valid date", user.getEmail());
//          }
//      }
//
//      logger.info("updateUserRoles method completed");
//  }
//
//  @Override
//  @Scheduled(fixedRate = 86400000) // Runs daily
//  public void sendSubscriptionExpiryWarnings() {
//      logger.info("sendSubscriptionExpiryWarnings method started");
//
//      List<User> users = userRepo.findAll();
//      for (User user : users) {
//          if (user.getSubscriptionValidDate() != null) {
//              LocalDateTime validDate = user.getSubscriptionValidDate();
//              LocalDateTime now = LocalDateTime.now();
//              LocalDateTime warningDate = validDate.minusDays(5);
//
//              if (now.isAfter(warningDate) && now.isBefore(validDate)) {
//                  String message = "Your subscription is ending soon. Please renew your subscription to continue enjoying our services.";
//                  notificationService.sendNotification(user.getName(), message);
//                  logger.info("Created warning notification for user: {}, Subscription Valid Date: {}", user.getEmail(), validDate);
//              }
//          }
//      }
//
//      logger.info("sendSubscriptionExpiryWarnings method completed");
//  }
    
        
    }

	

