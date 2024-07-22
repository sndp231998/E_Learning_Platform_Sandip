package com.e_learning.services;

import java.util.List;

import com.e_learning.payloads.ChildCategoryDto;

public interface ChildCategoryService {

	// create
			ChildCategoryDto createChildCategory(ChildCategoryDto childCategoryDto);

			// update
			ChildCategoryDto updateChildCategory(ChildCategoryDto childCategoryDto, Integer ch_categoryId);

			// delete
			void deleteChildCategory(Integer ch_categoryId);

			// get
			ChildCategoryDto getChildCategory(Integer ch_categoryId);

			// get All

			List<ChildCategoryDto> getChildCategories();
}
