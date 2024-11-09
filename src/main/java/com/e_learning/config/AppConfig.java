package com.e_learning.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
//     @Bean
//     public CommonsMultipartResolver multipartResolver() {
//    	 CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
//    	 multipartResolver.setMaxUploadSize(10485760); // 10MB return multipartResolver;
//		return multipartResolver;
//     }
}
