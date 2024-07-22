package com.e_learning.Controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.e_learning.payloads.ApiResponse;
import com.e_learning.payloads.ChildCategoryDto;
import com.e_learning.services.ChildCategoryService;

@RestController
@RequestMapping("/api/v1/childcategories")
public class ChildCategoryController {

    @Autowired
    private ChildCategoryService childCategoryService;

    // Create
    @PostMapping("/")
    public ResponseEntity<ChildCategoryDto> createChildCategory(@Valid @RequestBody ChildCategoryDto childCategoryDto) {
        ChildCategoryDto createCategory = this.childCategoryService.createChildCategory(childCategoryDto);
        return new ResponseEntity<>(createCategory, HttpStatus.CREATED);
    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<ChildCategoryDto> updateChildCategory(@Valid @RequestBody ChildCategoryDto childCategoryDto,
                                                                @PathVariable Integer id) {
        ChildCategoryDto updatedCategory = this.childCategoryService.updateChildCategory(childCategoryDto, id);
        return ResponseEntity.ok(updatedCategory);
    }


    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteChildCategory(@PathVariable Integer id) {
        this.childCategoryService.deleteChildCategory(id);
        return new ResponseEntity<ApiResponse>(new ApiResponse("ChildCAtegory is deleted successdully !!",true),
        		HttpStatus.OK);
    }

    // Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<ChildCategoryDto> getChildCategory(@PathVariable Integer id) {
        ChildCategoryDto category = this.childCategoryService.getChildCategory(id);
        return ResponseEntity.ok(category);
    }

    // Get all
    @GetMapping("/")
    public ResponseEntity<List<ChildCategoryDto>> getChildCategories() {
        List<ChildCategoryDto> categories = this.childCategoryService.getChildCategories();
        return ResponseEntity.ok(categories);
    }
}
