package com.e_learning.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_learning.entities.Answer;
import com.e_learning.entities.Notification;
import com.e_learning.entities.User;
import com.e_learning.exceptions.ResourceNotFoundException;
import com.e_learning.payloads.AnswerDto;
import com.e_learning.payloads.NotificationDto;
import com.e_learning.repositories.NotificationRepo;
import com.e_learning.repositories.UserRepo;
import com.e_learning.services.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;

    
  //call from other class
    @Override
    public void createNotification(Integer userId, String message) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notificationRepo.save(notification);
    }
    
    @Override
    public NotificationDto createNotification(Integer userId, NotificationDto notificationDto) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        Notification notification = this.modelMapper.map(notificationDto, Notification.class);
       
        notification.setUser(user);
        notification.setMessage(notificationDto.getMessage());
        Notification not=this.notificationRepo.save(notification);
        NotificationDto savedAotDto = this.modelMapper.map(not, NotificationDto.class);
        
        return savedAotDto;
    }

    
    
    
    
    
    @Override
    public List<Notification> getUnreadNotificationsForUser(Integer userId) {
        return notificationRepo.findByUserIdAndIsReadFalse(userId);
    }

    @Override
    public void markNotificationsAsRead(Integer userId) {
        List<Notification> notifications = notificationRepo.findByUserId(userId);
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepo.saveAll(notifications);
    }


   
    @Override
    public List<NotificationDto> getAllNotificationsForUser(Integer userId) {
        return notificationRepo.findByUserId(userId)
                .stream()
                .map(notification -> modelMapper.map(notification, NotificationDto.class))
                .collect(Collectors.toList());
    }

 // New method to create exam score notification
   @Override
    public void notifyExamScore(Integer userId, String examTitle, double score) {
        String message = String.format("You have received a score of %.2f for the exam: %s", score, examTitle);
        createNotification(userId, message);
    }


	
}