package com.e_learning.payloads;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;



import com.e_learning.entities.User;

import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
public class ForgetPasswordDto {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer fid;
	
	 private String phnum;
	private LocalDateTime  date;
	
	private String otp;
	private String newPassword;

	    private UserDto user;
}
