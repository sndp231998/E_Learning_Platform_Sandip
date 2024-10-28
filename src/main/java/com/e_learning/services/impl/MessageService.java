package com.e_learning.services.impl;

import org.springframework.stereotype.Service;

import com.e_learning.entities.Message;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageService {
    private final List<Message> chatHistory = new ArrayList<>();

    public void saveMessage(Message message) {
        chatHistory.add(message);
    }

    public List<Message> getChatHistory() {
        return new ArrayList<>(chatHistory); // Return a copy of the history
    }
}
