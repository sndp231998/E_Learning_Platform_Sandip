package com.e_learning.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.e_learning.payloads.ExamDto;
import com.e_learning.payloads.PaymentDto;

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
		public ResponseEntity<PaymentDto> createPayment(@RequestBody PaymentDto paymentDto, @PathVariable Integer userId) {
			PaymentDto createPayment = this.paymentService.createPayment(paymentDto, userId);
			return new ResponseEntity<PaymentDto>(createPayment, HttpStatus.CREATED);
		}
		
		//Get all payment
		@GetMapping("/payments")
		public ResponseEntity<List<PaymentDto>> getPayments() {
			List<PaymentDto> payments = this.paymentService.getAllPayments();
			return ResponseEntity.ok(payments);
		}
}
