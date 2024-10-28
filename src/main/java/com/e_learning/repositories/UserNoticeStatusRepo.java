package com.e_learning.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e_learning.entities.UserNoticeStatus;

public interface UserNoticeStatusRepo extends JpaRepository<UserNoticeStatus, Long> {
    
    Optional<UserNoticeStatus> findByUser_IdAndNotice_NoticeId(Integer userId, Long noticeId);
    
    List<UserNoticeStatus> findByUser_IdAndReadStatus(Integer userId, Boolean readStatus);
}