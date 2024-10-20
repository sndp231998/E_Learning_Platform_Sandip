package com.e_learning.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_learning.entities.Category;
import com.e_learning.entities.Payment;
import com.e_learning.entities.User;
import com.e_learning.exceptions.ResourceNotFoundException;

import com.e_learning.payloads.PaymentDto;
import com.e_learning.repositories.CategoryRepo;
import com.e_learning.repositories.PaymentRepo;
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

    @Override
    public PaymentDto createPayment(PaymentDto paymentDto, Integer userId, List<Integer> categoryIds) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "User id", userId));

        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new IllegalArgumentException("Category IDs cannot be null or empty.");
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

        // Map paymentDto to Payment
        Payment payment = modelMapper.map(paymentDto, Payment.class);
        payment.setUser(user);
        payment.setTotalPrice(totalPrice);
        payment.setAddedDate(LocalDateTime.now());
        payment.setCategories(categories);

        
        
        // Add the category names/IDs to the user's facult list
        List<String> categoryNames = categories.stream()
            .map(Category::getCategoryTitle) // Assuming Category entity has a 'name' field
            .collect(Collectors.toList());

        List<String> existingFaculties = user.getFacult() != null ? user.getFacult() : new ArrayList<>();
        existingFaculties.addAll(categoryNames);
        
        user.setFacult(existingFaculties);  // Update the user's faculties

              Payment newPayment = paymentRepo.save(payment);
        return modelMapper.map(newPayment, PaymentDto.class);
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
}
