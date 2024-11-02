package com.e_learning.services;

import java.util.List;

import com.e_learning.entities.Notification;

public interface NotificationService {

	void createNotification(Integer userId, String message);
	
    List<Notification> getUnreadNotificationsForUser(Integer userId);
    
    void markNotificationsAsRead(Integer userId); // optional if you want read marking

	List<Notification> getAllNotificationsForUser(Integer userId);
}
