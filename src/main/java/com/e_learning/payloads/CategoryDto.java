package com.e_learning.payloads;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Data
public class CategoryDto {
    private Integer categoryId;

    @NotBlank
    @Size(min = 4, message = "Min size of category title is 4")
    private String categoryTitle;

    private String categoryDescription;
}
