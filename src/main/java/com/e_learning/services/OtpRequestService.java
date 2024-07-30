package com.e_learning.services;

import com.e_learning.entities.OtpRequest;


public interface OtpRequestService {

	//create
			OtpRequest createOtp(OtpRequest otpReq);
			
			//ph num
			 OtpRequest SendOtp(OtpRequest otpReq, String phnumber);
			
}
