package com.e_learning.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_learning.entities.Category;
import com.e_learning.entities.ChildCategory;
import com.e_learning.exceptions.ResourceNotFoundException;
import com.e_learning.payloads.ChildCategoryDto;
import com.e_learning.repositories.CategoryRepo;
import com.e_learning.repositories.ChildCategoryRepo;
import com.e_learning.services.ChildCategoryService;

@Service
public class ChildCategoryServiceImpl implements ChildCategoryService {
	
	@Autowired
	private ChildCategoryRepo childCategoryRepo;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private CategoryRepo categoryRepo;

	@Override
    public ChildCategoryDto createChildCategory(ChildCategoryDto childCategoryDto) {
        Category category = this.categoryRepo.findById(childCategoryDto.getCategory().getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", childCategoryDto.getCategory().getCategoryId()));
                
        ChildCategory childCategory = this.modelMapper.map(childCategoryDto, ChildCategory.class);
        childCategory.setCategory(category);
             
        ChildCategory addedChildCategory = this.childCategoryRepo.save(childCategory); 
        return this.modelMapper.map(addedChildCategory, ChildCategoryDto.class);
    }

	@Override
	public ChildCategoryDto updateChildCategory(ChildCategoryDto childCategoryDto, Integer ch_categoryId) {
		ChildCategory childCategory = this.childCategoryRepo.findById(ch_categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("ChildCategory", "Child Category Id", ch_categoryId));
		
		Category category = this.categoryRepo.findById(childCategoryDto.getCategory().getCategoryId())
				.orElseThrow(() -> new ResourceNotFoundException("Category", "Category Id", childCategoryDto.getCategory().getCategoryId()));
		
		childCategory.setCategoryTitle(childCategoryDto.getCategoryTitle());
		childCategory.setCategoryDescription(childCategoryDto.getCategoryDescription());
		childCategory.setCategory(category);

		ChildCategory updatedChildCategory = this.childCategoryRepo.save(childCategory);

		return this.modelMapper.map(updatedChildCategory, ChildCategoryDto.class);
	}

	@Override
	public void deleteChildCategory(Integer ch_categoryId) {
		ChildCategory childCategory = this.childCategoryRepo.findById(ch_categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("ChildCategory", "Child Category Id", ch_categoryId));
		this.childCategoryRepo.delete(childCategory);
	}

	@Override
	public ChildCategoryDto getChildCategory(Integer ch_categoryId) {
		ChildCategory childCategory = this.childCategoryRepo.findById(ch_categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("ChildCategory", "Child Category Id", ch_categoryId));

		return this.modelMapper.map(childCategory, ChildCategoryDto.class);
	}

	@Override
	public List<ChildCategoryDto> getChildCategories() {
		List<ChildCategory> childCategories = this.childCategoryRepo.findAll();
		List<ChildCategoryDto> childCategoryDtos = childCategories.stream()
				.map((childCategory) -> this.modelMapper.map(childCategory, ChildCategoryDto.class))
				.collect(Collectors.toList());

		return childCategoryDtos;
	}
}
