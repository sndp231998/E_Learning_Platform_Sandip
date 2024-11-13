package com.e_learning.Controller;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.e_learning.entities.ForgetPassword;
import com.e_learning.entities.OtpRequest;
import com.e_learning.entities.User;
import com.e_learning.exceptions.ApiException;

import com.e_learning.payloads.ForgetPasswordDto;
import com.e_learning.payloads.JwtAuthRequest;
import com.e_learning.payloads.JwtAuthResponse;
import com.e_learning.payloads.UserDto;
import com.e_learning.repositories.UserRepo;
import com.e_learning.security.CustomUserDetailService;
import com.e_learning.security.JwtTokenHelper;
import com.e_learning.services.ForgetPasswordService;
import com.e_learning.services.OtpRequestService;
import com.e_learning.services.UserService;
import com.e_learning.services.impl.RateLimitingService;

import java.security.Principal;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/auth/")
public class AuthController {

	
	  @Autowired
	    private OtpRequestService otpRequestService;
	
	@Autowired
	private JwtTokenHelper jwtTokenHelper;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private ModelMapper mapper;
	@Autowired
	private UserService userService;
	
	 @Autowired
	    private RateLimitingService rateLimitingService;
	 
	 @Autowired
	    private ForgetPasswordService forgetPasswordService;

	 @PostMapping("/login")
		public ResponseEntity<?> createToken(@RequestBody JwtAuthRequest request) throws Exception {
			this.authenticate(request.getUsername(), request.getPassword());
			UserDetails userDetails = this.userDetailsService.loadUserByUsername(request.getUsername());
			//UserDetails userd=this.userDetailsService.loadUserByUsername(request.getMobilenum());
			String token = this.jwtTokenHelper.generateToken(userDetails);
			
			 // Fetch the user based on the username
			 Optional<User> optionalUser = this.userRepo.findByEmail(request.getUsername());
			    User user = optionalUser.orElseThrow(() -> new Exception("User not found"));

			    String providedIp = request.getBrowserInfo();
			    if (user.getUserAgent1() == null || user.getUserAgent1().isEmpty()) {
			        user.setUserAgent1(providedIp);
			        userRepo.save(user); // Save the updated user information in the database
			    }
		    // Get the stored IP and compare it with the provided IP from the request
		    String storedIp = user.getUserAgent1();
		   
		 // Check if the provided IP matches the stored IP
		    // Check if the provided IP matches the stored IP
		    if (!storedIp.equals(providedIp)) {
		        return new ResponseEntity<String>("IP address mismatch. please contact to Utkrista Shikshya ", HttpStatus.UNAUTHORIZED);
		    }
		    rateLimitingService.checkRateLimit("test-api-key");
			JwtAuthResponse response = new JwtAuthResponse();
			response.setToken(token);
			response.setUser(this.mapper.map((User) userDetails, UserDto.class));
			return new ResponseEntity<JwtAuthResponse>(response, HttpStatus.OK);
		}

	 

	
//otp for registration
	    @PostMapping("/get-phone-number")
	    public ResponseEntity<OtpRequest> createOtp(@RequestBody OtpRequest otpReq) {
	    	
	    	OtpRequest ph = otpRequestService.createOtp(otpReq);
	    	
	    			
	        return ResponseEntity.ok(ph);
	    }
	
	    


// register new user api

	@PostMapping("/register")
	public ResponseEntity<UserDto> registerUser(@Valid @RequestBody UserDto userDto) {
		UserDto registeredUser = this.userService.registerNewUser(userDto);
		rateLimitingService.checkRateLimit("test-api-key");		
		return new ResponseEntity<UserDto>(registeredUser, HttpStatus.CREATED);
	}

	// get loggedin user data


	@GetMapping("/current-user/")
	public ResponseEntity<UserDto> getUser(Principal principal) {
		User user = this.userRepo.findByEmail(principal.getName()).get();
		return new ResponseEntity<UserDto>(this.mapper.map(user, UserDto.class), HttpStatus.OK);
	}

	

	private void authenticate(String username, String password) throws Exception {

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
				password);

		try {

			this.authenticationManager.authenticate(authenticationToken);

		} catch (BadCredentialsException e) {
			System.out.println("Invalid Detials !!");
			throw new ApiException("Invalid username or password !!");
		}

	}
	
	//--------------Forget password-----------------
	//otp for forget password
	//get otp
    @PostMapping("/forgetpw")
    public ResponseEntity<ForgetPassword> createForgetPassword(@RequestBody ForgetPasswordDto forgetPasswordDto) {
        ForgetPassword forgetPassword = forgetPasswordService.createForget(forgetPasswordDto);
        rateLimitingService.checkRateLimit("test-api-key");
        return ResponseEntity.ok(forgetPassword);
    }

    @PostMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestBody ForgetPassword request) {
        try {
        	forgetPasswordService.updatePassword(request.getPhnum(), request.getOtp(), request.getNewPassword());
            return ResponseEntity.ok("Password updated successfully");
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

   
	 
}
