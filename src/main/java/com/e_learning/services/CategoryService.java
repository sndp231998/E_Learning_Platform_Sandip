package com.e_learning.services;

import java.util.List;

import com.e_learning.payloads.CategoryDto;

public interface CategoryService {

	// create
		CategoryDto createCategory(CategoryDto categoryDto);

		// update
		CategoryDto updateCategory(CategoryDto categoryDto, Integer categoryId);

		// delete
		void deleteCategory(Integer categoryId);

		// get
		CategoryDto getCategory(Integer categoryId);

		// get All

		List<CategoryDto> getCategories();
		
		//List<CategoryDto> getUsersByCategoryTitle(String title); 

}
