package com.e_learning.services;

import java.util.List;

import com.e_learning.entities.Notice;
import com.e_learning.entities.Notice.NoticeType;
import com.e_learning.entities.UserNotice;
import com.e_learning.payloads.NoticeDto;

public interface NoticeService {

    // Create notice for a subscribed user in a specific category
    NoticeDto createNotice(NoticeDto noticeDto, Integer userId, Integer categoryId);

    // Create notice for all users
    NoticeDto createNotice(NoticeDto noticeDto, Integer userId);

    // Get all notices (Admin view)
    List<NoticeDto> getAllNotices(Integer userID);

   

    // Get notices based on user faculty
    List<NoticeDto> getNoticsByUserFaculty(Integer userId, String faculty);

	boolean isNoticeReadByUser(Integer userId, Long noticeId);

	Notice makeNoticeAsRead(Integer userId, Long noticeId);

   
    //make read
 
   // _____________________________________________________________
	//public int countUnreadNoticesByUserId(Integer userId, NoticeType type);
    
}
