package com.e_learning.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.e_learning.entities.Category;
import com.e_learning.entities.Exam;
import com.e_learning.entities.Notice;
import com.e_learning.payloads.NoticeDto;

@Repository
public interface NoticeRepo extends JpaRepository<Notice, Long> {

	List<Notice> findByUser_Id(Integer userId);
	List<Notice> findByCategory(Category category);
	
	
	 List<Notice> findByNoticeType(Notice.NoticeType noticeType);
}