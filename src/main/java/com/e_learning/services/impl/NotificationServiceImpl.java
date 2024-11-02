package com.e_learning.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_learning.entities.Notification;
import com.e_learning.entities.User;
import com.e_learning.exceptions.ResourceNotFoundException;
import com.e_learning.repositories.NotificationRepo;
import com.e_learning.repositories.UserRepo;
import com.e_learning.services.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private UserRepo userRepo;

    @Override
    public void createNotification(Integer userId, String message) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notificationRepo.save(notification);
    }

//    @Override
//    public List<Notification> getUnreadNotificationsForUser(Integer userId) {
//        return notificationRepo.findByUserIdAndIsReadFalse(userId);
//    }

    @Override
    public void markNotificationsAsRead(Integer userId) {
        List<Notification> notifications = notificationRepo.findByUserId(userId);
        notifications.forEach(notification -> notification.setRead(true));
        notificationRepo.saveAll(notifications);
    }

	@Override
	public List<Notification> getUnreadNotificationsForUser(Integer userId) {
		// TODO Auto-generated method stub
		return null;
	}
   
	@Override
	public List<Notification> getAllNotificationsForUser(Integer userId) {
	    return notificationRepo.findByUserId(userId); // Gets both read and unread notifications
	}

    
}