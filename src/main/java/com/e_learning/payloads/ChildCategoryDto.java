package com.e_learning.payloads;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ChildCategoryDto {
    private Integer ch_categoryId;
    private String categoryTitle;
    private String categoryDescription;
    
    private CategoryDto category;
}
