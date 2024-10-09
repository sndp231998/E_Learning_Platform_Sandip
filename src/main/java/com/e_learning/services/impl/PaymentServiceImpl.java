package com.e_learning.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.LoggerFactory;
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
public class PaymentServiceImpl implements PaymentService{

	@Autowired
    private PaymentRepo paymentRepo;
	@Autowired
	private CategoryRepo categoryRepo;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepo userRepo;
    
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);
    
    @Override
    public PaymentDto createPayment(PaymentDto paymentDto, Integer userId, List<Integer> categoryIds) {
        // Fetch the user
        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "User id", userId));

        // Check if categoryIds are provided
        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new IllegalArgumentException("Category IDs cannot be null or empty.");
        }
        logger.info("list of category id" + categoryIds);

        // Fetch the selected categories
        List<Category> categories = this.categoryRepo.findAllById(categoryIds);

        // Log fetched categories
        System.out.println("Fetched Categories: " + categories);

        // Check if categories were found
        if (categories.isEmpty()) {
            throw new ResourceNotFoundException("Categories", "Category IDs", categoryIds.toString());
        }

        // Check if user already has purchased any of the requested categories
        List<Category> purchasedCategories = this.paymentRepo.findCategoriesByUserId(userId);
        List<Integer> purchasedCategoryIds = purchasedCategories.stream()
                .map(Category::getCategoryId) // Assuming `getId()` gets the category ID
                .collect(Collectors.toList());

        // Find intersection of requested and purchased categories
        List<Integer> alreadyPurchased = categoryIds.stream()
                .filter(purchasedCategoryIds::contains)
                .collect(Collectors.toList());

        if (!alreadyPurchased.isEmpty()) {
            throw new IllegalArgumentException("User has already purchased categories: " + alreadyPurchased);
        }

        // Calculate total price by summing up the prices of the selected categories
        int totalPrice = categories.stream()
                .mapToInt(category -> {
                    try {
                        // Convert price from String to int
                        return Integer.parseInt(category.getPrice());
                    } catch (NumberFormatException e) {
                        // Handle invalid price format
                        throw new IllegalArgumentException("Invalid price format for category: " + category.getCategoryId());
                    }
                })
                .sum();

        // Map paymentDto to Payment entity
        Payment payment = this.modelMapper.map(paymentDto, Payment.class);

        // Set user and total price
        payment.setUser(user);
        payment.setTotalPrice(totalPrice); // Set the dynamically calculated total price
        payment.setAddedDate(LocalDateTime.now());
        payment.setPayment_screensort(""); // Add payment screenshot logic as required

        // Save the payment
        Payment newPayment = this.paymentRepo.save(payment);

        // Return the PaymentDto mapped from the newly created payment entity
        return this.modelMapper.map(newPayment, PaymentDto.class);
    }
	@Override
	public List<PaymentDto> getAllPayments() {
		List<Payment> pay = this.paymentRepo.findAll();
		List<PaymentDto> payDtos = pay.stream().map((exa) -> this.modelMapper.map(exa, PaymentDto.class))
				.collect(Collectors.toList());

		return payDtos;
	}

}
