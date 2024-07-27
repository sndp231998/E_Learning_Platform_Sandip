package com.e_learning.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_learning.entities.Category;

import com.e_learning.exceptions.ResourceNotFoundException;
import com.e_learning.payloads.CategoryDto;

import com.e_learning.repositories.CategoryRepo;
import com.e_learning.services.CategoryService;



@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepo categoryRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public CategoryDto createCategory(CategoryDto categoryDto) {
		Category cat = this.modelMapper.map(categoryDto, Category.class);
		Category addedCat = this.categoryRepo.save(cat);
		return this.modelMapper.map(addedCat, CategoryDto.class);
	}

	@Override
	public CategoryDto updateCategory(CategoryDto categoryDto, Integer categoryId) {

		Category cat = this.categoryRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category ", "Category Id", categoryId));

		cat.setCategoryTitle(categoryDto.getCategoryTitle());
		cat.setCategoryDescription(categoryDto.getCategoryDescription());

		Category updatedcat = this.categoryRepo.save(cat);

		return this.modelMapper.map(updatedcat, CategoryDto.class);
	}

	
	//Delete Category
	@Override
	public void deleteCategory(Integer categoryId) {

		Category cat = this.categoryRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category ", "category id", categoryId));
		this.categoryRepo.delete(cat);
	}

	
	//GetCAtegory BY Id
	@Override
	public CategoryDto getCategory(Integer categoryId) {
		Category cat = this.categoryRepo.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "category id", categoryId));

		return this.modelMapper.map(cat, CategoryDto.class);
	}

	//GetAll CAtegory
	@Override
	public List<CategoryDto> getCategories() {

		List<Category> categories = this.categoryRepo.findAll();
		List<CategoryDto> catDtos = categories.stream().map((cat) -> this.modelMapper.map(cat, CategoryDto.class))
				.collect(Collectors.toList());

		return catDtos;
	}

	
//	@Override
//    public List<UserDto> getUsersByCollegeName(String collegename) {
//        List<User> users = userRepo.findByCollegename(collegename);
//        return users.stream()
//                    .map(user -> modelMapper.map(user, UserDto.class))
//                    .collect(Collectors.toList());
//    }
//	@Override
//	public List<CategoryDto> getUsersByCategoryTitle(String title) {
//		List<Category>categorys
//		return null;
//	}

}
