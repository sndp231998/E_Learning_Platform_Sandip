package com.e_learning.services.impl;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_learning.config.AppConstants;
import com.e_learning.entities.Category;
import com.e_learning.entities.Payment;
import com.e_learning.entities.Payment.PaymentStatus;
import com.e_learning.entities.Role;
import com.e_learning.entities.User;
import com.e_learning.exceptions.ResourceNotFoundException;

import com.e_learning.payloads.PaymentDto;
import com.e_learning.repositories.CategoryRepo;
import com.e_learning.repositories.PaymentRepo;
import com.e_learning.repositories.RoleRepo;
import com.e_learning.repositories.UserRepo;
import com.e_learning.services.PaymentService;
@Service
public class PaymentServiceImpl implements PaymentService {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RoleRepo roleRepo;
    
 
    public void updateTotalPrice(Payment payment) {
        if (payment.getCategories() != null && !payment.getCategories().isEmpty()) {
            // Calculate the total price based on selected categories
            Integer total = payment.getCategories().stream()
                    .mapToInt(category -> Integer.parseInt(category.getPrice())) // Ensure this returns an integer
                    .sum();
            
            // Update the total and totalPrice in the payment entity
           
            payment.setTotal(total);
            payment.setTotalPrice(total); // Assuming both total and totalPrice should be the same
        }
    }  
    @Override
    public PaymentDto createPayment(PaymentDto paymentDto, Integer userId, List<Integer> categoryIds) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "User id", userId));

//        Category = categoryRepo.findById(categoryIds)
//        		.orElseThrow(()-> new ResourceNotFoundException("Category","Category id",categoryId));
//        		
        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new IllegalArgumentException("Category IDs cannot be null or empty.");
        }

        // Ensure categoryIds are properly handled as a list even if it's a single element
        if (categoryIds.size() == 1) {
            categoryIds = Collections.singletonList(categoryIds.get(0));
        }

        List<Category> categories = categoryRepo.findAllById(categoryIds);

        if (categories.isEmpty()) {
            throw new ResourceNotFoundException("Categories", "Category IDs", categoryIds.toString());
        }

        // Check if user has already purchased any requested categories
        List<Category> purchasedCategories = paymentRepo.findCategoriesByUserId(userId);
        List<Integer> purchasedCategoryIds = purchasedCategories.stream()
                .map(Category::getCategoryId)
                .collect(Collectors.toList());

        List<Integer> alreadyPurchased = categoryIds.stream()
                .filter(purchasedCategoryIds::contains)
                .collect(Collectors.toList());

        if (!alreadyPurchased.isEmpty()) {
            throw new IllegalArgumentException("User has already purchased categories: " + alreadyPurchased);
        }

        // Calculate total price
        int totalPrice = categories.stream()
                .mapToInt(category -> Integer.parseInt(category.getPrice()))
                .sum();

        // Apply discount based on the number of categories selected
        if (categoryIds.size() == 2) {
            totalPrice *= 0.90;  // Reduce by 10% for 2 categories
        } else if (categoryIds.size() >= 3) {
            totalPrice *= 0.85;  // Reduce by 15% for 3 or more categories
        }


        // Map paymentDto to Payment
        Payment payment = modelMapper.map(paymentDto, Payment.class);
        payment.setUser(user);
        payment.setTotalPrice(totalPrice);
        payment.setPayment_screensort("");
        payment.setAddedDate(LocalDateTime.now());
        payment.setCategories(categories);
        payment.setStatus(PaymentStatus.PENDING); // Set the status to pending

        Payment newPayment = paymentRepo.save(payment);
        return modelMapper.map(newPayment, PaymentDto.class);
    }

    
    @Override
    public PaymentDto getPayment(Integer paymentId) {
        // Find the payment by ID or throw an exception if not found
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "Payment id", paymentId));

        // Map the found payment to PaymentDto and return it
        return modelMapper.map(payment, PaymentDto.class);
    }



    @Override
    public PaymentDto updatePayment(PaymentDto paymentDto, Integer paymentId) {
        // Find the payment by ID or throw an exception if not found
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "Payment id", paymentId));

        // Update the existing payment details with new data from PaymentDto
        payment.setTotalPrice(paymentDto.getTotalPrice());
        payment.setStatus(Payment.PaymentStatus.valueOf(paymentDto.getStatus().name()));
        payment.setPayment_screensort(paymentDto.getPayment_screensort());
        payment.setAddedDate(LocalDateTime.now());  // Optionally, you can update the date if necessary
        payment.setCategories(paymentDto.getCategories().stream()
                .map(categoryDto -> categoryRepo.findById(categoryDto.getCategoryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Category", "Category id", categoryDto.getCategoryId())))
                .collect(Collectors.toList())
        );

        // Save the updated payment to the database
        Payment updatedPayment = paymentRepo.save(payment);

        // Map the updated payment to PaymentDto and return it
        return modelMapper.map(updatedPayment, PaymentDto.class);
    }
    
    //---------------------approved payment-----------------------
    
    public PaymentDto approvePayment(Integer paymentId) {
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "Payment id", paymentId));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalArgumentException("Payment is already " + payment.getStatus());
        }
         
       
        // Update payment status to APPROVED
        payment.setStatus(PaymentStatus.APPROVED);
        paymentRepo.save(payment);

