package com.e_learning.payloads;



import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class PostDto {

	private Integer postId;
	
	private String title;
	
	private String content;
	
	private String imageName;
	
	private String videoLink;
	
	private Date addedDate;	
	
	private CategoryDto category;

	private UserDto user;
	
	//private Set<CommentDto> comments=new HashSet<>();


}
