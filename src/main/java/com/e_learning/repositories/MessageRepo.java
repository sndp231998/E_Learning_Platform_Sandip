package com.e_learning.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.e_learning.entities.Message;

public interface MessageRepo extends JpaRepository<Message, Long>{

}