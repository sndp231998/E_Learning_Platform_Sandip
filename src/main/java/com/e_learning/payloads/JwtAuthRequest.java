package com.e_learning.payloads;

import lombok.Data;

@Data
public class JwtAuthRequest {
      private String username;
	
	private String password;
	
	private String mobilenum;
   
	private String browserInfo;

}
