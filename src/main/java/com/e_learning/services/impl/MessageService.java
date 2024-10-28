package com.e_learning.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_learning.entities.Message;
import com.e_learning.repositories.MessageRepo;

import java.util.List;


@Service
public class MessageService {

    @Autowired
    private MessageRepo messageRepo;

    // Save a message to the database
    public void saveMessage(Message message) {
        messageRepo.save(message);
    }

    // Retrieve all messages from the database as chat history
    public List<Message> getChatHistory() {
        return messageRepo.findAll(); // Retrieve chat history from DB
    }
}
