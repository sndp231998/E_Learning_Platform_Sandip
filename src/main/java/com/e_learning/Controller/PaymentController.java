package com.e_learning.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e_learning.entities.User;
import com.e_learning.payloads.ExamDto;
import com.e_learning.payloads.PaymentDto;
import com.e_learning.payloads.PaymentRequest;
import com.e_learning.services.FileService;
import com.e_learning.services.PaymentService;

@RestController
@RequestMapping("/api/v1/")
public class PaymentController {

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private FileService fileService;

	@Value("${project.image}")
	private String path;
	
	//create Payment
	@PostMapping("/user/{userId}/payments")
	public ResponseEntity<PaymentDto> createPayment(
	        @RequestBody PaymentRequest paymentRequest, 
	        @PathVariable Integer userId) {
	    
	    // Extract the user ID from the authentication context
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    User userDetails = (User) authentication.getPrincipal();
	    Integer tokenUserId = userDetails.getId(); // Get the user ID from the token

	    // Compare the user ID from the token with the user ID from the path variable
	    if (!tokenUserId.equals(userId)) {
	        return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
	    }

	    // Call the service to create the payment, passing the selected category IDs
	    PaymentDto createPayment = this.paymentService.createPayment(
	        paymentRequest.getPaymentDto(), userId, paymentRequest.getCategoryIds());
	    
	    return new ResponseEntity<>(createPayment, HttpStatus.CREATED);
	}

		
		//Get all payment
		@GetMapping("/payments")
		public ResponseEntity<List<PaymentDto>> getPayments() {
			List<PaymentDto> payments = this.paymentService.getAllPayments();
			return ResponseEntity.ok(payments);
		}
}
