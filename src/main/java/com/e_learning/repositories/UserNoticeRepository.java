package com.e_learning.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.e_learning.entities.Notice;
import com.e_learning.entities.User;
import com.e_learning.entities.UserNotice;

public interface UserNoticeRepository extends JpaRepository<UserNotice, Long> {
	
	boolean existsByUserAndNotice(User user, Notice notice);

	//Optional<UserNotice> findByUser_IdAndNotice_Id(Integer userId, Long noticeId);
	Optional<UserNotice> findByUser_IdAndNotice_NoticeId(Integer userId, Long noticeId);

	 @Query("SELECT COUNT(un) FROM UserNotice un WHERE un.user.id = :userId AND un.isRead = false AND un.notice.noticeType = :noticeType")
	    int countUnreadNoticesByUserIdAndNoticeType(@Param("userId") Integer userId, @Param("noticeType") String noticeType);
}