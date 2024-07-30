package com.e_learning.services.impl;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Random;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.e_learning.entities.OtpRequest;
import com.e_learning.repositories.OtpRequestRepo;
import com.e_learning.services.OtpRequestService;

@Service
public class OtpRequestServiceImpl implements OtpRequestService {
    private static final Logger logger = LoggerFactory.getLogger(OtpRequestServiceImpl.class);

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private OtpRequestRepo otpRequestRepo;

    @Autowired
    private RestTemplate restTemplate;

    private static final String SMS_API_URL = "https://sms.aakashsms.com/sms/v3/send";
    private static final String SMS_API_TOKEN = "e8d63c5cb1c2e22408180c4ce72cf28471fecd48bb27e0106910ddf6cad8243a";

    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public void sendOtpSms(String mobileNo, String otp) {
        String url = String.format("%s?auth_token=%s&to=%s&text=Your OTP: %s",
                SMS_API_URL, SMS_API_TOKEN, mobileNo, otp);

        logger.info("Sending OTP to mobile number: {}", mobileNo);
        logger.info("Generated OTP: {}", otp);
        logger.info("Final URL: {}", url);

        String response = restTemplate.getForObject(url, String.class);
        logger.info("Response from SMS API: {}", response);
    }

    @Override
    public OtpRequest createOtp(OtpRequest otpReq) {
        if (otpReq.getMobileNo() == null || otpReq.getMobileNo().isEmpty()) {
            throw new IllegalArgumentException("Phone number is required");
        }
        SendOtp(otpReq, otpReq.getMobileNo());
        return otpRequestRepo.save(otpReq);
    }

    @Override
    public OtpRequest SendOtp(OtpRequest otpReq, String phnumber) {
        String ph = otpReq.getMobileNo();
        String otp = generateOtp();
        otpReq.setOtp(otp);
        sendOtpSms(ph, otp);
        Instant otpValidUntilInstant = Instant.now().plus(10, ChronoUnit.MINUTES);
        LocalDateTime otpValidUntil = LocalDateTime.ofInstant(otpValidUntilInstant, ZoneId.systemDefault());
        otpReq.setOtpValidUntil(otpValidUntil); // OTP valid for 10 minutes
        
        return otpRequestRepo.save(otpReq);
    }

}
