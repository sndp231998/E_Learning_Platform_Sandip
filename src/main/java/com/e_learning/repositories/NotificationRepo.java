package com.e_learning.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e_learning.entities.Notification;

public interface NotificationRepo extends JpaRepository<Notification, Integer> {

	 // Retrieve unread notifications for a user
    List<Notification> findByUserIdAndIsReadFalse(Integer userId);

    // Mark all notifications as read for a user (if needed)
    List<Notification> findByUserId(Integer userId);
    

}
