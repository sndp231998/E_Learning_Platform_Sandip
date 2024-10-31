package com.e_learning.services;

import java.util.List;

import com.e_learning.payloads.NoticeDto;

public interface NoticeService {

    // Create notice for a subscribed user in a specific category
    NoticeDto createNotice(NoticeDto noticeDto, Integer userId, Integer categoryId);

    // Create notice for all users
    NoticeDto createNotice(NoticeDto noticeDto, Integer userId);

    // Get all notices (Admin view)
    List<NoticeDto> getAllNotices();

    boolean hasUserSeenNotice(Integer userId, Long noticeId);

    // Get notices based on user faculty
    List<NoticeDto> getNoticsByUserFaculty(Integer userId, String faculty);

   

    
}
