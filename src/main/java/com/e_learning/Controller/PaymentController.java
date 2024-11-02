package com.e_learning.Controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.hibernate.engine.jdbc.StreamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.e_learning.entities.User;
import com.e_learning.payloads.CategoryDto;
import com.e_learning.payloads.ExamDto;
import com.e_learning.payloads.PaymentDto;
import com.e_learning.payloads.PaymentRequest;
import com.e_learning.repositories.RoleRepo;
import com.e_learning.services.FileService;
import com.e_learning.services.PaymentService;

@RestController
@RequestMapping("/api/v1/payment")
public class PaymentController {

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private FileService fileService;

	@Value("${project.image}")
	private String path;
	
	//create Payment
	@PostMapping("/user/{userId}")
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

	
	// Approve payment (Admin)
    @PutMapping("/approve/{paymentId}")
    public ResponseEntity<PaymentDto> approvePayment(@PathVariable Integer paymentId) {
        PaymentDto approvedPayment = paymentService.approvePayment(paymentId);
        return new ResponseEntity<>(approvedPayment, HttpStatus.OK);
    }

    // Reject payment (Admin)
    @PutMapping("/reject/{paymentId}")
    public ResponseEntity<PaymentDto> rejectPayment(@PathVariable Integer paymentId) {
        PaymentDto rejectedPayment = paymentService.rejectPayment(paymentId);
        return new ResponseEntity<>(rejectedPayment, HttpStatus.OK);
    }
		
		//Get all payment
		@GetMapping("/payments")
		public ResponseEntity<List<PaymentDto>> getPayments() {
			List<PaymentDto> payments = this.paymentService.getAllPayments();
			return ResponseEntity.ok(payments);
		}
		
		@GetMapping("/check/user/{userId}/category/{categoryId}")
		public ResponseEntity<Boolean> checkIfCategoryPayment(@PathVariable Integer userId, @PathVariable Integer categoryId) {
		    boolean isBooked = paymentService.isCategoryPaymentByUser(userId, categoryId);
		    return ResponseEntity.ok(isBooked);
		}
		
		
		//-------------Image upload-------------------
	  	// Post method for file upload
	      @PostMapping("/file/upload/{paymentId}")
	      public ResponseEntity<PaymentDto> uploadPaymentFile(@RequestParam("file") MultipartFile file,
	                                                    @PathVariable Integer paymentId) throws IOException {
	          PaymentDto paymentDto = this.paymentService.getPayment(paymentId);
	          String fileName = this.fileService.uploadFile(path, file);
	          paymentDto.setPayment_screensort(fileName);// Assuming you want to set the uploaded file name as imageName
	          PaymentDto updatedpayment = this.paymentService.updatePayment(paymentDto, paymentId);
	          return new ResponseEntity<>(updatedpayment, HttpStatus.OK);
	      }
	    //-------------method to serve files------------------
	      @GetMapping(value = "/image/{imageName}", produces = MediaType.IMAGE_JPEG_VALUE)
	      public void downloadImage(
	              @PathVariable("imageName") String imageName,
	              HttpServletResponse response
	      ) throws IOException {
	          // Log the file name and path
	          String filePath = path + "/" + imageName;
	          System.out.println("Serving image: " + filePath);

	          String fileExtension = FilenameUtils.getExtension(imageName).toLowerCase();
	          MediaType mediaType = MediaType.IMAGE_JPEG;  // Default

	          if (fileExtension.equals("png")) {
	              mediaType = MediaType.IMAGE_PNG;
	          } else if (fileExtension.equals("jpg") || fileExtension.equals("jpeg")) {
	              mediaType = MediaType.IMAGE_JPEG;
	          }

	          response.setContentType(mediaType.toString());
	          try (InputStream resource = this.fileService.getResource(path, imageName)) {
	              if (resource == null) {
	                  throw new FileNotFoundException("File not found: " + filePath);
	              }
	              StreamUtils.copy(resource, response.getOutputStream());
	          } catch (Exception e) {
	              e.printStackTrace();  // Log the exception
	              response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error serving the image");
	          }
	      }
	      
	   // Update Payment
	      @PutMapping("/{paymentId}")
	      public ResponseEntity<PaymentDto> updatePayment(
	              @RequestBody PaymentDto paymentDto, 
	              @PathVariable Integer paymentId) {
	          PaymentDto updatedPayment = this.paymentService.updatePayment(paymentDto, paymentId);
	          return new ResponseEntity<>(updatedPayment, HttpStatus.OK);
	      }
	   // Get Payment by ID
	      @GetMapping("/{paymentId}")
	      public ResponseEntity<PaymentDto> getPaymentById(@PathVariable Integer paymentId) {
	          PaymentDto payment = this.paymentService.getPayment(paymentId);
	          return ResponseEntity.ok(payment);
	      }
	      
	      @GetMapping("/monthly/revenue")
	      public ResponseEntity<Map<String, Integer>> getMonthlyRevenues() {
	          return ResponseEntity.ok(paymentService.getMonthlyRevenues());
	      }

	      
//	      @GetMapping("/weekly")
//	      public ResponseEntity<Integer> getWeeklyRevenue() {
//	          return ResponseEntity.ok(paymentService.getWeeklyRevenue());
//	      }

	      

//	      @GetMapping("/yearly")
//	      public ResponseEntity<Integer> getYearlyRevenue() {
//	          return ResponseEntity.ok(paymentService.getYearlyRevenue());
//	      }
//	      
//	      @GetMapping("/daily")
//	      public ResponseEntity<Integer> getDailyRevenue() {
//	          return ResponseEntity.ok(paymentService.getDailyRevenue());
//	      }
}
