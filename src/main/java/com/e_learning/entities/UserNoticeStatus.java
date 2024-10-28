package com.e_learning.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_notice_status")
@Data
@NoArgsConstructor
public class UserNoticeStatus {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @ManyToOne
	    @JoinColumn(name = "user_id", nullable = false)
	    private User user;

	    @ManyToOne
	    @JoinColumn(name = "notice_id", nullable = false)
	    private Notice notice;

	    @Column(nullable = false)
	    private Boolean readStatus = false; // false for unread, true for read

	    private LocalDateTime readDate; // Optional: timestamp when the notice was read
	}

