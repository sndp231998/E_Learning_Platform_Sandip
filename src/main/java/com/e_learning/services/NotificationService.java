package com.e_learning.services;

import java.util.List;

import com.e_learning.entities.Notification;
import com.e_learning.payloads.NotificationDto;

public interface NotificationService {

	void createNotification(Integer userId, String message);
	
    List<Notification> getUnreadNotificationsForUser(Integer userId);
    
    void markNotificationsAsRead(Integer userId); // optional if you want read marking

	List<NotificationDto> getAllNotificationsForUser(Integer userId);

	NotificationDto createNotification(Integer userId, NotificationDto notificationDto);


	void notifyExamScore(Integer userId, String examTitle, double score);

	
}
