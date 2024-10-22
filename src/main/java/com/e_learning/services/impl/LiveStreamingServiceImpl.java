package com.e_learning.services.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.e_learning.entities.Category;
import com.e_learning.entities.LiveStreaming;
import com.e_learning.entities.Post;
import com.e_learning.entities.User;
import com.e_learning.exceptions.ResourceNotFoundException;
import com.e_learning.payloads.LiveStreamingDto;
import com.e_learning.payloads.PostDto;
import com.e_learning.payloads.UserDto;
import com.e_learning.repositories.CategoryRepo;
import com.e_learning.repositories.LiveStreamingRepo;
import com.e_learning.repositories.UserRepo;
import com.e_learning.services.LiveStreamingService;

@Service
public class LiveStreamingServiceImpl implements LiveStreamingService {

    @Autowired
    private LiveStreamingRepo liveRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Override
    public LiveStreamingDto createLiveStreaming(LiveStreamingDto liveDto, Integer userId, Integer categoryId) {
        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "User id", userId));

        Category category = this.categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "category id", categoryId));

        LiveStreaming live = this.modelMapper.map(liveDto, LiveStreaming.class);
        live.setTitle(liveDto.getTitle());
        live.setStartingTime(liveDto.getStartingTime());
        live.setStreamlink(liveDto.getStreamlink());
        live.setUser(user);
        live.setCategory(category);

        LiveStreaming newLive = this.liveRepo.save(live);

        return this.modelMapper.map(newLive, LiveStreamingDto.class);
    }

    @Override
    public List<LiveStreamingDto> getLiveStreamingByCategory(Integer categoryId) {
        Category category = this.categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "category id", categoryId));

        List<LiveStreaming> lives = this.liveRepo.findByCategory(category);
 if(lives.isEmpty()) {
	 throw new ResourceNotFoundException("live streaming","category id",categoryId);
 }
        List<LiveStreamingDto> liveDtos = lives.stream()
                .map(live -> this.modelMapper.map(live, LiveStreamingDto.class))
                .collect(Collectors.toList());

        return liveDtos;
    }

    @Override
    public List<LiveStreamingDto> getLiveStreamingsByUserFaculty(Integer userId) {
        // Retrieve user by ID
        User user = this.userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        // Get the user's faculty
        String userFaculty = user.getFaculty();

        // Find the category that matches the user's faculty
        Category category = this.categoryRepo.findByCategoryTitle(userFaculty);
        if (category == null) {
            throw new ResourceNotFoundException("Category", "title", userFaculty);
        }
        // Fetch live streams associated with the category
        List<LiveStreaming> lives = this.liveRepo.findByCategory(category);

        // Convert live streams to LiveStreamingDto
        List<LiveStreamingDto> liveDtos = lives.stream()
                .map(live -> this.modelMapper.map(live, LiveStreamingDto.class))
                .collect(Collectors.toList());

        return liveDtos;
    }
    public LiveStreaming dtoToLiveStreaming(LiveStreamingDto liveStreamingDto) {
        return this.modelMapper.map(liveStreamingDto, LiveStreaming.class);
    }

    public LiveStreamingDto LiveStreamingToDto(LiveStreaming liveStreaming) {
        return this.modelMapper.map(liveStreaming, LiveStreamingDto.class);
    }

    @Override
    public List<LiveStreamingDto> getAllLives() {
        List<LiveStreaming> lives = liveRepo.findAll();
        return lives.stream()
                    .map(this::LiveStreamingToDto) // Ensure this method returns LiveStreamingDto
                    .collect(Collectors.toList());
    }

    @Override
    public LiveStreamingDto updateLiveStreaming(LiveStreamingDto liveDto, Integer liveId) {
        // Check if liveId exists
        LiveStreaming live = this.liveRepo.findById(liveId)
                .orElseThrow(() -> new ResourceNotFoundException("live ", "live id", liveId));
        
        // Set fields from the DTO to the entity
        live.setStartingTime(liveDto.getStartingTime());
        live.setStreamlink(liveDto.getStreamlink());
        live.setTitle(liveDto.getTitle());

        // Check if category is provided before setting it
        if (liveDto.getCategory() != null && liveDto.getCategory().getCategoryId() != null) {
            Category category = this.categoryRepo.findById(liveDto.getCategory().getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", liveDto.getCategory().getCategoryId()));
            live.setCategory(category);
        }

        // Save the updated entity
        LiveStreaming updatedLive = this.liveRepo.save(live);
        return this.modelMapper.map(updatedLive, LiveStreamingDto.class);
    }

	@Override
	public void deleteLiveStreaming(Integer liveId) {
		LiveStreaming live = this.liveRepo.findById(liveId)
                .orElseThrow(() -> new ResourceNotFoundException("Liv ", "live id", liveId));

        this.liveRepo.delete(live);
		
	}


}
