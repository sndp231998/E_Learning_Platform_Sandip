package com.e_learning;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.e_learning.config.AppConstants;
import com.e_learning.entities.Role;
import com.e_learning.repositories.RoleRepo;

@SpringBootApplication
public class ELearningPlatformApplication implements CommandLineRunner {

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private RoleRepo roleRepo;
	
	public static void main(String[] args) {
		SpringApplication.run(ELearningPlatformApplication.class, args);
	}
	
	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
	
	
	@Override
	public void run(String... args) throws Exception {
		System.out.println(this.passwordEncoder.encode("xyz"));

		try {
			
			  Role role1 = new Role();
		        role1.setId(AppConstants.NORMAL_USER);
		        role1.setName("ROLE_NORMAL");
		        
	        Role role = new Role();
	        role.setId(AppConstants.ADMIN_USER);
	        role.setName("ROLE_ADMIN");

	      
	        Role role2 = new Role();
	        role2.setId(AppConstants.SUBSCRIBED_USER);
	        role2.setName("ROLE_SUBSCRIBED");

	        
	        Role role3=new Role();
	        role3.setId(AppConstants.TEACHER_USER);
	        role3.setName("Role_TEACHER");
	        
	        
	        List<Role> roles = List.of(role, role1, role2 , role3);

	        List<Role> result = this.roleRepo.saveAll(roles);

	        result.forEach(r -> {
	            System.out.println(r.getName());
	        });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
