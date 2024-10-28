package com.e_learning.Controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.e_learning.entities.Message;
import com.e_learning.services.FileService;
import com.e_learning.services.impl.MessageService;

@RestController
public class MessageController {

    @Autowired
    private FileService fileService;

    @Autowired
    private MessageService messageService;

    //private static final String UPLOAD_PATH = "/images";

    @Value("${project.image}")
	private String UPLOAD_PATH;
    
    @MessageMapping("/message")
    @SendTo("/topic/return-to")
    public Message getContent(Message message) {
        messageService.saveMessage(message); // Save each message to history
        return message; // Send message to all subscribers
    }

    @MessageMapping("/history")
    @SendTo("/topic/history")
    public List<Message> getChatHistory() {
        return messageService.getChatHistory(); // Retrieve and send chat history
    }

    @PostMapping("/upload-image")
    public Message uploadImage(@RequestParam("file") MultipartFile file, 
                               @RequestParam("name") String name,
                               @RequestParam("content") String content) throws IOException {
        String imageUrl = fileService.uploadFile(UPLOAD_PATH, file); // Upload file and get URL

        // Create a message object with the image URL
        Message message = new Message();
        message.setName(name);
        message.setContent(content);
        message.setImageUrl(imageUrl); // Set image URL in message

        messageService.saveMessage(message); // Save message to history
        return message; // Return the message with image URL
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<InputStreamResource> serveImage(@PathVariable String filename) {
        try {
            InputStream imageStream = fileService.getResource(UPLOAD_PATH, filename); // Retrieve image file
            InputStreamResource resource = new InputStreamResource(imageStream);

            // Determine content type based on file extension
            MediaType mediaType = getMediaTypeForFileName(filename);
            if (mediaType == null) {
                return ResponseEntity.badRequest().build(); // Unsupported media type
            }

            // Set headers and content type for the image response
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .contentType(mediaType)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Helper method to determine MediaType based on filename
    private MediaType getMediaTypeForFileName(String filename) {
        if (filename.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        }
        return null; // Unsupported type
    }
}
