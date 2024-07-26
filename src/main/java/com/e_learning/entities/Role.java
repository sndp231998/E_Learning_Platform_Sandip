package com.e_learning.entities;




import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
@Entity
@Data
public class Role {
	@Id	
	private int id;
	
	private String name;
	
	//private LocalDateTime date_Of_Role_Changed;
	
}
