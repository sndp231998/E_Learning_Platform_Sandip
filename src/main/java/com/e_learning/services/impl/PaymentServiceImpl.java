package com.e_learning.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    private PaymentRepo paymentRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;

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
            totalPrice *= 0.10;  // 10% discount for 2 categories
        } else if (categoryIds.size() >= 3) {
            totalPrice *= 0.15;  // 15% discount for 3 or more categories
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

        // Fetch roles from roleRepo
        Role normalUserRole = roleRepo.findByName("ROLE_NORMAL")
                .orElseThrow(() -> new RuntimeException("Normal user role not found"));
        Role subscribedUserRole = roleRepo.findByName("ROLE_SUBSCRIBE")
                .orElseThrow(() -> new RuntimeException("Subscribed user role not found"));

        
        // Update the user's faculty with the purchased categories
        User user = payment.getUser();
        boolean isNormalUser = user.getRoles().contains(normalUserRole);
        boolean isSubscribedUser = user.getRoles().contains(subscribedUserRole);

        if (isNormalUser) {
            // Remove NORMAL_USER role
            user.getRoles().remove(normalUserRole);
            // Add SUBSCRIBED_USER role
            user.getRoles().add(subscribedUserRole);
            userRepo.save(user);
        }

        
        List<String> categoryNames = payment.getCategories().stream()
                .map(Category::getCategoryTitle)
                .collect(Collectors.toList());

        List<String> existingFaculties = user.getFacult() != null ? user.getFacult() : new ArrayList<>();
        existingFaculties.addAll(categoryNames);
        
        user.setFacult(existingFaculties);  // Update the user's faculties
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



    


    

    // Add the category names/IDs to the user's facult list
//  List<String> categoryNames = categories.stream()
//      .map(Category::getCategoryTitle) // Assuming Category entity has a 'name' field
//      .collect(Collectors.toList());
//
//  List<String> existingFaculties = user.getFacult() != null ? user.getFacult() : new ArrayList<>();
//  existingFaculties.addAll(categoryNames);
//  
//  user.setFacult(existingFaculties);  // Update the user's faculties
//
//        Payment newPayment = paymentRepo.save(payment);
//  return modelMapper.map(newPayment, PaymentDto.class);

}
