package com.e_learning;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.e_learning.entities.Category;
import com.e_learning.repositories.CategoryRepo;

@SpringBootTest
class ELearningPlatformApplicationTests {

	@Test
	void contextLoads() {
	}
	
	@Autowired
	CategoryRepo categoryRepo;

	@Test
	public void testFindAllById() {
	    List<Category> categories = categoryRepo.findAllById(Arrays.asList(1, 2));
	    assertNotNull(categories);
	    assertEquals(2, categories.size()); // Adjust based on what you expect
	    
	 // Print the fetched categories
        categories.forEach(category -> {
            System.out.println("Category ID: " + category.getCategoryTitle());
        
            System.out.println("Category Price: " + category.getPrice());
            System.out.println("---------------------------");
        });
	}

}
