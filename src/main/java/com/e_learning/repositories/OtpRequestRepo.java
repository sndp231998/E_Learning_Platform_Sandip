package com.e_learning.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e_learning.entities.OtpRequest;
public interface OtpRequestRepo extends JpaRepository<OtpRequest, Integer>{

	 List<OtpRequest> findByOtp(String otp);
}
