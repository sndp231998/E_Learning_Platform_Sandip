package com.e_learning.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="categories")
@NoArgsConstructor
@Data
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer categoryId;
	
	@Column(name="title", length = 100, nullable = false)
	private String categoryTitle;
	
	
	@Column(name="description",length=1000)
	private String categoryDescription;
	
	//@NotBlank(message = "mainis required")
	private String mainCategory;
	
	 private LocalDateTime courseValidDate;
	 
	 private LocalDateTime addedDate;

	 @NotBlank(message = "subject price is required")
	private String price;
	
	private String imageName;
	
	private String videoLink;
	
	@NotBlank(message = "cource type  is required")
	private String categoryType;
	
	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Post> posts = new ArrayList<>();
	
	
	
	 @Override
	    public String toString() {
	        return "Category{" +
	               "categoryId=" + categoryId +
	               ", categoryTitle='" + categoryTitle + '\'' +
	               ", price='" + price + '\'' +
	               // Exclude posts to prevent circular reference
	               '}';
	    }
}
