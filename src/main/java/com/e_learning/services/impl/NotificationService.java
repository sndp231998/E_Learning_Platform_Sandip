package com.e_learning.services.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendNotification(String user, String message) {
        messagingTemplate.convertAndSendToUser(user, "/topic/notifications", message);
    }
}

