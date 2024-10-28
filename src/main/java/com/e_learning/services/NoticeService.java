package com.e_learning.services;

import java.util.List;

import com.e_learning.entities.Notice;
import com.e_learning.payloads.AnswerDto;
import com.e_learning.payloads.NoticeDto;

public interface NoticeService {


//for subscribed user
	NoticeDto createNotice(NoticeDto noticeDto, Integer userId ,Integer categoryId);

	//for all
	NoticeDto createNotice(NoticeDto noticeDto,Integer userId);
	
    List<NoticeDto> getAllNotices();
    
    List<NoticeDto> getNoticesByUserId(Integer userId);

   boolean hasUserReadNotice(Integer userId, Long noticeId);
//
//    List<NoticeDto> getAllNoticesForUsers();

    List<NoticeDto> getNoticsByUserFaculty(Integer userId, String faculty);
 
}