//        
       
        User user = payment.getUser();
       
        Role newRole = this.roleRepo.findById(AppConstants.SUBSCRIBED_USER)
              .orElseThrow(() -> new ResourceNotFoundException("Role", "id", AppConstants.SUBSCRIBED_USER));
//
      logger.info("Added new role: {} for user: {}", newRole.getName(), user.getEmail());
//     
      user.getRoles().clear();
        user.getRoles().add(newRole);
       
        
        userRepo.save(user);

        // Update the user's faculty with the purchased categories
        List<String> categoryNames = payment.getCategories().stream()
                .map(Category::getCategoryTitle)
                .collect(Collectors.toList());

     // Log the category names
        logger.info("Category Titles for paymentId {}: {}", paymentId, categoryNames);

        List<String> existingFaculties = user.getFacult() != null ? user.getFacult() : new ArrayList<>();
        existingFaculties.addAll(categoryNames);
        
        logger.info("Existing Faculties before update for userId {}: {}", user.getId(), existingFaculties);

        user.setFacult(existingFaculties);  // Update the user's faculties
        
        logger.info("Updated Faculties after adding categories for userId {}: {}", user.getId(), user.getFacult());

        userRepo.save(user);  // Save the user with the updated faculties

        return modelMapper.map(payment, PaymentDto.class);
    }

    
    
    
    public PaymentDto rejectPayment(Integer paymentId) {
        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "Payment id", paymentId));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new IllegalArgumentException("Payment is already " + payment.getStatus());
        }

        // Update payment status to REJECTED
        payment.setStatus(PaymentStatus.REJECTED);
        paymentRepo.save(payment);

        return modelMapper.map(payment, PaymentDto.class);
    }

    
    
    
    

    @Override
    public List<PaymentDto> getAllPayments() {
        return paymentRepo.findAll().stream()
                .map(payment -> modelMapper.map(payment, PaymentDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isCategoryPaymentByUser(Integer userId, Integer categoryId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "User id", userId));

        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Category id", categoryId));

        return paymentRepo.findByUserAndCategory(user, category).isPresent();
    }


    @Override
    public Integer getWeeklyRevenue() {
        LocalDateTime startOfWeek = LocalDateTime.now().with(java.time.DayOfWeek.MONDAY).truncatedTo(ChronoUnit.DAYS);
        LocalDateTime endOfWeek = startOfWeek.plusWeeks(1);
        return paymentRepo.calculateTotalRevenue(startOfWeek, endOfWeek);
    }

    @Override
    public Integer getMonthlyRevenue() {
        LocalDateTime startOfMonth = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).truncatedTo(ChronoUnit.DAYS);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
        return paymentRepo.calculateTotalRevenue(startOfMonth, endOfMonth);
    }

    @Override
    public Integer getYearlyRevenue() {
        LocalDateTime startOfYear = LocalDateTime.now().with(TemporalAdjusters.firstDayOfYear()).truncatedTo(ChronoUnit.DAYS);
        LocalDateTime endOfYear = startOfYear.plusYears(1);
        return paymentRepo.calculateTotalRevenue(startOfYear, endOfYear);
    }

}
