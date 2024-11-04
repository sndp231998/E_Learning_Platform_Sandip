package com.e_learning.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.e_learning.entities.Notification;
import com.e_learning.services.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Endpoint to get all notifications for a user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getAllNotificationForUser(@PathVariable Integer userId) {
        List<Notification> notifications = notificationService.getUnreadNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }

    // Endpoint to get only unread notifications for a user
 // Endpoint to get only unread notifications for a user
 // Endpoint to get only unread notifications for a user
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotificationsForUser(@PathVariable Integer userId) {
        List<Notification> unreadNotifications = notificationService.getUnreadNotificationsForUser(userId);
        return ResponseEntity.ok(unreadNotifications);
    }



    // Endpoint to mark all notifications as read for a user
    @PostMapping("/user/{userId}/mark-read")
    public ResponseEntity<Void> markNotificationsAsRead(@PathVariable Integer userId) {
        notificationService.markNotificationsAsRead(userId);
        return ResponseEntity.ok().build();
    }

    // Endpoint to get the count of unread notifications for a user
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Long> getUnreadNotificationCount(@PathVariable Integer userId) {
        long unreadCount = notificationService.getUnreadNotificationsForUser(userId).stream().count();
        return ResponseEntity.ok(unreadCount);
    }
    
 // Endpoint to get all notifications for a user, including read and unread
    @GetMapping("/user/{userId}/all")
    public ResponseEntity<List<Notification>> getAllNotificationsForUser(@PathVariable Integer userId) {
        List<Notification> notifications = notificationService.getAllNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }

}

